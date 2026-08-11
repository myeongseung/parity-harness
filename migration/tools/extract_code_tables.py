"""레거시에 하드코딩된 코드표를 자원 파일로 추출한다.

    FieldCodeRepository  업종코드 156쌍
    BankCodeRepository   은행코드  73쌍

둘 다 자바 배열 두 개(keys/values)로 소스에 박혀 있다. `code_tbl` 이 있는데도 비어
있으니 코드가 유일한 출처다. 손으로 옮겨 적으면 오타가 나고 검증할 방법도 없으므로
원본에서 기계로 뽑는다.

담는 곳만 바꾼다 — 값도 순서도 조회 방식도 그대로다.

사용법::

    python migration/tools/extract_code_tables.py
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
LEGACY_REPO = ROOT / "legacy" / "ERFlow" / "src" / "main" / "java" / "repository"
RESOURCES = ROOT / "migration" / "app" / "src" / "main" / "resources" / "data"

#: (레거시 클래스, 자원 파일, 사람이 읽을 이름)
TABLES = (
    ("FieldCodeRepository.java", "field-codes.properties", "업종코드"),
    ("BankCodeRepository.java", "bank-codes.properties", "은행코드"),
)

_ARRAY = r'{name}\s*=\s*new String\[\]\s*\{{(.*?)\}};'
_STRING = re.compile(r'"((?:[^"\\]|\\.)*)"', re.S)


def read_array(source: str, name: str) -> list[str]:
    """자바 소스에서 문자열 배열 하나를 읽는다."""
    match = re.search(_ARRAY.format(name=name), source, re.S)
    if match is None:
        raise SystemExit(f"{name} 배열을 찾지 못했다")
    return [item.group(1) for item in _STRING.finditer(match.group(1))]


def extract(class_name: str, resource: str, label: str) -> int:
    """레거시 저장소 클래스 하나를 자원 파일로 뽑는다.

    @param class_name 레거시 클래스 파일명
    @param resource 만들 자원 파일명
    @param label 사람이 읽을 표 이름
    @return 뽑은 항목 수
    """
    source_file = LEGACY_REPO / class_name
    source = source_file.read_text(encoding="utf-8", errors="replace")
    keys = read_array(source, "keys")
    values = read_array(source, "values")

    if len(keys) != len(values):
        raise SystemExit(f"{label}: 짝이 맞지 않는다 — 코드 {len(keys)} / 이름 {len(values)}")
    if len(set(keys)) != len(keys):
        raise SystemExit(f"{label}: 코드가 중복된다")
    if any("=" in code or "\n" in name for code, name in zip(keys, values)):
        raise SystemExit(f"{label}: '=' 또는 줄바꿈이 있어 properties 로 담을 수 없다")

    # properties 로 담는다. 이 표 때문에 JSON 파서를 끌어올 이유가 없다.
    # 순서는 레거시 배열 순서를 지킨다 — 레거시가 HashMap 이라 순서가 없더라도,
    # 자원 파일에서까지 흐트러뜨리면 원본과 대조하기 어려워진다.
    source_path = str(source_file.relative_to(ROOT)).replace("\\", "/")
    lines = [
        "# 생성물. 직접 고치지 말고 migration/tools/extract_code_tables.py 를 다시 돌린다.",
        f"# 출처: {source_path}",
        f"# 형식: 코드={label.replace('코드', '')}명",
        "",
    ]
    lines += [f"{code}={name}" for code, name in zip(keys, values)]

    out = RESOURCES / resource
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text("\n".join(lines) + "\n", encoding="utf-8")

    print(f"{label} {len(keys)}건 -> {out.relative_to(ROOT)}")
    for code, name in list(zip(keys, values))[:2]:
        print(f"    {code}  {name}")
    return len(keys)


def main() -> int:
    for class_name, resource, label in TABLES:
        extract(class_name, resource, label)
    return 0


if __name__ == "__main__":
    sys.exit(main())
