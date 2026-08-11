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
import urllib.parse
import urllib.request

LEGACY = os.environ.get("ERFLOW_LEGACY_BASE", "http://localhost:19090/ERFlow")
NEW = os.environ.get("ERFLOW_NEW_BASE", "http://localhost:18080")

#: (이름, 레거시 경로, 신규 경로)
SCREENS = (
    ("생산 설비 관리", "/unit/unitList.jsp", "/unit/list"),
    ("협력업체 관리 (구매)", "/company/companyList.jsp?flag=1", "/company/list?flag=1"),
    ("협력업체 관리 (영업)", "/company/companyList.jsp?flag=0", "/company/list?flag=0"),
)

_TAG = re.compile(r"<[^>]+>")
_CSRF = re.compile(r'name="_csrf"\s+value="([^"]*)"')


def opener() -> urllib.request.OpenerDirector:
    """쿠키를 담는 오프너. 세션이 유지되어야 화면을 받을 수 있다."""
    jar = http.cookiejar.CookieJar()
    return urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))


def fetch(client, url: str, data: dict | None = None) -> str:
    body = urllib.parse.urlencode(data).encode() if data else None
    with client.open(url, body, timeout=30) as response:
        return response.read().decode("utf-8", errors="replace")


def login(client, base: str, path: str, account: str, password: str, csrf: bool) -> None:
    """로그인한다. 신규 앱은 CSRF 토큰을 요구한다(레거시에는 없던 방어다)."""
    form = {"id": account, "password": password}
    if csrf:
        page = fetch(client, f"{base}/login")
        token = _CSRF.search(page)
        if token:
            form["_csrf"] = token.group(1)
    fetch(client, f"{base}{path}", form)


def table_rows(html: str) -> list[list[str]]:
    """`<tbody>` 안의 데이터 행을 글자만 남겨 뽑는다."""
    start, end = html.find("<tbody"), html.find("</tbody>")
    if start < 0 or end < 0:
        return []
    rows = []
    for chunk in re.split(r"<tr[^>]*>", html[start:end])[1:]:
        cells = [
            _TAG.sub("", cell).replace("&nbsp;", " ").strip()
            for cell in re.findall(r"<td[^>]*>(.*?)</td>", chunk, re.S)
        ]
        if cells:
            rows.append(cells)
    return rows


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
    except OSError as e:
        print(f"ERROR 로그인 실패: {e}\n      두 앱이 모두 떠 있는지 확인한다", file=sys.stderr)
        return 2

    worst = 0
    for label, legacy_path, new_path in SCREENS:
        try:
            before = table_rows(fetch(legacy, LEGACY + legacy_path))
            after = table_rows(fetch(new, NEW + new_path))
        except OSError as e:
            print(f"  ERROR {label}: {e}")
            worst = max(worst, 2)
            continue

        if not before:
            print(f"  ERROR {label}: 레거시에서 표를 읽지 못했다 (권한? 로그인?)")
            worst = max(worst, 2)
            continue

        if len(before) != len(after):
            print(f"  FAIL  {label}: 행수 다름 — 레거시 {len(before)} / 신규 {len(after)}")
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
            print(f"  PASS  {label}: {len(before)}행 일치")

    verdict = {0: "PASS", 1: "FAIL", 2: "ERROR"}.get(worst, "?")
    print(f"\n결과: {verdict} (exit {worst})")
    return worst


if __name__ == "__main__":
    sys.exit(main())
