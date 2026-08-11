"""게이트: 메뉴 정합성.

메뉴를 테이블로 빼면 메뉴 정합성이 마크업 문제에서 **데이터 문제**로 옮겨간다.
화면마다 헤더/사이드바를 검증할 필요가 없어지고, seed 한 벌만 정답과 대조하면 된다.
레거시가 화면 145개에 같은 메뉴를 include 하던 것을 생각하면 검증량이 1/145 이 된다.

계층 경로로 대조한다. 레거시에는 라벨이 같은 메뉴가 둘 있다
("구매 > 협력업체 관리" 와 "영업 > 협력업체 관리"). 라벨만 보면 하나로 뭉개진다.

seed 는 각 행이 어느 레거시 URL 에서 왔는지(`legacy_url`)를 기록한다. 게이트는 그
출처를 검증한다. 신규 라우트 규칙은 프로젝트마다 다르므로 게이트가 알 필요가 없다.

사용법::

    python -m gates.check_menu_parity \\
        --golden migration/golden/layout/menu.json \\
        --seed   migration/seed/menu-seed.json
"""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

from . import EXIT_ERROR, EXIT_ESCALATE, EXIT_FAIL, EXIT_PASS
from .allowlist import MIN_REASON_LEN
from .extract_menu import flatten, prune_chrome


def _load(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def _seed_rows(seed: dict, placement: str | None = None) -> list[dict]:
    """seed 의 부모 참조를 따라가 계층 경로를 복원한다.

    사이드바와 헤더는 서로 다른 마크업에서 오므로 정답도 따로다.
    `placement` 로 걸러 같은 출처끼리 대조한다.
    """
    by_id = {row["menu_id"]: row for row in seed["menus"]}
    rows = []
    for row in seed["menus"]:
        if placement and row.get("placement") != placement:
            continue
        trail, cursor = [], row
        while cursor is not None:
            trail.append(cursor["label"])
            cursor = by_id.get(cursor["parent_id"]) if cursor["parent_id"] else None
        rows.append(
            {
                "path": " > ".join(reversed(trail)),
                "order": row["sort_order"],
                "legacy_url": row.get("legacy_url"),
                "url": row.get("url"),
                "program_id": row.get("program_id"),
                "exempt": (row.get("program_exempt_reason") or "").strip(),
            }
        )
    return rows


def compare(golden: list[dict], seed: list[dict]) -> list[tuple[str, str]]:
    """(심각도, 메시지) 목록을 돌려준다."""
    gold_by_path = {row["path"]: row for row in golden}
    seed_by_path = {row["path"]: row for row in seed}
    findings: list[tuple[str, str]] = []

    for path in gold_by_path.keys() - seed_by_path.keys():
        findings.append(("HIGH", f"메뉴 누락: «{path}»"))
    for path in seed_by_path.keys() - gold_by_path.keys():
        findings.append(("HIGH", f"메뉴 발명: «{path}» — 레거시에 없다"))

    for path in sorted(gold_by_path.keys() & seed_by_path.keys()):
        gold, row = gold_by_path[path], seed_by_path[path]
        if gold["href"] != row["legacy_url"]:
            findings.append(
                (
                    "HIGH",
                    f"출처 불일치: «{path}» 레거시 {gold['href']!r} "
                    f"인데 seed 는 {row['legacy_url']!r} 로 기록",
                )
            )
        if gold["order"] != row["order"]:
            findings.append(
                ("MEDIUM", f"표시 순서 다름: «{path}» {gold['order']} -> {row['order']}")
            )
        if row["url"] and not row["program_id"]:
            # 사유 없이 비워둔 것과 근거를 대고 비워둔 것은 다르다.
            # allowlist 와 같은 규칙을 쓴다: 예외는 허용하되 기록을 남긴다.
            if not row["exempt"]:
                findings.append(
                    ("MEDIUM", f"권한 미연결: «{path}» — 화면인데 program_id 가 비었다")
                )
            elif len(row["exempt"]) < MIN_REASON_LEN:
                findings.append(
                    (
                        "HIGH",
                        f"사유 부실: «{path}» 권한 면제 사유가 {MIN_REASON_LEN}자 미만이다",
                    )
                )

    order = {"HIGH": 0, "MEDIUM": 1}
    findings.sort(key=lambda item: (order[item[0]], item[1]))
    return findings


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        prog="check_menu_parity", description="레거시 메뉴 트리와 신규 메뉴 seed 를 대조한다."
    )
    parser.add_argument("--golden", required=True, help="메뉴 정답 manifest")
    parser.add_argument("--seed", required=True, help="신규 메뉴 seed")
    parser.add_argument("--placement", default=None, help="대조할 위치 (SIDE / HEADER)")
    parser.add_argument("--strict", action="store_true", help="MEDIUM 도 실패로 처리")
    args = parser.parse_args(argv)

    golden_path, seed_path = Path(args.golden), Path(args.seed)
    if not golden_path.is_file():
        print(f"ESCALATE 메뉴 정답 없음: {golden_path}", file=sys.stderr)
        return EXIT_ESCALATE
    if not seed_path.is_file():
        print(f"ERROR seed 없음: {seed_path}", file=sys.stderr)
        return EXIT_ERROR

    golden = flatten(prune_chrome(_load(golden_path)["items"]))
    seed = _seed_rows(_load(seed_path), args.placement)
    findings = compare(golden, seed)

    where = f"{args.placement} " if args.placement else ""
    blocking = [f for f in findings if f[0] == "HIGH" or args.strict]
    if not findings:
        print(f"PASS  {where}메뉴 {len(golden)}건 정합  ({seed_path.name})")
        return EXIT_PASS

    highs = sum(1 for severity, _ in findings if severity == "HIGH")
    print(f"메뉴 불일치 {len(findings)}건  (HIGH {highs} / MEDIUM {len(findings) - highs})\n")
    for severity, message in findings:
        print(f"  {severity:6} {message}")
    if not args.strict and len(findings) > highs:
        print("\n  MEDIUM 은 실패로 집계하지 않는다. --strict 로 승격 가능.")
    return EXIT_FAIL if blocking else EXIT_PASS


if __name__ == "__main__":
    sys.exit(main())
