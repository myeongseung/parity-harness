"""저장소에 가려야 할 것이 남아 있는지 검사한다.

세 번 같은 실수를 했다. 소스에서 지우고, 마스킹 스크립트에 적어서 다시 새고,
그 이야기를 쓴 글에 또 적었다. 셋 다 사람이 눈으로 확인하다 놓쳤다.

**사람의 주의력에 기대는 규칙은 지켜지지 않는다.** 기계가 확인하게 만든다.

검사 대상은 `redact-patterns.local.json` 에서 읽는다. 이 스크립트에 적으면
이 스크립트가 그것을 공개한다 — 두 번째 실수가 정확히 그것이었다.

사용법::

    python migration/tools/check_no_secrets.py

exit 0 이면 깨끗하다. 1 이면 남아 있다. 2 는 검사 자체가 불가능한 상태다.
"""

from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
PATTERNS = ROOT / "migration" / "tools" / "redact-patterns.local.json"

SKIP_DIRS = {".git", "build", ".gradle", "node_modules", "__pycache__", ".bkit"}
SKIP_SUFFIX = {".png", ".jpg", ".gif", ".ico", ".ttf", ".woff", ".woff2", ".jar", ".zip", ".map"}

#: 절대 추적되면 안 되는 파일. 문자열 검사와 별개의 그물이다 —
#: 목록에 없는 비밀이 들어 있어도 파일 자체가 올라간 것은 잡아야 한다.
#: `git add -f` 는 .gitignore 를 무시하므로 gitignore 만으로는 부족하다.
NEVER_TRACKED = (
    "application-local.yml",
    "redact-patterns.local.json",
    "legacy/ERFlow-DB.sql",
)


def needles() -> list[str] | None:
    """가려야 할 문자열을 읽는다.

    @return 검사할 문자열 목록. 목록 파일이 없으면 None
    """
    if not PATTERNS.is_file():
        return None
    data = json.loads(PATTERNS.read_text(encoding="utf-8"))
    return [item["from"] for item in data.get("replacements", [])]


def tracked_files() -> list[Path]:
    """git 이 추적하는 파일만 본다. 추적하지 않는 것은 공개되지 않는다."""
    result = subprocess.run(
        ["git", "ls-files"], cwd=ROOT, capture_output=True, text=True,
        encoding="utf-8", errors="replace")
    return [ROOT / line for line in result.stdout.splitlines() if line.strip()]


def leaked_files(tracked: list[Path]) -> list[str]:
    """올라가면 안 되는 파일이 추적되고 있는지 본다.

    `.example` 로 끝나는 템플릿은 대상이 아니다 — 값이 없으니 올라가야 한다.
    """
    leaked = []
    for path in tracked:
        name = path.as_posix()
        if name.endswith(".example"):
            continue
        if any(name.endswith(marker) for marker in NEVER_TRACKED):
            leaked.append(path.name)
    return leaked


def main() -> int:
    patterns = needles()
    if patterns is None:
        # 검사 대상을 모르는 것은 "위반 없음"이 아니라 "판정 불가"다.
        # 게이트 규약과 같은 이유로 통과(0)와 구분한다.
        print(f"ERROR {PATTERNS.name} 이 없다. 검사할 대상을 알 수 없다.\n"
              "      redact-patterns.example.json 을 보고 만든다. 커밋하지 않는다.",
              file=sys.stderr)
        return 2

    hits: list[tuple[str, int]] = []
    tracked = tracked_files()

    leaked = leaked_files(tracked)
    if leaked:
        print("FAIL  올라가면 안 되는 파일이 추적되고 있다")
        for name in leaked:
            print(f"        {name}")
        print("      git rm --cached <파일> 로 추적을 끊는다. 이미 푸시했다면 이력도 지운다.")
        return 1

    for path in tracked:
        if not path.is_file() or any(part in SKIP_DIRS for part in path.parts):
            continue
        if path.suffix.lower() in SKIP_SUFFIX:
            continue
        try:
            text = path.read_text(encoding="utf-8", errors="replace")
        except OSError:
            continue
        for index, needle in enumerate(patterns, start=1):
            if needle in text:
                # 앞 몇 글자라도 찍으면 그것이 CI 로그에 남는다. 목록 번호만 알린다 —
                # 무엇인지는 redact-patterns.local.json 을 보면 된다.
                hits.append((path.relative_to(ROOT).as_posix(), index))

    if hits:
        print(f"FAIL  가려야 할 것이 {len(hits)}곳에 남아 있다")
        for name, index in hits:
            print(f"        {name}  (목록 #{index})")
        return 1

    print(f"PASS  추적 파일 {len(tracked)}개에 남은 것 없음 "
          f"(문자열 {len(patterns)}종 · 금지 파일 {len(NEVER_TRACKED)}종)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
