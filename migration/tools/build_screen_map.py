"""화면 대조표를 만든다. `migration/design/03-screen-map.md`.

두 앱을 띄워 놓고 «어느 주소와 어느 주소를 견주면 되는가» 를 적은 표다. 손으로
적으면 화면이 늘 때마다 어긋난다 — 화면 목록(`screens.py`)에서 뽑는다.

주소는 컨트롤러의 `@GetMapping` 에서 읽는다. 목록에 없는 화면이나 매핑이 없는 화면은
표에 «?» 로 남기고 마지막에 세어 준다. 조용히 빼지 않는다.

사용법::

    python migration/tools/build_screen_map.py
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from screens import ROOT, SCREENS, TEMPLATES, Screen  # noqa: E402

APP = ROOT / "migration" / "app" / "src" / "main" / "java" / "com" / "erflow"
OUT = ROOT / "migration" / "design" / "03-screen-map.md"

#: 레거시 기준 주소. 톰캣에 `/ERFlow` 로 올린다.
LEGACY_BASE = "/ERFlow"

#: 도메인 묶음 이름. 표의 소제목이 된다.
GROUPS = (
    ("login", "로그인"),
    ("index", "메인"),
    ("profile", "프로필"),
    ("post", "게시판"),
    ("message", "쪽지"),
    ("proposal", "전자결재"),
    ("document", "문서"),
    ("unit", "생산 설비"),
    ("company", "협력업체"),
    ("product", "제품"),
    ("process", "공정"),
    ("task", "수·발주"),
    ("bound", "입·출고"),
    ("find", "찾기 팝업"),
    ("admin", "관리자"),
    ("error", "안내 화면"),
)

#: 템플릿 이름으로 주소를 찾을 수 없는 화면. 이유를 함께 적는다.
#:
#: 대부분은 «화면이 아니라 조각» 이라 주소가 없는 것들이다. 조각은 그것을 품은
#: 화면을 열면 함께 보이므로 대조도 그렇게 한다.
MANUAL: dict[str, tuple[str, str]] = {
    "fragments/footer.html": ("", "메인 화면 안에 그려진다"),
    "fragments/admin-side.html": ("", "관리자 화면 안에 그려진다"),
    "fragments/admin-header.html": ("", "관리자 화면 안에 그려진다"),
    "fragments/admin-footer.html": ("", "관리자 화면 안에 그려진다"),
    "task/history-modal.html": ("", "수·발주 목록에서 «이력» 을 누르면 뜬다"),
    "login/password-error.html": ("/login/password-error", ""),
    "login/password-ok.html": ("/login/password-ok", ""),
    "login/send-ok.html": ("", "이관 전(O-011)"),
}

_MAPPING = re.compile(r'@(?:Get|Request)Mapping\(\s*(?:value\s*=\s*)?\{?([^)]*?)\}?\s*\)')
_CLASS_MAPPING = re.compile(r'@RequestMapping\("([^"]+)"\)\s*(?:public\s+)?class')

#: 따옴표 안의 글자. 이 중 **실제 템플릿 파일이 있는 것**만 화면 이름으로 인정한다.
#:
#: `return "..."` 만 보면 안 된다. 목록 화면 여럿이 도우미 하나를 함께 쓰면서
#: 템플릿 이름을 **인자로** 넘긴다 — `return list(..., "product/register")` 처럼.
#: 그 자리를 못 보면 화면 열한 개가 표에서 «?» 로 남는다.
#:
#: 모양으로 거르면 `"success"` 같은 글자가 섞이고, 이름에 `/` 를 요구하면 메인
#: 화면(`"index"`)이 빠진다. 파일이 있는지 보는 편이 둘 다 맞는다.
_QUOTED = re.compile(r'"([a-z][a-z0-9/_-]*)"')


def routes_by_view() -> dict[str, str]:
    """컨트롤러를 읽어 «템플릿 이름 -> 주소» 를 만든다.

    `@GetMapping` 다음에 처음 나오는 템플릿 이름이 그 주소의 화면이다. 어림짐작이
    아니라 실제 매핑이므로, 컨트롤러를 고치면 표도 따라 바뀐다.
    """
    found: dict[str, str] = {}
    for path in sorted(APP.rglob("*Controller.java")):
        text = path.read_text(encoding="utf-8")
        prefix_match = _CLASS_MAPPING.search(text)
        prefix = prefix_match.group(1) if prefix_match else ""

        pending: list[str] = []
        for line in text.splitlines():
            stripped = line.strip()
            if stripped.startswith("*"):
                # 주석 안의 예시를 매핑으로 읽지 않는다.
                continue
            if stripped.startswith("@GetMapping"):
                pending = [
                    part.strip().strip('"')
                    for part in _MAPPING.sub(r"\1", stripped).split(",")
                    if part.strip().startswith('"')
                ]
                continue
            if not pending:
                continue
            for candidate in _QUOTED.findall(stripped):
                if not (TEMPLATES / f"{candidate}.html").exists():
                    continue
                # 한 화면을 여러 주소가 가리키면 긴 쪽을 적는다(`/` 보다 `/index`).
                route = sorted(pending, key=len)[-1]
                found.setdefault(candidate + ".html", prefix + route)
                pending = []
                break
    return found


def group_of(screen: Screen) -> str:
    for key, _ in GROUPS:
        if screen.domain == key:
            return key
    return "기타"


def main() -> int:
    by_view = routes_by_view()
    unknown = 0
    lines: list[str] = []

    lines.append("# 화면 대조표")
    lines.append("")
    lines.append("두 앱을 띄워 놓고 **어느 주소와 어느 주소를 견주는가**. "
                 "`build_screen_map.py` 가 화면 목록에서 뽑는다 — 손으로 고치지 않는다.")
    lines.append("")
    lines.append("| | |")
    lines.append("|---|---|")
    lines.append(f"| 레거시 | `http://localhost:19090{LEGACY_BASE}` |")
    lines.append("| 신규 | `http://localhost:18080` |")
    lines.append("")
    lines.append("로그인은 양쪽 다 따로 한다. 세션이 다르므로 한쪽에서 로그인해도 "
                 "다른 쪽은 모른다.")
    lines.append("")
    lines.append("> **먼저 여는 쪽이 뒤에 여는 쪽을 오염시킨다.** 두 앱이 같은 스키마를 "
                 "본다. 게시글 보기처럼 여는 것만으로 조회수가 오르는 화면은 순서가 "
                 "결과를 바꾼다(D-033).")
    lines.append("")

    for key, title in GROUPS:
        rows = [s for s in SCREENS if group_of(s) == key]
        if not rows:
            continue
        lines.append(f"## {title}")
        lines.append("")
        lines.append("| 화면 | 레거시 | 신규 |")
        lines.append("|---|---|---|")
        for screen in sorted(rows, key=lambda s: s.template):
            manual = MANUAL.get(screen.template)
            if manual is not None:
                route, note = manual
                new = f"`{route}`" if route else f"— {note}"
            elif screen.template in by_view:
                new = f"`{by_view[screen.template]}`"
            else:
                new = "**?**"
                unknown += 1
            lines.append(
                f"| {screen.label} | `{LEGACY_BASE}/{screen.legacy}` | {new} |")
        lines.append("")

    lines.append("## 화면이 아닌 대조")
    lines.append("")
    lines.append("| 대상 | 레거시 | 신규 |")
    lines.append("|---|---|---|")
    lines.append("| 달력 일정 조회 | `/ERFlow/calendar/view` | `/calendar/view` |")
    lines.append("| 근무 현황 그래프 | `/ERFlow/admin/graph/view` | `/admin/graph/view` |")
    lines.append("")
    lines.append("메인 화면의 달력과 관리자 대시보드의 그래프는 화면이 아니라 JSON 이다. "
                 "정합성 게이트가 아예 닿지 않으므로 눈으로 견주거나 시험으로 막는다.")
    lines.append("")
    lines.append("## 견줄 때 알아둘 것")
    lines.append("")
    lines.append("**파라미터가 있어야 열리는 화면이 있다.** 보기·수정 화면은 대개 "
                 "`?id=` 를 요구하고, 없으면 레거시는 그 자리에서 죽는다(500). "
                 "목록 화면에서 눌러 들어가는 편이 확실하다.")
    lines.append("")
    lines.append("**로그인하지 않고 열면 세 화면은 404 가 뜬다.** `passwordCheck`, "
                 "`findProposalRoute`, 그리고 `profile` 의 날짜 오류 경로다. "
                 "레거시가 안내 화면을 한 칸 위에서 찾는다 — 신규는 제대로 "
                 "보내므로 여기서만 다르다(D-087).")
    lines.append("")
    lines.append("**한 화면이 파라미터로 갈리는 곳이 있다.** 협력업체 관리는 "
                 "`?flag=1` 이 구매, `?flag=0` 이 영업이고 요구 권한도 다르다.")
    lines.append("")

    if unknown:
        lines.append(f"> 주소를 찾지 못한 화면 {unknown}건이 **?** 로 남아 있다. "
                     "컨트롤러에 매핑이 없거나, 매핑과 템플릿 이름이 이어지지 않는다.")
        lines.append("")

    OUT.write_text("\n".join(lines), encoding="utf-8")
    sys.stdout.buffer.write(
        f"화면 대조표: {OUT}  (화면 {len(SCREENS)}건, 주소 미상 {unknown}건)\n"
        .encode("utf-8"))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
