"""화면마다 **어떤 CSS 를 어떤 순서로** 싣는지 레거시와 견준다.

CSS 는 나중에 실은 것이 이긴다. 목록이 같아도 순서가 다르면 다른 화면이 된다.
레거시는 화면마다 목록도 순서도 제각각이다 — 56가지가 있다. 공용 조각 하나로
누르면 그 차이가 통째로 사라진다.

정합성 게이트는 `<link>` 를 아예 보지 않는다(화면에 «보이는» 요소가 아니므로).
실화면 그림 대조(`compare_render.py`)가 결과를 보긴 하지만 «다르다» 까지만 말한다.
여기서는 원인을 바로 짚는다.

`bootstrap2.css` 처럼 이름이 비슷한 다른 파일이 섞여 있고, 같은 파일을 두 번 싣는
화면도 있다. 둘 다 그대로 옮겨야 한다 — 두 번 싣는 것은 무해해 보이지만 그 사이에
실은 파일의 우선순위가 달라진다.

사용법::

    python migration/tools/check_stylesheets.py

exit code 는 게이트 규약을 따른다(0 PASS / 1 FAIL).
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from screens import MIGRATED, TEMPLATES  # noqa: E402

#: `href="..."` 와 `th:href="@{...}"` 를 함께 읽는다. 신규 템플릿은 뒤쪽 모양이다.
_LINK = re.compile(r'<link[^>]+href="@?\{?([^"}]+\.css)\}?"', re.I)
_FRAGMENT = re.compile(r"~\{fragments/head\s*::\s*head\(([^)]*)\)\}", re.S)

#: 공용 `fragments/head` 가 고정으로 싣는 목록. 순서까지 그대로다.
#:
#: **이 고정 목록이 문제의 뿌리다.** 레거시는 화면마다 목록도 순서도 다른데
#: (56가지가 있다) 조각 하나가 한 순서로 눌러 버린다. 고치려면 조각이 화면에서
#: 목록을 통째로 받아야 한다 — 지금은 아직 그렇게 되어 있지 않다(O-015).
_SHARED = ("bootstrap.css", "common.css", "page.css", "header.css", "aside.css")

def _split_args(text: str) -> list[str]:
    """괄호 안의 인자를 쉼표로 나눈다. 중괄호 안의 쉼표는 세지 않는다."""
    parts, depth, cur = [], 0, ""
    for ch in text:
        if ch in "({[":
            depth += 1
        if ch in ")}]":
            depth -= 1
        if ch == "," and depth == 0:
            parts.append(cur)
            cur = ""
        else:
            cur += ch
    parts.append(cur)
    return parts


def legacy_sheets(path: Path) -> list[str]:
    text = path.read_text(encoding="euc-kr", errors="replace")
    return [u.split("/")[-1] for u in _LINK.findall(text)]


def new_sheets(path: Path) -> list[str]:
    """신규 템플릿이 싣는 목록.

    공용 조각을 쓰면 화면이 넘긴 목록이 곧 전부다 — 조각은 고정 목록을 갖지 않는다.

    조각은 인자를 셋 받는다(제목·CSS·스크립트). **두 번째만 봐야 한다** — 레거시가
    CSS 파일을 `<script>` 로 싣는 자리가 있어서(`taskHistory.css`), 인자를 통째로
    훑으면 그것까지 스타일시트로 세어 없던 차이가 보고된다.
    """
    text = path.read_text(encoding="utf-8", errors="replace")
    fragment = _FRAGMENT.search(text)
    if not fragment:
        return [u.split("/")[-1] for u in _LINK.findall(text)]
    args = _split_args(fragment.group(1))
    if len(args) != 3:
        return []
    return [u.split("/")[-1]
            for u in re.findall(r"'((?:/css/|https?://)[^']+\.css)'", args[1])]


def main() -> int:
    worst = 0
    same = 0
    for screen in MIGRATED:
        template = TEMPLATES / screen.template
        if not screen.legacy_file.exists() or not template.exists():
            continue
        before = legacy_sheets(screen.legacy_file)
        after = new_sheets(template)
        if before == after:
            same += 1
            continue
        worst = 1
        print(f"  FAIL  {screen.label}")
        print(f"          레거시 {' > '.join(before) or '(없음)'}")
        print(f"          신규   {' > '.join(after) or '(없음)'}")

    print(f"\n일치 {same} / 다름 {sum(1 for _ in MIGRATED) - same}")
    return worst


if __name__ == "__main__":
    raise SystemExit(main())
