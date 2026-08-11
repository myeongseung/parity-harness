"""게이트: 발명(invention) 차단.

레거시에 없던 요소가 신규 구현에 등장하면 실패시킨다.

LLM 마이그레이션의 최대 실패 모드는 버그가 아니라 친절함이다. 없던 검색창을
추가하고, 없던 토스트를 띄우고, 없던 페이지네이션을 만든다. 그럴듯해서 코드
리뷰에서 걸리지 않는다. 이 게이트는 그것만 잡는다.

사용법::

    python -m gates.check_no_invention --golden golden/x.json --new new/x.html
    python -m gates.check_no_invention --golden golden/x.json --new new/x.html --strict --json
"""

from __future__ import annotations

import argparse
import json
import sys
from collections import Counter
from dataclasses import dataclass
from pathlib import Path

from . import EXIT_ERROR, EXIT_ESCALATE, EXIT_FAIL, EXIT_PASS
from .allowlist import AllowlistError, load_allowlist
from .compare import DETAIL_CHANGE, LABEL_DRIFT, NONE, classify, surplus
from .extract import extract_file
from .model import ManifestError, Signature, load_manifest

#: 발명 시 사용자 영향이 큰 종류. 나머지는 한 단계 낮춰 보고한다.
_HIGH_IMPACT_KINDS = {"control", "nav", "column", "option"}


@dataclass
class Finding:
    signature: Signature
    severity: str
    reason: str
    hint: str | None

    def to_dict(self) -> dict:
        return {
            "severity": self.severity,
            "reason": self.reason,
            "key": self.signature.key,
            "label": self.signature.raw or self.signature.label,
            "line": self.signature.line,
            "near_miss": self.hint,
        }


def detect(
    golden: list[Signature], new: list[Signature], approved: dict[str, str]
) -> tuple[list[Finding], list[Signature]]:
    """신규에만 존재하는(또는 개수가 초과된) 요소를 찾는다.

    개수까지 센다. 레거시에 버튼이 1개인데 신규에 3개면 2개가 발명이다.

    Returns:
        (findings, 예외 승인으로 걸러진 항목들)
    """
    budget = Counter(sig.key for sig in golden)
    findings: list[Finding] = []
    waived: list[Signature] = []

    # 힌트가 이미 짝이 맞은 요소를 가리키지 않도록, 반대편도 짝 없는 것만 넘긴다.
    unmatched = surplus(golden, new)

    for signature in new:
        if budget[signature.key] > 0:
            budget[signature.key] -= 1
            continue
        if signature.key in approved:
            waived.append(signature)
            continue

        reason, hint = classify(signature, unmatched)
        if reason == NONE and signature.kind in _HIGH_IMPACT_KINDS:
            severity = "HIGH"
        else:
            severity = "MEDIUM"
        findings.append(Finding(signature, severity, reason, hint))

    order = {"HIGH": 0, "MEDIUM": 1}
    findings.sort(key=lambda item: (order[item.severity], item.signature.line))
    return findings, waived


def _report(
    findings: list[Finding], waived: list[Signature], new_path: Path, strict: bool
) -> None:
    if not findings:
        print(f"PASS  발명 요소 없음  ({new_path})")
        if waived:
            print(f"      예외 승인 {len(waived)}건 (allowlist)")
        return

    highs = sum(1 for finding in findings if finding.severity == "HIGH")
    mediums = len(findings) - highs
    by_reason = Counter(finding.reason for finding in findings)
    breakdown = " / ".join(f"{name} {count}" for name, count in sorted(by_reason.items()))
    print(f"차이 {len(findings)}건  (HIGH {highs} / MEDIUM {mediums})  [{breakdown}]  {new_path}\n")

    for finding in findings:
        signature = finding.signature
        print(f"  {finding.severity:6} {new_path.name}:{signature.line}")
        print(f"         {signature.human()}")
        if finding.reason == DETAIL_CHANGE:
            print(f"         라벨은 같고 대상이 다르다. 레거시: «{finding.hint}»")
            print("         조치: 라우트 변경이면 의도된 것이다. 라벨은 건드리지 않는다")
        elif finding.reason == LABEL_DRIFT:
            print(f"         레거시 유사 항목: «{finding.hint}» -> 표기가 틀어졌다")
            print("         조치: 레거시 원문 표기로 되돌린다")
        else:
            print("         레거시에 대응물 없음 — 없던 것을 만들어냈다")
            print("         조치: 요소를 제거하거나, 의도적이면 allowlist 에 사유와 함께 등록")
        print(f"         key: {signature.key}")
        print()

    if waived:
        print(f"  예외 승인 {len(waived)}건 (allowlist)")
    if mediums and not strict:
        print("  MEDIUM 은 실패로 집계하지 않는다. --strict 로 승격 가능.")


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        prog="check_no_invention",
        description="레거시에 없던 요소가 신규 구현에 등장했는지 검사한다.",
    )
    parser.add_argument("--golden", required=True, help="golden manifest 경로 (JSON)")
    parser.add_argument("--new", required=True, help="신규 구현 마크업 경로")
    parser.add_argument("--allowlist", default=None, help="예외 목록 경로 (JSON)")
    parser.add_argument("--strict", action="store_true", help="MEDIUM 도 실패로 처리")
    parser.add_argument("--no-text", action="store_true", help="정적 텍스트는 비교하지 않음")
    parser.add_argument("--json", action="store_true", help="기계 판독용 JSON 출력")
    args = parser.parse_args(argv)

    new_path = Path(args.new)
    if not new_path.is_file():
        print(f"ERROR 신규 구현 파일 없음: {new_path}", file=sys.stderr)
        return EXIT_ERROR

    try:
        approved = load_allowlist(args.allowlist)
    except AllowlistError as exc:
        print(f"ERROR allowlist: {exc}", file=sys.stderr)
        return EXIT_ERROR

    try:
        golden = load_manifest(args.golden)
    except ManifestError as exc:
        # 정답이 없으면 통과시키지 않는다. 사람이 판단해야 한다.
        print(f"ESCALATE 정답 부재로 판정 불가: {exc}", file=sys.stderr)
        return EXIT_ESCALATE

    include_text = not args.no_text
    new = extract_file(new_path, include_text=include_text)
    if not include_text:
        golden = [sig for sig in golden if sig.kind != "text"]

    findings, waived = detect(golden, new, approved)
    blocking = [
        finding
        for finding in findings
        if finding.severity == "HIGH" or (args.strict and finding.severity == "MEDIUM")
    ]

    if args.json:
        print(
            json.dumps(
                {
                    "gate": "no_invention",
                    "target": str(new_path),
                    "status": "FAIL" if blocking else "PASS",
                    "findings": [finding.to_dict() for finding in findings],
                    "waived": [sig.key for sig in waived],
                },
                ensure_ascii=False,
                indent=2,
            )
        )
    else:
        _report(findings, waived, new_path, args.strict)

    return EXIT_FAIL if blocking else EXIT_PASS


if __name__ == "__main__":
    sys.exit(main())
