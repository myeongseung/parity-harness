"""레거시 마크업에서 golden manifest 를 생성한다.

정답은 손으로 쓰지 않는다. 레거시 화면 자체가 정답이므로 기계로 추출한다.
사람이 개입하는 순간 "정답"이 주관이 되고, 게이트의 근거가 무너진다.

사용법::

    python -m gates.extract_golden --legacy legacy/attendance.aspx \\
        --screen attendance-list -o golden/attendance.json
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

from . import EXIT_ERROR, EXIT_PASS
from .extract import extract_file
from .model import dump_manifest


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        prog="extract_golden",
        description="레거시 마크업 -> golden manifest",
    )
    parser.add_argument("--legacy", required=True, help="레거시 화면 마크업 경로")
    parser.add_argument("--screen", required=True, help="화면 식별자 (예: attendance-list)")
    parser.add_argument("-o", "--out", default=None, help="출력 경로 (생략 시 표준출력)")
    parser.add_argument("--no-text", action="store_true", help="정적 텍스트는 수집하지 않음")
    args = parser.parse_args(argv)

    legacy_path = Path(args.legacy)
    if not legacy_path.is_file():
        print(f"ERROR 레거시 파일 없음: {legacy_path}", file=sys.stderr)
        return EXIT_ERROR

    signatures = extract_file(legacy_path, include_text=not args.no_text)
    payload = dump_manifest(signatures, args.screen, str(legacy_path).replace("\\", "/"))

    if args.out:
        out_path = Path(args.out)
        out_path.parent.mkdir(parents=True, exist_ok=True)
        out_path.write_text(payload, encoding="utf-8")
        print(f"golden manifest 생성: {out_path}  (요소 {len(signatures)}건)", file=sys.stderr)
    else:
        sys.stdout.write(payload)
    return EXIT_PASS


if __name__ == "__main__":
    sys.exit(main())
