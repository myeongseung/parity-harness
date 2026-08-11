"""공개할 수 없는 것을 레거시 소스에서 가린다.

**원칙의 예외를 기록한다**

이 프로젝트는 "레거시는 정답이니 수정하지 않는다"를 지킨다. 여기가 유일한 예외다.
그 원칙은 이관 정합성을 위한 것이지, 살아있는 자격증명과 개인정보를 공개하는 근거가
될 수는 없다.

**무엇을 가리는가**

| 대상 | 이유 |
|---|---|
| Gmail 앱 비밀번호 | 실제로 동작하는 자격증명 |
| 메일 계정·주소 | 개인 식별 정보 |
| 인프라 호스트명 | 지금도 살아있는 서버 |

`legacy/ERFlow-DB.sql` 은 가리지 않고 **저장소에서 뺀다**. 사용자 55명의 이름·
주민등록번호·주소·휴대전화·이메일이 들어 있어 치환으로 해결되지 않는다. 이관에
필요한 권한 프로그램 20건은 `extract_programs.py` 가 따로 뽑아 둔다.

**게이트에 영향이 없다**

가리는 곳은 메일 발송 코드와 DB 덤프뿐이다. 정합성 게이트가 보는 것은 화면 JSP 의
마크업이므로 판정이 달라지지 않는다. 적용 후 `run_gates.py` 로 확인한다.

사용법::

    python migration/tools/redact_legacy.py [--check]

`--check` 는 고치지 않고 남아 있는지만 본다. exit 1 이면 아직 남아 있다.
"""

from __future__ import annotations

import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

#: 저장소에서 통째로 빼는 파일. 개인정보가 있어 치환으로 해결되지 않는다.
EXCLUDED = ("legacy/ERFlow-DB.sql",)

#: 긴 것부터 바꾼다. 짧은 것을 먼저 바꾸면 긴 문자열이 조각난다.
REPLACEMENTS = (
    ("[REDACTED_APP_PASSWORD]", "[REDACTED_APP_PASSWORD]"),
    ("[REDACTED_MAIL_ADDRESS]", "[REDACTED_MAIL_ADDRESS]"),
    ("[REDACTED_MAIL_USER]", "[REDACTED_MAIL_USER]"),
    ("[REDACTED_HOST]", "[REDACTED_HOST]"),
    ("[REDACTED_HOST]", "[REDACTED_HOST]"),
    ("[REDACTED_HOST]", "[REDACTED_HOST]"),
)

#: 훑을 확장자. 이진 파일은 건너뛴다.
SUFFIXES = {".java", ".jsp", ".js", ".css", ".html", ".xml", ".sql", ".md", ".properties"}


def targets(root: Path):
    legacy = root / "legacy"
    if not legacy.is_dir():
        return
    for path in legacy.rglob("*"):
        if path.is_file() and path.suffix.lower() in SUFFIXES:
            yield path


def main(argv: list[str]) -> int:
    check_only = "--check" in argv
    root = Path.cwd() if (Path.cwd() / "legacy").is_dir() else ROOT

    removed = []
    for relative in EXCLUDED:
        path = root / relative
        if path.exists():
            if check_only:
                print(f"  남아 있음: {relative} (개인정보 — 저장소에서 빼야 한다)")
            else:
                path.unlink()
                removed.append(relative)

    changed, remaining = [], []
    for path in targets(root):
        try:
            text = path.read_text(encoding="utf-8", errors="replace")
        except OSError:
            continue
        if not any(needle in text for needle, _ in REPLACEMENTS):
            continue

        if check_only:
            remaining.append(path.relative_to(root).as_posix())
            continue

        for needle, mask in REPLACEMENTS:
            text = text.replace(needle, mask)
        path.write_text(text, encoding="utf-8")
        changed.append(path.relative_to(root).as_posix())

    if check_only:
        if remaining:
            print("  아직 남아 있다:")
            for name in remaining:
                print(f"    {name}")
        if not remaining and not any((root / r).exists() for r in EXCLUDED):
            print("  깨끗하다")
            return 0
        return 1

    for name in removed:
        print(f"  제거  {name}")
    for name in changed:
        print(f"  가림  {name}")
    if not removed and not changed:
        print("  바꿀 것이 없다")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
