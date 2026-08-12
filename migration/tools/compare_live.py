"""가동 중인 레거시와 신규 화면을 실제로 받아 대조한다.

정합성 게이트는 템플릿 원본까지만 본다. 값이 서버에서 채워지는 자리는 «dyn» 으로
넘기므로 **표에 찍히는 실제 문자열**은 보지 못한다. 날짜 형식 하나가 어긋나도 게이트는
통과한다 — 실제로 그렇게 놓쳤다(D-020).

두 앱을 띄워 같은 계정으로 로그인하고, 같은 화면을 받아 표 내용을 견준다.
레거시가 살아 있어야 성립하는 검증이다.

준비
    1. 레거시  migration/legacy-runtime 에서 WAR 를 만들어 Tomcat 에 올린다
    2. 신규    migration/app 에서 bootRun
    3. 두 앱이 같은 스키마를 보게 한다. 그래야 데이터 차이가 아니라 화면 차이만 남는다

환경변수
    ERFLOW_LEGACY_BASE   기본 http://localhost:19090/ERFlow
    ERFLOW_NEW_BASE      기본 http://localhost:18080
    ERFLOW_TEST_ID       비교에 쓸 계정
    ERFLOW_TEST_PASSWORD

사용법::

    python migration/tools/compare_live.py

exit code 는 게이트 규약을 따른다(0 PASS / 1 FAIL / 2 ERROR).
"""

from __future__ import annotations

import http.cookiejar
import os
import re
import sys
import urllib.error
import urllib.parse
import urllib.request

LEGACY = os.environ.get("ERFLOW_LEGACY_BASE", "http://localhost:19090/ERFlow")
NEW = os.environ.get("ERFLOW_NEW_BASE", "http://localhost:18080")

#: (이름, 레거시 경로, 신규 경로, 비교 방식)
#:
#: 방식이 여럿인 이유가 있다. 목록 화면은 표를 견주면 되지만, 게시글 보기는 표가
#: 아니다. 값이 <div> 에 흩어져 있어 표 비교로는 아무것도 잡지 못한다.
SCREENS = (
    ("생산 설비 관리", "/unit/unitList.jsp", "/unit/list", "rows"),
    ("사용자 찾기 팝업", "/findUser.jsp", "/find/user", "rows"),
    ("사용자 찾기 팝업 (한 명)", "/findEachUser.jsp", "/find/each-user", "rows"),
    # 검색어를 비운 채 누른 것으로 열어야 표가 나온다. 파라미터가 없으면 도움말이다
    ("여러 제품 찾기 팝업", "/findMultiProduct.jsp?search=", "/find/multi-product?search=", "rows"),
    ("협력업체 관리 (구매)", "/company/companyList.jsp?flag=1", "/company/list?flag=1", "rows"),
    ("협력업체 관리 (영업)", "/company/companyList.jsp?flag=0", "/company/list?flag=0", "rows"),
    ("게시판 목록", "/post/boardList.jsp", "/post/board-list", "rows"),
    ("게시글 목록 (공지사항)", "/post/postList.jsp?boardId=1", "/post/list?boardId=1", "rows"),
    ("게시글 목록 (자유게시판)", "/post/postList.jsp?boardId=2", "/post/list?boardId=2", "rows"),
    ("게시글 보기", "/post/postView.jsp?boardId=2&id={post}", "/post/view?boardId=2&id={post}", "blocks"),
    ("글쓰기", "/post/postRegister.jsp?boardId=2", "/post/register?boardId=2", "readonly"),
    ("답변쓰기", "/post/postReply.jsp?boardId=2&postId={post}", "/post/reply?boardId=2&postId={post}", "readonly"),
)

#: 조회수를 올리지 않게 만드는 쿠키 값. 어떤 글 번호와도 겹치지 않으면 된다.
#: D-029 의 뒤집힌 조건 때문에 "이미 본 목록에 없는 글"은 세지 않는다.
_NO_COUNT_COOKIE = "0"

#: 게시글 보기에서 견줄 영역. 레거시와 신규가 같은 class 를 쓴다.
_VIEW_BLOCKS = ("subject-center", "left-subject", "right-subject", "content-body")

_TAG = re.compile(r"<[^>]+>")
_CSRF = re.compile(r'name="_csrf"\s+value="([^"]*)"')


def opener() -> urllib.request.OpenerDirector:
    """쿠키를 담는 오프너. 세션이 유지되어야 화면을 받을 수 있다."""
    jar = http.cookiejar.CookieJar()
    client = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))
    client.jar = jar
    return client


def set_cookie(client, name: str, value: str) -> None:
    """쿠키를 심는다. 조회수 집계를 통제하려면 필요하다.

    레거시 판정이 뒤집혀 있어서(D-029) `postId` 쿠키에 지금 글 번호가 **없으면**
    조회수가 오르지 않는다. 그 성질을 이용해 화면을 여러 번 받아도 값이 흔들리지
    않게 만든다.
    """
    for host in ("localhost", "localhost.local"):
        client.jar.set_cookie(http.cookiejar.Cookie(
            version=0, name=name, value=value, port=None, port_specified=False,
            domain=host, domain_specified=True, domain_initial_dot=False,
            path="/", path_specified=True, secure=False, expires=None,
            discard=False, comment=None, comment_url=None, rest={}))


#: 권한이 없을 때 두 앱이 보내는 곳. 경로는 다르지만 뜻은 같다.
_DENIED = re.compile(r"permissionError|/permission-error|accessError|/access-error")


def fetch(client, url: str, data: dict | None = None) -> str:
    body = urllib.parse.urlencode(data).encode() if data else None
    with client.open(url, body, timeout=30) as response:
        return response.read().decode("utf-8", errors="replace")


def fetch_or_denial(client, url: str) -> tuple[str, bool]:
    """화면을 받는다. 권한으로 막히면 그것도 결과다.

    막힌 것을 오류로 처리하면 "둘 다 막혔다"를 확인할 수 없다. 그런데 그것이야말로
    확인해야 하는 것이다 — 레거시에서 막히던 사람이 신규에서 통과하면 이관 사고다.

    @return (본문, 막혔는지)
    """
    try:
        with client.open(url, None, timeout=30) as response:
            body = response.read().decode("utf-8", errors="replace")
            # 레거시는 200 으로 permissionError.jsp 를 그려 보낸다.
            final = response.geturl()
            return body, bool(_DENIED.search(final))
    except urllib.error.HTTPError as error:
        if error.code in (401, 403):
            return "", True
        raise


def login(client, base: str, path: str, account: str, password: str, csrf: bool) -> None:
    """로그인한다. 신규 앱은 CSRF 토큰을 요구한다(레거시에는 없던 방어다)."""
    form = {"id": account, "password": password}
    if csrf:
        page = fetch(client, f"{base}/login")
        token = _CSRF.search(page)
        if token:
            form["_csrf"] = token.group(1)
    fetch(client, f"{base}{path}", form)


def _text(chunk: str) -> str:
    """태그를 걷어내고 공백을 하나로 줄인다."""
    bare = _TAG.sub(" ", chunk).replace("&nbsp;", " ").replace("&gt;", ">").replace("&lt;", "<")
    return " ".join(bare.split())


def table_rows(html: str) -> list[list[str]]:
    """표의 데이터 행을 글자만 남겨 뽑는다.

    `<tbody>` 가 있으면 그 안을 본다. 없으면 표 전체를 보되 헤더(`<th>`)만 있는
    행은 걸러진다 — `<td>` 가 없는 행은 셀 목록이 비기 때문이다.
    게시글 목록이 `<tbody>` 없이 `<tr>` 을 늘어놓는다.
    """
    start, end = html.find("<tbody"), html.find("</tbody>")
    if start < 0 or end < 0:
        start = html.find("<table")
        end = html.find("</table>")
    if start < 0 or end < 0:
        return []
    rows = []
    for chunk in re.split(r"<tr[^>]*>", html[start:end])[1:]:
        # 셀 안쪽 공백까지 하나로 줄인다. 줄바꿈 문자가 무엇이든, 들여쓰기가
        # 탭이든 공백이든 화면에는 드러나지 않는다. 그것을 차이로 보고하면
        # 진짜 차이가 소음에 묻힌다.
        cells = [
            _text(cell) for cell in re.findall(r"<td[^>]*>(.*?)</td>", chunk, re.S)
        ]
        if cells:
            rows.append(cells)
    return rows


def _element_text(html, opening):
    """여는 태그부터 짝이 맞는 닫는 태그까지의 글자를 뽑는다.

    같은 태그가 안에 또 들어 있으므로 깊이를 센다. 정규식 backreference 로 첫
    닫는 태그를 찾으면 안쪽만 잘라 온다 — 게시글 보기의 right-subject 는 안에
    div 가 여럿이라 그러면 조회수도 댓글수도 놓친다.

    @param html 전체 문서
    @param opening 여는 태그 match
    @return 그 요소 안쪽 글자
    """
    tag = opening.group(1)
    depth = 0
    cursor = opening.start()
    step_pattern = re.compile("<(/?)" + tag + "(?![-\w])[^>]*?(/?)>", re.I)
    while True:
        step = step_pattern.search(html, cursor)
        if step is None:
            return _text(html[opening.end():])
        cursor = step.end()
        if step.group(2) == "/":
            continue
        depth += -1 if step.group(1) else 1
        if depth == 0:
            return _text(html[opening.end():step.start()])


def blocks(html):
    """지정한 class 를 가진 영역의 글자를 순서대로 뽑는다.

    표가 아닌 화면을 견주기 위한 것이다. 게시글 보기가 그렇다 — 작성자·작성일·
    조회수·댓글이 전부 div 안에 흩어져 있다.

    @param html 받은 문서
    @return "영역이름: 글자" 목록
    """
    found = []
    for name in _VIEW_BLOCKS:
        opener = re.compile(
            r'<(\w+)[^>]*class="[^"]*(?<![\w-])' + re.escape(name)
            + r'(?![\w-])[^"]*"[^>]*>')
        for opening in opener.finditer(html):
            text = _element_text(html, opening)
            if text:
                found.append(name + ": " + text)
    return found


def readonly_values(html: str) -> list[str]:
    """읽기 전용 입력에 박힌 값. 화면에 글자로 뜨지만 표에는 없다.

    글쓰기 화면의 게시판 이름이 여기 해당한다. 레거시가 "자유게시판"을 박아
    두었는데(D-032) 게이트는 value 를 라벨로 보지 않아 이 차이를 못 본다.
    """
    values = []
    for tag in re.findall(r"<input[^>]*>", html):
        if "readonly" not in tag:
            continue
        value = re.search(r'value="([^"]*)"', tag)
        values.append(value.group(1) if value else "")
    return values


def probe_ids(client) -> dict[str, str]:
    """비교에 쓸 글번호를 레거시 목록에서 직접 읽는다.

    번호를 코드에 박으면 데이터가 바뀌는 순간 조용히 엉뚱한 글을 견주게 된다.
    """
    ids = {}
    for key, board in (("post", 2), ("notice", 1)):
        html = fetch(client, f"{LEGACY}/post/postList.jsp?boardId={board}")
        found = re.search(r'class="post-reader"\s+data-value="(\d+)"', html)
        ids[key] = found.group(1) if found else "0"
    return ids


def view_count(client, base: str, path: str) -> int:
    """화면에 찍힌 조회수를 읽는다.

    @param client 오프너
    @param base 앱 주소
    @param path 게시글 보기 경로
    @return 조회수. 못 읽으면 -1
    """
    html, denied = fetch_or_denial(client, base + path)
    if denied:
        return -1
    for line in blocks(html):
        found = re.search(r"조회수:\s*(\d+)", line)
        if found:
            return int(found.group(1))
    return -1


def check_view_counting(legacy, new, post_id: str) -> int:
    """조회수를 올리는 규칙이 같은지 본다.

    **게이트도 화면 대조도 이것을 보지 못한다.** 게이트는 조회수 자리를 «dyn» 으로
    넘기고, 화면 대조는 한 번 찍힌 값만 견준다. 규칙이 다르면 시간이 지나면서
    숫자가 벌어지는데 어느 한 순간만 보면 알 수 없다.

    레거시 규칙은 뒤집혀 있다(D-029) — 이미 본 글만 세고 처음 보는 글은 세지
    않는다. 그 이상한 동작이 양쪽에서 똑같이 재현되는지 확인한다.

    @param legacy 레거시 오프너
    @param new 신규 오프너
    @param post_id 시험할 글번호
    @return exit code
    """
    print()
    print("== 조회수 집계 규칙 ==")
    legacy_path = f"/post/postView.jsp?boardId=2&id={post_id}"
    new_path = f"/post/view?boardId=2&id={post_id}"

    worst = 0
    cases = (
        ("처음 보는 글 (쿠키에 없음)", _NO_COUNT_COOKIE, 0),
        ("이미 본 글 (쿠키에 있음)", post_id, 1),
    )
    for label, cookie, expected in cases:
        deltas, crashed = [], []
        for client, base, path in ((legacy, LEGACY, legacy_path), (new, NEW, new_path)):
            try:
                set_cookie(client, "postId", cookie)
                before = view_count(client, base, path)
                set_cookie(client, "postId", cookie)
                after = view_count(client, base, path)
                deltas.append(after - before if before >= 0 and after >= 0 else None)
                crashed.append(False)
            except urllib.error.HTTPError as error:
                deltas.append(None)
                crashed.append(error.code >= 500)

        if crashed[0] and not crashed[1]:
            # 레거시가 쿠키 값에 ';' 를 넣어 Tomcat 이 거부한다. 이미 본 글을 다시
            # 열면 500 이 난다. 고치기로 한 차이이며 근거를 남겼다.
            print(f"  KNOWN {label}: 레거시 500 (쿠키 값의 ';' — D-033), 신규는 정상")
        elif crashed[1]:
            print(f"  FAIL  {label}: 신규가 500")
            worst = max(worst, 1)
        elif None in deltas:
            print(f"  ERROR {label}: 조회수를 읽지 못했다")
            worst = max(worst, 2)
        elif deltas[0] != deltas[1]:
            print(f"  FAIL  {label}: 레거시 +{deltas[0]} / 신규 +{deltas[1]}")
            worst = max(worst, 1)
        elif deltas[0] != expected:
            # 양쪽이 같지만 레거시를 읽고 예상한 값과 다르다. 내 이해가 틀렸다는 뜻이다.
            print(f"  FAIL  {label}: 양쪽 다 +{deltas[0]} 인데 예상은 +{expected}")
            worst = max(worst, 1)
        else:
            print(f"  PASS  {label}: 양쪽 다 +{deltas[0]}")

    for client in (legacy, new):
        set_cookie(client, "postId", _NO_COUNT_COOKIE)
    return worst


def main() -> int:
    account = os.environ.get("ERFLOW_TEST_ID")
    password = os.environ.get("ERFLOW_TEST_PASSWORD")
    if not account or not password:
        print("ERROR 환경변수 필요: ERFLOW_TEST_ID, ERFLOW_TEST_PASSWORD", file=sys.stderr)
        return 2

    try:
        legacy, new = opener(), opener()
        login(legacy, LEGACY, "/login/Login", account, password, csrf=False)
        login(new, NEW, "/login", account, password, csrf=True)
        # 두 앱이 같은 스키마를 본다. 조회수를 올리는 화면을 그냥 열면 먼저 연
        # 쪽이 값을 바꿔 놓아 뒤에 연 쪽과 달라진다 — 측정이 대상을 바꾼다.
        for client in (legacy, new):
            set_cookie(client, "postId", _NO_COUNT_COOKIE)
    except OSError as e:
        print(f"ERROR 로그인 실패: {e}\n      두 앱이 모두 떠 있는지 확인한다", file=sys.stderr)
        return 2

    ids = probe_ids(legacy)
    worst = 0
    for label, legacy_path, new_path, mode in SCREENS:
        legacy_path = legacy_path.format(**ids)
        new_path = new_path.format(**ids)
        read = {"rows": table_rows, "blocks": blocks, "readonly": readonly_values}[mode]
        try:
            legacy_html, legacy_denied = fetch_or_denial(legacy, LEGACY + legacy_path)
            new_html, new_denied = fetch_or_denial(new, NEW + new_path)
        except OSError as e:
            print(f"  ERROR {label}: {e}")
            worst = max(worst, 2)
            continue

        if legacy_denied or new_denied:
            if legacy_denied and new_denied:
                print(f"  PASS  {label}: 양쪽 다 권한으로 막힘")
            else:
                side = "신규만 통과" if legacy_denied else "레거시만 통과"
                print(f"  FAIL  {label}: 한쪽만 막혔다 — {side}")
                worst = max(worst, 1)
            continue

        before, after = read(legacy_html), read(new_html)

        if not before:
            print(f"  ERROR {label}: 레거시에서 내용을 읽지 못했다")
            worst = max(worst, 2)
            continue

        if len(before) != len(after):
            print(f"  FAIL  {label}: 개수 다름 — 레거시 {len(before)} / 신규 {len(after)}")
            for index in range(max(len(before), len(after)))[:4]:
                print(f"          [{index}] 레거시 {before[index] if index < len(before) else '(없음)'}")
                print(f"               신규   {after[index] if index < len(after) else '(없음)'}")
            worst = max(worst, 1)
            continue

        mismatched = [
            (index, a, b) for index, (a, b) in enumerate(zip(before, after)) if a != b
        ]
        if mismatched:
            print(f"  FAIL  {label}: {len(mismatched)}/{len(before)}행 다름")
            for index, a, b in mismatched[:3]:
                print(f"          [{index}] 레거시 {a}")
                print(f"               신규   {b}")
            worst = max(worst, 1)
        else:
            unit = "행" if mode == "rows" else "건"
            print(f"  PASS  {label}: {len(before)}{unit} 일치")

    worst = max(worst, check_view_counting(legacy, new, ids["post"]))

    verdict = {0: "PASS", 1: "FAIL", 2: "ERROR"}.get(worst, "?")
    print(f"\n결과: {verdict} (exit {worst})")
    return worst


if __name__ == "__main__":
    sys.exit(main())
