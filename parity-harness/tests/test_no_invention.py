"""게이트 자체를 검증한다.

게이트는 코드가 아니라 판정 기준이다. 기준이 틀리면 모든 하위 판정이 틀리므로
게이트에는 반드시 자체 테스트가 붙어야 한다. 특히 오탐(충실한 이관을 실패시킴)은
미탐보다 치명적이다. 팀이 게이트를 꺼버리기 때문이다.
"""

from __future__ import annotations

import io
import json
import sys
import tempfile
import unittest
from contextlib import redirect_stdout
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from gates import EXIT_ERROR, EXIT_ESCALATE, EXIT_FAIL, EXIT_PASS  # noqa: E402
from gates.check_no_invention import main as check_main  # noqa: E402
from gates.extract_golden import main as extract_main  # noqa: E402

ROOT = Path(__file__).resolve().parents[1]
FIXTURES = ROOT / "fixtures"
LEGACY = FIXTURES / "legacy" / "attendance.html"
ALLOWLIST = FIXTURES / "allowlist.json"


def run_gate(new_name: str, *extra: str) -> tuple[int, dict]:
    """게이트를 JSON 모드로 실행하고 (exit code, 리포트) 를 돌려준다."""
    argv = [
        "--golden", str(TestNoInvention.golden),
        "--new", str(FIXTURES / "new" / new_name),
        "--json",
        *extra,
    ]
    buffer = io.StringIO()
    with redirect_stdout(buffer):
        code = check_main(argv)
    return code, json.loads(buffer.getvalue())


def labels(report: dict, severity: str) -> set[str]:
    return {
        finding["label"]
        for finding in report["findings"]
        if finding["severity"] == severity
    }


class TestNoInvention(unittest.TestCase):
    golden: Path
    _tmp: tempfile.TemporaryDirectory

    @classmethod
    def setUpClass(cls) -> None:
        # 정답은 손으로 쓰지 않는다. 레거시 화면에서 기계로 뽑는다.
        cls._tmp = tempfile.TemporaryDirectory()
        cls.golden = Path(cls._tmp.name) / "attendance.json"
        code = extract_main(
            ["--legacy", str(LEGACY), "--screen", "attendance-list", "-o", str(cls.golden)]
        )
        assert code == EXIT_PASS, "golden 추출 실패"

    @classmethod
    def tearDownClass(cls) -> None:
        cls._tmp.cleanup()

    def test_golden_captures_meaningful_elements(self) -> None:
        """정답에 컨트롤/컬럼/콤보 항목이 빠짐없이 잡혀야 한다."""
        data = json.loads(self.golden.read_text(encoding="utf-8"))
        keys = {
            f"{s['kind']}:{s['role']}|{s['label']}|{s['detail']}"
            for s in data["signatures"]
        }
        self.assertIn("control:input|사원번호|text", keys)
        self.assertIn("control:select|부서|", keys)
        self.assertIn("control:button|조회|submit", keys)
        self.assertIn("control:button|저장|button", keys)
        self.assertIn("column:header|출근시각|", keys)
        self.assertIn("option:item|경영지원|", keys)
        self.assertEqual(5, sum(1 for key in keys if key.startswith("column:header")))

    def test_faithful_migration_passes(self) -> None:
        """DOM 구조가 완전히 달라도 의미 있는 요소가 같으면 통과해야 한다.

        오탐 제어의 핵심 케이스. 테이블 레이아웃 -> flex, dx-* 래퍼 추가에도
        발명 판정이 나오면 안 된다.
        """
        code, report = run_gate("attendance_pass.html")
        self.assertEqual(EXIT_PASS, code, f"오탐 발생: {report['findings']}")
        self.assertEqual([], report["findings"])

    def test_invention_is_detected(self) -> None:
        """레거시에 없던 버튼/콤보항목은 HIGH, 없던 안내문구는 MEDIUM."""
        code, report = run_gate("attendance_invention.html")
        self.assertEqual(EXIT_FAIL, code)

        high = labels(report, "HIGH")
        self.assertIn("엑셀 다운로드", high)
        self.assertIn("인사", high)

        medium = labels(report, "MEDIUM")
        self.assertTrue(
            any("총 3건" in label for label in medium),
            f"없던 안내문구를 잡지 못했다: {medium}",
        )

    def test_label_drift_is_not_reported_as_invention(self) -> None:
        """"경영지원" -> "경영지원부" 는 발명이 아니라 표기 드리프트다.

        조치가 다르다(요소 제거 vs 원문 복원). 심각도를 섞으면 리포트가 무의미해진다.
        """
        code, report = run_gate("attendance_drift.html")
        self.assertEqual(EXIT_PASS, code)
        self.assertEqual(set(), labels(report, "HIGH"))
        self.assertIn("경영지원부", labels(report, "MEDIUM"))
        drift = next(f for f in report["findings"] if f["label"] == "경영지원부")
        self.assertEqual("경영지원", drift["near_miss"])

    def test_same_label_different_target_is_not_label_drift(self) -> None:
        """라벨이 그대로고 링크 대상만 바뀐 것은 표기 문제가 아니다.

        이관에서 라우트를 새로 설계하면(D-005) 모든 메뉴 링크가 여기 걸린다.
        이걸 "표기가 틀어졌다"고 보고하면 사람이 멀쩡한 라벨을 건드리게 된다.
        """
        from gates.check_no_invention import DETAIL_CHANGE, detect
        from gates.extract import extract

        golden = extract('<a href="/ERFlow/unit/unitList.jsp">생산 설비 관리</a>')
        new = extract('<a href="/unit/list">생산 설비 관리</a>')
        findings, _ = detect(golden, new, {})

        self.assertEqual(1, len(findings))
        self.assertEqual(DETAIL_CHANGE, findings[0].reason)
        self.assertEqual("MEDIUM", findings[0].severity)

    def test_invented_link_is_still_high(self) -> None:
        """대상이 바뀐 것과 없던 것을 만든 것은 구분되어야 한다."""
        from gates.check_no_invention import INVENTION, detect
        from gates.extract import extract

        golden = extract('<a href="/ERFlow/unit/unitList.jsp">생산 설비 관리</a>')
        new = extract('<a href="/unit/stats">생산 통계</a>')
        findings, _ = detect(golden, new, {})

        self.assertEqual(INVENTION, findings[0].reason)
        self.assertEqual("HIGH", findings[0].severity)

    def test_strict_promotes_medium_to_blocking(self) -> None:
        code, _ = run_gate("attendance_drift.html", "--strict")
        self.assertEqual(EXIT_FAIL, code)

    def test_csrf_token_fails_without_allowlist(self) -> None:
        """프레임워크가 필요로 하는 요소여도 승인 없이는 통과시키지 않는다."""
        code, report = run_gate("attendance_csrf.html")
        self.assertEqual(EXIT_FAIL, code)
        self.assertIn("_csrf", labels(report, "HIGH"))

    def test_csrf_token_waived_by_allowlist(self) -> None:
        code, report = run_gate("attendance_csrf.html", "--allowlist", str(ALLOWLIST))
        self.assertEqual(EXIT_PASS, code)
        self.assertIn("control:hidden|_csrf|hidden", report["waived"])

    def test_hidden_key_is_stable_across_token_values(self) -> None:
        """토큰 값이 매 렌더 달라져도 allowlist 등록이 유지되어야 한다."""
        from gates.extract import extract

        first = extract('<input type="hidden" name="_csrf" value="aaa-111">')
        second = extract('<input type="hidden" name="_csrf" value="zzz-999">')
        self.assertEqual(first[0].key, second[0].key)

    def test_stale_golden_escalates(self) -> None:
        """추출 규칙이 바뀌었는데 정답을 다시 뽑지 않았으면 판정하지 않는다.

        낡은 기준으로 통과시키는 것은 "정답이 틀렸는데 통과"라 가장 나쁘다.
        """
        with tempfile.TemporaryDirectory() as tmp:
            stale = Path(tmp) / "stale.json"
            data = json.loads(self.golden.read_text(encoding="utf-8"))
            data["extractor"] = 0
            stale.write_text(json.dumps(data, ensure_ascii=False), encoding="utf-8")

            argv = [
                "--golden", str(stale),
                "--new", str(FIXTURES / "new" / "attendance_pass.html"),
            ]
            with redirect_stdout(io.StringIO()):
                code = check_main(argv)
        self.assertEqual(EXIT_ESCALATE, code)

    def test_missing_golden_escalates_not_passes(self) -> None:
        """정답이 없으면 통과가 아니라 사람 판단으로 넘긴다."""
        argv = [
            "--golden", str(Path(self._tmp.name) / "does-not-exist.json"),
            "--new", str(FIXTURES / "new" / "attendance_pass.html"),
        ]
        with redirect_stdout(io.StringIO()):
            code = check_main(argv)
        self.assertEqual(EXIT_ESCALATE, code)

    def test_allowlist_without_reason_is_config_error(self) -> None:
        """예외는 근거를 남겨야 승인된다. 사유 없는 예외는 게이트를 실패시킨다."""
        with tempfile.TemporaryDirectory() as tmp:
            bad = Path(tmp) / "allowlist.json"
            bad.write_text(
                json.dumps(
                    {
                        "schema": "parity-harness/allowlist@1",
                        "entries": [{"key": "control:button|엑셀 다운로드|button", "reason": "필요"}],
                    },
                    ensure_ascii=False,
                ),
                encoding="utf-8",
            )
            argv = [
                "--golden", str(self.golden),
                "--new", str(FIXTURES / "new" / "attendance_invention.html"),
                "--allowlist", str(bad),
            ]
            with redirect_stdout(io.StringIO()):
                code = check_main(argv)
        self.assertEqual(EXIT_ERROR, code)

    def test_duplicate_control_counts_as_invention(self) -> None:
        """레거시에 1개인 버튼이 신규에 3개면 2개가 발명이다."""
        from gates.extract import extract
        from gates.check_no_invention import detect

        golden = extract('<button type="button">저장</button>')
        new = extract(
            '<button type="button">저장</button>'
            '<button type="button">저장</button>'
            '<button type="button">저장</button>'
        )
        findings, _ = detect(golden, new, {})
        self.assertEqual(2, len(findings))


if __name__ == "__main__":
    unittest.main()
