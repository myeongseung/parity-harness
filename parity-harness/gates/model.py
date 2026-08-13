"""정합성 판정의 기본 자료구조: 정규화 규칙, Signature, golden manifest IO.

Signature 는 스택 중립적이다. 레거시가 ASPxButton 이든 신규가 dxButton 이든
동일한 `control:button|저장|submit` 으로 환원되어야 diff 가 의미를 갖는다.
"""

from __future__ import annotations

import json
import re
import unicodedata
from dataclasses import dataclass, field
from pathlib import Path

SCHEMA_ID = "parity-harness/golden-manifest@1"

#: 추출 규칙이 바뀔 때 올린다.
#:
#: 정답은 추출기가 만든다. 추출기를 고치고 정답을 다시 뽑지 않으면 낡은 기준으로
#: 판정하게 되고, 그것은 "정답이 틀렸는데 통과"라는 가장 나쁜 실패다. 버전이 어긋나면
#: 통과시키지 않고 사람에게 넘긴다.
EXTRACTOR_VERSION = 7

#: 서버 렌더링/바인딩으로 채워지는 텍스트는 정적 분석으로 알 수 없다.
#: 레거시 <%= %> 와 신규 th:text 를 동일한 자리표시자로 환원해 오탐을 막는다.
DYNAMIC = "«dyn»"

_WS = re.compile(r"\s+")
_TRIM = " \t\r\n*:：·"


def norm(value: object) -> str:
    """표시 텍스트를 비교 가능한 형태로 정규화한다.

    전각/반각(NFKC), NBSP, 연속 공백, 장식용 필수표시(*)와 콜론을 흡수한다.
    레거시 "사원번호 *" 와 신규 "사원번호:" 는 같은 라벨로 취급되어야 한다.
    """
    if not value:
        return ""
    text = unicodedata.normalize("NFKC", str(value))
    text = text.replace(" ", " ")
    text = _WS.sub(" ", text).strip()
    return text.strip(_TRIM).strip().casefold()


@dataclass(frozen=True)
class Signature:
    """의미 있는 UI 요소 하나의 스택 중립 지문."""

    kind: str
    """control | nav | column | option | text"""

    role: str
    """button | input | select | textarea | link | header | item | static | widget"""

    label: str
    """정규화된 표시 라벨 (또는 DYNAMIC)"""

    detail: str = ""
    """동일 라벨을 구분하는 보조 키 (input type, href path, 위젯명 등)"""

    raw: str = field(default="", compare=False)
    """리포트 표시용 원문 라벨. 비교에는 쓰지 않는다."""

    line: int = field(default=0, compare=False)
    """원본 파일에서의 줄 번호. 리포트 표시용."""

    @property
    def key(self) -> str:
        return f"{self.kind}:{self.role}|{self.label}|{self.detail}"

    def human(self) -> str:
        label = self.raw or self.label or "(무라벨)"
        detail = f" [{self.detail}]" if self.detail else ""
        return f"{self.kind}:{self.role} «{label}»{detail}"

    def to_dict(self) -> dict:
        return {
            "kind": self.kind,
            "role": self.role,
            "label": self.label,
            "detail": self.detail,
            "raw": self.raw,
        }

    @classmethod
    def from_dict(cls, data: dict) -> "Signature":
        return cls(
            kind=data["kind"],
            role=data["role"],
            label=data.get("label", ""),
            detail=data.get("detail", ""),
            raw=data.get("raw", ""),
        )


class ManifestError(Exception):
    """golden manifest 를 읽을 수 없거나 형식이 어긋남."""


def load_manifest(path: str | Path) -> list[Signature]:
    """golden manifest 파일에서 Signature 목록을 읽는다."""
    file = Path(path)
    if not file.is_file():
        raise ManifestError(f"golden manifest 없음: {file}")
    try:
        data = json.loads(file.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        raise ManifestError(f"golden manifest JSON 파싱 실패: {file} ({exc})") from exc

    if data.get("schema") != SCHEMA_ID:
        raise ManifestError(
            f"schema 불일치: {data.get('schema')!r} (기대값 {SCHEMA_ID!r})"
        )
    recorded = data.get("extractor")
    if recorded != EXTRACTOR_VERSION:
        raise ManifestError(
            f"추출기 버전 불일치: 정답은 {recorded}, 지금은 {EXTRACTOR_VERSION}. "
            "추출 규칙이 바뀌었다. extract_golden 으로 정답을 다시 뽑아야 한다"
        )
    entries = data.get("signatures")
    if not isinstance(entries, list):
        raise ManifestError("signatures 배열이 없음")
    return [Signature.from_dict(entry) for entry in entries]


def dump_manifest(
    signatures: list[Signature], screen: str, source: str
) -> str:
    """Signature 목록을 golden manifest JSON 문자열로 직렬화한다."""
    payload = {
        "schema": SCHEMA_ID,
        "extractor": EXTRACTOR_VERSION,
        "screen": screen,
        "source": source,
        "signatures": [sig.to_dict() for sig in signatures],
    }
    return json.dumps(payload, ensure_ascii=False, indent=2) + "\n"
