"""의도적으로 추가한 요소의 예외 등록.

게이트를 끄는 방법은 있어야 한다. 다만 끄면 기록이 남아야 한다.
사유 없는 예외는 설정 오류로 취급해 게이트 자체를 실패시킨다.
CSRF 토큰이나 접근성 skip link 처럼 레거시에 없지만 필요한 요소가 대상이다.
"""

from __future__ import annotations

import json
from pathlib import Path

SCHEMA_ID = "parity-harness/allowlist@1"
MIN_REASON_LEN = 10


class AllowlistError(Exception):
    """allowlist 파일을 읽을 수 없거나 사유가 부실함."""


def load_allowlist(path: str | Path | None) -> dict[str, str]:
    """allowlist 파일에서 {signature key: 사유} 를 읽는다.

    경로가 None 이거나 파일이 없으면 빈 예외 목록으로 간주한다.
    """
    if path is None:
        return {}
    file = Path(path)
    if not file.is_file():
        return {}

    try:
        data = json.loads(file.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        raise AllowlistError(f"allowlist JSON 파싱 실패: {file} ({exc})") from exc

    if data.get("schema") != SCHEMA_ID:
        raise AllowlistError(f"schema 불일치: {data.get('schema')!r} (기대값 {SCHEMA_ID!r})")

    entries = data.get("entries")
    if not isinstance(entries, list):
        raise AllowlistError("entries 배열이 없음")

    approved: dict[str, str] = {}
    for index, entry in enumerate(entries):
        key = entry.get("key")
        reason = (entry.get("reason") or "").strip()
        if not key:
            raise AllowlistError(f"entries[{index}]: key 누락")
        if len(reason) < MIN_REASON_LEN:
            raise AllowlistError(
                f"entries[{index}] ({key}): 사유가 {MIN_REASON_LEN}자 미만이다. "
                "예외는 근거를 남겨야 승인된다."
            )
        approved[key] = reason
    return approved
