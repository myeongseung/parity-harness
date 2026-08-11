"""모든 정합성 게이트를 한 번에 돌린다.

CI 와 로컬이 같은 것을 돌려야 한다. 손으로 하나씩 돌리면 빠뜨린 게이트가 생기고,
빠뜨린 줄도 모른다.

검사 순서

1. **정답이 재생산되는가** — 레거시에서 정답을 다시 뽑아 저장소의 것과 대조한다.
   추출 규칙을 고치고 정답을 다시 뽑지 않았다면 여기서 걸린다.
2. **메뉴 정합성** — 레거시 메뉴 트리와 seed 를 대조한다.
3. **발명 차단** — 레거시에 없던 요소가 끼어들었는지 본다.
4. **누락 차단** — 레거시에 있던 요소가 사라졌는지 본다.
5. **비밀 잔존 검사** — 가려야 할 것이 추적 파일에 남아 있는지 본다.

3과 4는 방향만 다른 한 쌍이다. 한쪽만 보면 "레거시와 같다"고 말할 수 없다.

사용법::

    python migration/tools/run_gates.py

exit code 는 게이트 규약을 따른다(0 PASS / 1 FAIL / 2 ERROR / 3 ESCALATE).
여러 게이트 중 가장 나쁜 값을 돌려준다.
"""

from __future__ import annotations

import json
import subprocess
import sys
import tempfile
from pathlib import Path

from screens import (  # noqa: E402  경로 상수와 화면 목록의 단일 출처
    ALLOWLIST, GOLDEN, HARNESS, LAYOUTS, ROOT, SCREENS, SEED, TEMPLATES,
    golden_path, legacy_path, template_path,
)


def run(args: list[str]) -> tuple[int, str]:
    """하네스 모듈을 돌린다."""
    result = subprocess.run(
        [sys.executable, "-m", *args],
        cwd=HARNESS,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    return result.returncode, (result.stdout or "") + (result.stderr or "")


def check_golden_is_reproducible() -> int:
    """저장소의 정답이 레거시에서 다시 뽑은 것과 같은지 본다.

    정답은 생성물이다. 원본에서 다시 뽑았을 때 달라진다면 둘 중 하나다 —
    추출 규칙이 바뀌었는데 정답을 갱신하지 않았거나, 정답을 손으로 고쳤거나.
    어느 쪽이든 그 정답으로 내린 판정을 믿을 수 없다.

    비교는 **판정에 쓰이는 것**만 한다 — 추출기 버전과 signature 목록. `screen` 라벨과
    `source` 경로는 뽑을 때 넘긴 값이라 호출 방식에 따라 달라지며, 그것까지 견주면
    내용이 같은데도 실패한다.
    """
    print("== 정답 재생산 ==")
    worst = 0
    with tempfile.TemporaryDirectory() as tmp:
        for domain, jsp, _ in SCREENS:
            fresh = Path(tmp) / f"{jsp}.json"
            code, output = run([
                "gates.extract_golden",
                "--legacy", str(legacy_path(domain, jsp)),
                "--screen", f"{domain}-{jsp}",
                "-o", str(fresh),
            ])
            if code != 0:
                print(f"  ERROR {jsp}\n{output}")
                worst = max(worst, 2)
                continue

            committed = golden_path(domain, jsp)
            if not committed.is_file():
                print(f"  MISSING {domain}/{jsp}.json")
                worst = max(worst, 3)
                continue

            before = json.loads(committed.read_text(encoding="utf-8"))
            after = json.loads(fresh.read_text(encoding="utf-8"))
            same = (before.get("extractor") == after.get("extractor")
                    and before.get("signatures") == after.get("signatures"))

            print(f"  {'OK   ' if same else 'DRIFT'} {domain}/{jsp}.json")
            if not same:
                print(f"        저장소 {len(before.get('signatures', []))}건 / "
                      f"재생성 {len(after.get('signatures', []))}건")
                print("        extract_golden 을 다시 돌려 정답을 갱신해야 한다")
                worst = max(worst, 3)
    return worst


def check_secrets() -> int:
    """가려야 할 것이 남아 있는지 본다.

    사람이 눈으로 확인하다 세 번 놓쳤다. 매번 돌게 해 둔다.
    검사 대상 목록이 없는 환경(클론 직후 등)에서는 건너뛴다.
    """
    print("== 비밀 잔존 ==")
    checker = ROOT / "migration" / "tools" / "check_no_secrets.py"
    result = subprocess.run(
        [sys.executable, str(checker)], cwd=ROOT, capture_output=True,
        text=True, encoding="utf-8", errors="replace")
    output = (result.stdout + result.stderr).strip()
    print("  " + output.replace("\n", "\n  "))
    # 대상 목록이 없으면(exit 2) 검사할 수 없다. 그것을 실패로 보지는 않는다 —
    # 이미 가려진 저장소를 클론한 사람에게는 목록이 없는 것이 정상이다.
    return 0 if result.returncode in (0, 2) else result.returncode


def check_menus() -> int:
    print("\n== 메뉴 정합성 ==")
    worst = 0
    for golden, placement in LAYOUTS:
        code, output = run([
            "gates.check_menu_parity",
            "--golden", str(GOLDEN / "layout" / f"{golden}.json"),
            "--seed", str(SEED),
            "--placement", placement,
        ])
        print("  " + output.strip().replace("\n", "\n  "))
        worst = max(worst, code)
    return worst


def check_screens(module: str, title: str) -> int:
    """화면별 정합성 게이트를 돌린다.

    @param module 하네스 모듈 이름
    @param title 출력에 쓸 제목
    @return 가장 나쁜 exit code
    """
    print(f"\n== {title} ==")
    worst = 0
    for domain, jsp, template in SCREENS:
        code, output = run([
            module,
            "--golden", str(golden_path(domain, jsp)),
            "--new", str(template_path(domain, template)),
            "--allowlist", str(ALLOWLIST),
        ])
        head = output.strip().splitlines()[0] if output.strip() else "(출력 없음)"
        # 경로가 길어 첫 줄이 지저분해진다. 파일명만 남긴다.
        head = head.replace(str(TEMPLATES), "").replace("\\", "/")
        print(f"  [{code}] {domain}/{template}.html  {head}")
        if code != 0:
            print("      " + output.strip().replace("\n", "\n      "))
        worst = max(worst, code)
    return worst


def main() -> int:
    worst = max(
        check_secrets(),
        check_golden_is_reproducible(),
        check_menus(),
        check_screens("gates.check_no_invention", "발명 차단"),
        check_screens("gates.check_completeness", "누락 차단"),
    )
    verdict = {0: "PASS", 1: "FAIL", 2: "ERROR", 3: "ESCALATE"}.get(worst, "?")
    print(f"\n결과: {verdict} (exit {worst})")
    return worst


if __name__ == "__main__":
    sys.exit(main())
