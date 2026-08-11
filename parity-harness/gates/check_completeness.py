"""게이트: 누락 차단.

레거시에 있던 요소가 신규 구현에서 사라졌으면 실패시킨다.

발명 게이트의 나머지 반쪽이다. 그쪽은 "없던 것을 만들었는가"를 보고 이쪽은
"있던 것을 빠뜨렸는가"를 본다. 한쪽만 있으면 "레거시와 같다"고 말할 수 없다.

**누락이 더 조용하다.** 없던 버튼이 생기면 눈에 띄지만, 있던 버튼이 사라지면
화면이 멀쩡해 보인다. 그 기능을 쓰던 사람만 나중에 안다.

사용법::

    python -m gates.check_completeness --golden golden/x.json --new new/x.html
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

#: 사라지면 사용자가 하던 일을 못 하게 되는 종류.
#: 발명 게이트와 달리 근사 매칭으로 완화하지 않는다 — 사라진 저장 버튼은
#: 비슷한 이름이 어딘가 있다고 해서 덜 심각해지지 않는다.
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
    """정답에만 있는(또는 개수가 모자란) 요소를 찾는다.

    @param golden 레거시에서 뽑은 정답
    @param new 신규 구현에서 뽑은 요소
    @param approved 사유와 함께 승인된 예외
    @return (findings, 예외 승인으로 걸러진 항목들)
    """
    findings: list[Finding] = []
    waived: list[Signature] = []

    for signature in surplus(golden, new):
        if signature.key in approved:
            waived.append(signature)
            continue

        reason, hint = classify(signature, new)
        if reason == NONE and signature.kind in _HIGH_IMPACT_KINDS:
            severity = "HIGH"
        else:
            # 이름이나 대상만 바뀐 것은 사라진 것이 아니다. 같은 결함을 발명
            # 게이트가 반대편에서 이미 보고하므로 여기서는 낮춰 잡는다.
            severity = "MEDIUM"
        findings.append(Finding(signature, severity, reason, hint))

    order = {"HIGH": 0, "MEDIUM": 1}
    findings.sort(key=lambda item: (order[item.severity], item.signature.line))
    return findings, waived


def _report(
    findings: list[Finding], waived: list[Signature], golden_path: Path, strict: bool
) -> None:
    if not findings:
        print(f"PASS  누락 없음  ({golden_path.name})")
        if waived:
            print(f"      예외 승인 {len(waived)}건 (allowlist)")
        return

    highs = sum(1 for finding in findings if finding.severity == "HIGH")
    mediums = len(findings) - highs
    by_reason = Counter(finding.reason for finding in findings)
    breakdown = " / ".join(f"{name} {count}" for name, count in sorted(by_reason.items()))
    print(f"누락 {len(findings)}건  (HIGH {highs} / MEDIUM {mediums})  "
          f"[{breakdown}]  기준 {golden_path.name}\n")

    for finding in findings:
        signature = finding.signature
        print(f"  {finding.severity:6} {signature.human()}")
        if finding.reason == DETAIL_CHANGE:
            print(f"         신규에 같은 라벨이 있고 대상만 다르다: «{finding.hint}»")
            print("         조치: 사라진 것이 아니다. 발명 게이트가 반대편에서 보고한다")
        elif finding.reason == LABEL_DRIFT:
            print(f"         신규 유사 항목: «{finding.hint}» -> 표기가 틀어졌다")
            print("         조치: 레거시 원문 표기로 되돌린다")
        else:
            print("         신규에 대응물 없음 — 있던 것이 사라졌다")
            print("         조치: 요소를 되살리거나, 의도적이면 allowlist 에 사유와 함께 등록")
        print(f"         key: {signature.key}")
        print()

    if waived:
        print(f"  예외 승인 {len(waived)}건 (allowlist)")
    if mediums and not strict:
        print("  MEDIUM 은 실패로 집계하지 않는다. --strict 로 승격 가능.")


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        prog="check_completeness",
        description="레거시에 있던 요소가 신규 구현에서 사라졌는지 검사한다.",
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
                    "gate": "completeness",
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
        _report(findings, waived, Path(args.golden), args.strict)

    return EXIT_FAIL if blocking else EXIT_PASS


if __name__ == "__main__":
    sys.exit(main())
