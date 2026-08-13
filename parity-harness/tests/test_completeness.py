"""게이트: 누락 차단.

발명 게이트의 나머지 반쪽이다. 없던 것을 만든 것만 잡고 있던 것을 빠뜨린 것을
놓치면 "레거시와 같다"고 말할 수 없다.

**누락이 더 조용하다.** 없던 버튼이 생기면 눈에 띄지만, 있던 버튼이 사라지면 화면이
멀쩡해 보인다. 그래서 근사 매칭으로 완화하지 않는다.
"""

from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from gates.check_completeness import detect  # noqa: E402
from gates.compare import DETAIL_CHANGE, LABEL_DRIFT, NONE  # noqa: E402
from gates.extract import extract  # noqa: E402


def sigs(markup: str):
    return extract(markup)


class TestCompleteness(unittest.TestCase):

    def test_missing_button_is_high(self) -> None:
        """사라진 버튼은 무조건 막는다. 사용자가 하던 일을 못 하게 된다."""
        golden = sigs('<button type="button">저장</button><button type="button">삭제</button>')
        new = sigs('<button type="button">저장</button>')

        findings, _ = detect(golden, new, {})

        self.assertEqual(1, len(findings))
        self.assertEqual("HIGH", findings[0].severity)
        self.assertEqual(NONE, findings[0].reason)
        self.assertEqual("삭제", findings[0].signature.label)

    def test_nothing_missing_passes(self) -> None:
        markup = '<button type="button">저장</button><th>장비명</th>'
        self.assertEqual([], detect(sigs(markup), sigs(markup), {})[0])

    def test_count_matters(self) -> None:
        """레거시에 3개인데 신규에 1개면 2개가 사라진 것이다."""
        golden = sigs("<th>A</th><th>A</th><th>A</th>")
        new = sigs("<th>A</th>")

        findings, _ = detect(golden, new, {})
        self.assertEqual(2, len(findings))

    def test_renamed_is_not_missing(self) -> None:
        """이름만 바뀐 것은 사라진 것이 아니다. 발명 게이트가 반대편에서 보고한다."""
        golden = sigs("<select><option>경영지원</option></select>")
        new = sigs("<select><option>경영지원부</option></select>")

        findings, _ = detect(golden, new, {})
        self.assertEqual(LABEL_DRIFT, findings[0].reason)
        self.assertEqual("MEDIUM", findings[0].severity)

    def test_route_change_is_not_missing(self) -> None:
        """라벨이 같고 대상만 바뀐 것도 사라진 것이 아니다."""
        golden = sigs('<a href="/ERFlow/unit/unitList.jsp">생산 설비 관리</a>')
        new = sigs('<a href="/unit/list">생산 설비 관리</a>')

        findings, _ = detect(golden, new, {})
        self.assertEqual(DETAIL_CHANGE, findings[0].reason)
        self.assertEqual("MEDIUM", findings[0].severity)

    def test_route_change_without_label_is_not_missing(self) -> None:
        """글자 없는 링크도 마찬가지다 — 로고 링크가 그렇다.

        `<a href="..."><img alt=""></a>` 는 라벨이 없다(장식 이미지라 alt 도 비어
        있다). 이관하면 주소만 바뀌는데, 라벨이 없다는 이유로 짝을 못 찾으면
        «있던 링크가 사라졌다»가 되어 HIGH 로 잡힌다.
        """
        golden = sigs('<a href="/ERFlow/admin/admin.jsp"><img src="/logo.png" alt=""></a>')
        new = sigs('<a href="/admin"><img src="/images/logo.png" alt=""></a>')

        findings, _ = detect(golden, new, {})
        self.assertEqual(1, len(findings))
        self.assertEqual(DETAIL_CHANGE, findings[0].reason)
        self.assertEqual("MEDIUM", findings[0].severity)

    def test_removed_link_without_label_is_still_missing(self) -> None:
        """짝이 아예 없으면 라벨이 없어도 사라진 것이다."""
        golden = sigs('<a href="/ERFlow/admin/admin.jsp"><img src="/logo.png" alt=""></a>')

        findings, _ = detect(golden, [], {})
        self.assertEqual(1, len(findings))
        self.assertEqual(NONE, findings[0].reason)

    def test_missing_column_is_high(self) -> None:
        """그리드 컬럼이 하나 빠지면 그 데이터를 볼 수 없다."""
        golden = sigs("<th>장비ID</th><th>장비명</th><th>관리자</th>")
        new = sigs("<th>장비ID</th><th>장비명</th>")

        findings, _ = detect(golden, new, {})
        self.assertEqual("HIGH", findings[0].severity)
        self.assertEqual("관리자", findings[0].signature.label)

    def test_missing_option_is_high(self) -> None:
        """콤보에서 항목이 빠지면 그 값을 고를 수 없다."""
        golden = sigs("<select><option>전체</option><option>장비명</option></select>")
        new = sigs("<select><option>전체</option></select>")

        findings, _ = detect(golden, new, {})
        self.assertEqual("HIGH", findings[0].severity)

    def test_missing_static_text_is_medium(self) -> None:
        """안내 문구가 사라진 것은 기능 손실보다 가볍다."""
        golden = sigs("<div>안내 문구</div><button>저장</button>")
        new = sigs("<button>저장</button>")

        findings, _ = detect(golden, new, {})
        self.assertEqual("MEDIUM", findings[0].severity)
        self.assertEqual("text", findings[0].signature.kind)

    def test_allowlist_waives_with_reason(self) -> None:
        """의도적으로 뺀 것은 사유와 함께 승인한다."""
        golden = sigs('<button type="button">엑셀 다운로드</button>')
        approved = {"control:button|엑셀 다운로드|button": "레거시 기능이 폐기되어 이관 대상에서 제외한다"}

        findings, waived = detect(golden, sigs("<div></div>"), approved)
        self.assertEqual([], findings)
        self.assertEqual(1, len(waived))


if __name__ == "__main__":
    unittest.main()
