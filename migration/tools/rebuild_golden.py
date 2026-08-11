"""정답을 레거시에서 다시 뽑아 저장소에 덮어쓴다.

추출 규칙을 바꾸면 정답도 다시 뽑아야 한다. 안 하면 게이트가 ESCALATE 하고,
그것을 무시하면 낡은 기준으로 판정하게 된다 — 가장 나쁜 실패다.

지금까지 이것을 손으로 했다. 화면이 늘어나면 빠뜨린다. 그래서 명령 하나로 만든다.

사용법::

    python migration/tools/rebuild_golden.py            # 다시 뽑아 덮어쓴다
    python migration/tools/rebuild_golden.py --check    # 달라지는지만 본다 (쓰지 않음)

`--check` 는 CI 용이다. 정답이 원본에서 재생산되지 않으면 실패한다.
"""

from __future__ import annotations

import argparse
import json
import subprocess
import sys
import tempfile
from pathlib import Path

from screens import GOLDEN, HARNESS, ROOT, SCREENS, golden_path, legacy_path


def extract_one(domain: str, jsp: str, out: Path) -> tuple[int, str]:
    """하네스 추출기를 돌려 정답 하나를 만든다."""
    result = subprocess.run(
        [sys.executable, "-m", "gates.extract_golden",
         "--legacy", str(legacy_path(domain, jsp)),
         "--screen", f"{domain}-{jsp}",
         "-o", str(out)],
        cwd=HARNESS, capture_output=True, text=True, encoding="utf-8", errors="replace")
    return result.returncode, (result.stdout or "") + (result.stderr or "")


def judged_part(manifest: dict) -> tuple:
    """판정에 실제로 쓰이는 부분만 뽑는다.

    `screen` 과 `source` 는 뽑을 때 넘긴 값이라 호출 방식에 따라 달라진다.
    그것까지 견주면 내용이 같은데도 다르다고 나온다.
    """
    return (manifest.get("extractor"), manifest.get("signatures"))


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        prog="rebuild_golden",
        description="레거시에서 정답을 다시 뽑는다.")
    parser.add_argument("--check", action="store_true",
                        help="쓰지 않고 달라지는지만 본다 (CI 용)")
    args = parser.parse_args(argv)

    changed, failed = [], []
    with tempfile.TemporaryDirectory() as tmp:
        for domain, jsp, _, _done in SCREENS:
            fresh_path = Path(tmp) / f"{domain}-{jsp}.json"
            code, output = extract_one(domain, jsp, fresh_path)
            if code != 0:
                print(f"  ERROR {domain}/{jsp}\n{output}")
                failed.append(f"{domain}/{jsp}")
                continue

            fresh = json.loads(fresh_path.read_text(encoding="utf-8"))
            # 추출기는 넘겨준 경로를 그대로 적는다. 절대경로가 박히면 기계마다
            # 정답이 달라지고, 공개 저장소에 남의 디렉터리 구조가 들어간다.
            fresh["source"] = legacy_path(domain, jsp).relative_to(ROOT).as_posix()
            target = golden_path(domain, jsp)
            before = (json.loads(target.read_text(encoding="utf-8"))
                      if target.is_file() else None)

            name = target.relative_to(GOLDEN).as_posix()
            # 판정 내용이 달라진 것과, 파일을 다시 써야 하는 것은 별개다.
            # `source` 경로만 정규화되는 경우는 판정에 영향이 없으므로 --check 를
            # 실패시키지 않는다. 그래도 파일은 갱신해야 한다.
            content_changed = before is None or judged_part(before) != judged_part(fresh)
            needs_write = before is None or before != fresh

            if content_changed:
                was = len(before["signatures"]) if before else 0
                changed.append(f"{name}  {was} -> {len(fresh['signatures'])}건")

            if args.check:
                print(f"  {'DRIFT  ' + changed[-1] if content_changed else '같음   ' + name}")
                continue

            if not needs_write:
                print(f"  같음   {name}")
                continue

            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(
                json.dumps(fresh, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
            print(f"  갱신   {changed[-1] if content_changed else name + '  (경로 정규화)'}")

    if failed:
        print(f"\nERROR 추출 실패 {len(failed)}건: {', '.join(failed)}")
        return 2
    if args.check and changed:
        print(f"\nFAIL  정답 {len(changed)}건이 원본에서 재생산되지 않는다.")
        print("      python migration/tools/rebuild_golden.py 로 다시 뽑는다.")
        return 1
    print(f"\n{'변경 없음' if not changed else f'{len(changed)}건 갱신'} (화면 {len(SCREENS)}개)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
