"""권한 프로그램 목록을 레거시 DB 덤프에서 뽑아 자원 파일로 남긴다.

**왜 덤프를 저장소에 두지 않는가**

`legacy/ERFlow-DB.sql` 에는 사용자 55명의 개인정보가 들어 있다 — 이름, 주민등록번호,
주소, 휴대전화, 이메일. 공개 저장소에 둘 수 있는 것이 아니다.

"레거시는 정답이니 수정하지 않는다"는 이 프로젝트의 원칙이지만, 그 원칙이 개인정보를
공개하는 근거가 될 수는 없다. 원칙은 이관 정합성을 위한 것이지 그보다 상위의 것을
이기지 못한다.

이관에 필요한 것은 `permission_program_tbl` 20건뿐이고 거기에는 개인정보가 없다.
그것만 뽑아 커밋하고, 덤프 자체는 저장소에서 뺀다(`.gitignore`).

**재생성**

덤프가 있는 환경에서만 다시 뽑을 수 있다. 산출물 `migration/seed/programs.json` 은
커밋되므로 덤프 없이도 seed 생성과 CI 는 그대로 돈다.

    python migration/tools/extract_programs.py
"""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DUMP = ROOT / "legacy" / "ERFlow-DB.sql"
OUT = ROOT / "migration" / "seed" / "programs.json"

_INSERT = re.compile(r"INSERT INTO `permission_program_tbl`.*?;", re.S)
_ROW = re.compile(r"\(\d+,\s*'([0-9A-F]+)',\s*'([^']+)',\s*(-?\d+),\s*(-?\d+)\)")


def main() -> int:
    if not DUMP.is_file():
        print(f"ERROR 덤프가 없다: {DUMP}", file=sys.stderr)
        print("      개인정보가 있어 커밋하지 않는다. 원본을 구해 두고 다시 돌린다.", file=sys.stderr)
        return 2

    block = _INSERT.search(DUMP.read_text(encoding="utf-8", errors="replace"))
    if block is None:
        print("ERROR permission_program_tbl INSERT 문을 찾지 못했다", file=sys.stderr)
        return 2

    programs = [
        {
            "program_id": program_id,
            "name": name,
            "dept_level": int(dept),
            "job_level": int(job),
        }
        for program_id, name, dept, job in _ROW.findall(block.group(0))
    ]
    if not programs:
        print("ERROR 프로그램을 하나도 읽지 못했다", file=sys.stderr)
        return 2

    payload = {
        "schema": "erflow/programs@1",
        "generated_by": "migration/tools/extract_programs.py",
        "source": "legacy/ERFlow-DB.sql (permission_program_tbl 만. 개인정보는 담지 않는다)",
        "programs": programs,
    }
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

    print(f"권한 프로그램 {len(programs)}건 -> {OUT.relative_to(ROOT)}")
    for entry in programs[:3]:
        print(f"    {entry['name']}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
