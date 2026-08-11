"""공개할 수 없는 것을 레거시 소스에서 가린다.

**원칙의 예외를 기록한다**

이 프로젝트는 "레거시는 정답이니 수정하지 않는다"를 지킨다. 여기가 유일한 예외다.
그 원칙은 이관 정합성을 위한 것이지, 살아있는 자격증명을 공개하는 근거가 될 수 없다.

**가릴 대상은 이 파일에 적지 않는다**

가려야 할 문자열을 스크립트에 적으면 스크립트가 그것을 공개한다. 실제로 한 번
그렇게 새어 나갔다. 대상은 `redact-patterns.local.json` 에 두고 그 파일은 커밋하지
않는다(`redact-patterns.example.json` 참조).

**이미 적용된 저장소에서는 이 스크립트가 필요 없다**

저장소에 담긴 소스는 이미 가려져 있다. 이 도구는 원본을 새로 들여올 때만 쓴다.
패턴 파일이 없으면 그렇게 안내하고 멈춘다.

**게이트에 영향이 없다**

가리는 곳은 메일 발송 코드뿐이다. 정합성 게이트가 보는 것은 화면 JSP 의 마크업이라
판정이 달라지지 않는다. 적용 후 `run_gates.py` 로 확인한다.

사용법::

    python migration/tools/redact_legacy.py [--check]

`--check` 는 고치지 않고 남아 있는지만 본다. exit 1 이면 아직 남아 있다.
"""

from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
PATTERNS = Path(__file__).resolve().parent / "redact-patterns.local.json"

#: 훑을 확장자. 이진 파일은 건너뛴다.
SUFFIXES = {".java", ".jsp", ".js", ".css", ".html", ".xml", ".sql", ".md", ".properties"}


def load_patterns() -> tuple[list[str], list[tuple[str, str]]]:
    """가릴 대상을 읽는다.

    @return (저장소에서 뺄 경로 목록, (원문, 대체문) 목록)
    """
    if not PATTERNS.is_file():
        raise SystemExit(
            f"{PATTERNS.name} 이 없다.\n"
            "  이미 가려진 저장소에서는 이 도구가 필요 없다.\n"
            "  원본을 새로 들여왔다면 redact-patterns.example.json 을 보고 만든다.\n"
            "  이 파일은 커밋하지 않는다 — 가릴 대상을 적어 두는 파일이다.")

    data = json.loads(PATTERNS.read_text(encoding="utf-8"))
    replacements = [(item["from"], item["to"]) for item in data.get("replacements", [])]
    # 긴 것부터 바꾼다. 짧은 것을 먼저 바꾸면 긴 문자열이 조각난다.
    replacements.sort(key=lambda pair: len(pair[0]), reverse=True)
    return data.get("excluded", []), replacements


def targets(root: Path):
    legacy = root / "legacy"
    if not legacy.is_dir():
        return
    for path in legacy.rglob("*"):
        if path.is_file() and path.suffix.lower() in SUFFIXES:
            yield path


def main(argv: list[str]) -> int:
    check_only = "--check" in argv
    excluded, replacements = load_patterns()
    root = Path.cwd() if (Path.cwd() / "legacy").is_dir() else ROOT

    left_behind = [relative for relative in excluded if (root / relative).exists()]
    if not check_only:
        for relative in left_behind:
            (root / relative).unlink()
            print(f"  제거  {relative}")

    changed, remaining = [], []
    for path in targets(root):
        try:
            text = path.read_text(encoding="utf-8", errors="replace")
        except OSError:
            continue
        if not any(needle in text for needle, _ in replacements):
            continue

        name = path.relative_to(root).as_posix()
        if check_only:
            remaining.append(name)
            continue

        for needle, mask in replacements:
            text = text.replace(needle, mask)
        path.write_text(text, encoding="utf-8")
        changed.append(name)
        print(f"  가림  {name}")

    if check_only:
        for relative in left_behind:
            print(f"  남아 있음: {relative}")
        for name in remaining:
            print(f"  남아 있음: {name}")
        if not left_behind and not remaining:
            print("  깨끗하다")
            return 0
        return 1

    if not left_behind and not changed:
        print("  바꿀 것이 없다")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
