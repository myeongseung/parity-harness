"""실제 레거시 JSP(ERFlow)를 붙이며 드러난 결함들의 회귀 방지.

가상 fixture 만으로는 나오지 않던 문제들이다. 게이트는 실제 마크업에 붙여봐야
빈틈이 드러난다. 여기 있는 케이스는 전부 legacy/ERFlow 에서 실제로 발생했다.
"""

from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from gates.extract import extract  # noqa: E402
from gates.model import DYNAMIC  # noqa: E402

LEGACY_UNIT = (
    Path(__file__).resolve().parents[2]
    / "legacy" / "ERFlow" / "src" / "main" / "webapp" / "unit"
)


def keys(markup: str) -> list[str]:
    return [sig.key for sig in extract(markup)]


def labels(markup: str, kind: str, role: str) -> list[str]:
    return [
        sig.label for sig in extract(markup) if sig.kind == kind and sig.role == role
    ]


class TestJspSyntax(unittest.TestCase):
    """HTML 파서에게 `<%` 는 태그가 아니라 텍스트다. 전처리 없이는 전부 샌다."""

    def test_scriptlet_does_not_leak_as_visible_text(self) -> None:
        markup = """
        <div>
        <% int totalRecord = activityCon.getUnitCount(keyfield, keyword);
           int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage); %>
        <span>생산 설비 관리</span>
        </div>
        """
        texts = labels(markup, "text", "static")
        self.assertIn("생산 설비 관리", texts)
        self.assertFalse(
            any("totalrecord" in text for text in texts),
            f"자바 코드가 화면 텍스트로 샜다: {texts}",
        )

    def test_directive_and_include_are_stripped(self) -> None:
        markup = '<%@page import="java.util.Vector"%><%@include file="/indexHeader.jsp"%><p>본문</p>'
        texts = labels(markup, "text", "static")
        self.assertEqual(["본문"], texts)

    def test_expression_in_attribute_does_not_close_tag_early(self) -> None:
        """`%>` 의 `>` 가 태그를 조기 종료시켜 라벨 앞에 '>' 가 붙던 결함."""
        markup = (
            '<select name="keyfield">'
            '<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>'
            '<option value="id" <%=keyfield.equals("id") ? "selected" : ""%>>장비ID</option>'
            "</select>"
        )
        self.assertEqual(["전체조회", "장비id"], labels(markup, "option", "item"))

    def test_dynamic_label_becomes_placeholder(self) -> None:
        """라벨 자리에 표현식이 오면 알 수 없으므로 자리표시자로 환원한다."""
        markup = '<input type="submit" value="<%=btnLabel%>">'
        self.assertEqual([f"control:button|{DYNAMIC}|submit"], keys(markup))

    def test_data_value_does_not_become_label(self) -> None:
        """일반 입력의 value 는 데이터다. 이름으로 식별해야 정답이 쓸모를 갖는다."""
        markup = '<input type="text" name="unitName" value="<%=name%>">'
        self.assertEqual(["control:input|unitname|text"], keys(markup))


class TestLabelResolution(unittest.TestCase):
    """무엇이 '사용자가 보는 라벨'인가. 틀리면 게이트가 엉뚱한 값을 비교한다."""

    def test_button_text_wins_over_value(self) -> None:
        """`<button value="delete">삭제</button>` 에서 사용자가 보는 것은 "삭제"다.

        value 는 서버로 보내는 코드값이다. option 과 동일한 함정.
        """
        markup = '<button type="button" name="flag" value="delete">삭제</button>'
        self.assertEqual(["삭제"], labels(markup, "control", "button"))

    def test_submit_input_still_uses_value_as_label(self) -> None:
        """반대로 `<input type="submit">` 은 안쪽 텍스트가 없어 value 가 곧 라벨이다."""
        markup = '<input type="submit" value="조회">'
        self.assertEqual(["조회"], labels(markup, "control", "button"))

    def test_data_attribute_expression_does_not_hide_label(self) -> None:
        """과교정 회귀 방지. data-id 가 동적이어도 라벨은 "수정"이다."""
        markup = '<button type="button" id="update-button" data-id="<%=id%>">수정</button>'
        self.assertEqual(["수정"], labels(markup, "control", "button"))

    def test_href_expression_does_not_hide_link_label(self) -> None:
        """같은 과교정. href 안의 표현식 때문에 "이전"이 «dyn» 이 되던 결함."""
        markup = "<a href=\"javascript:block('<%=pagePerBlock%>', '<%=nowBlock-1%>')\">이전</a>"
        self.assertEqual(["이전"], labels(markup, "nav", "link"))

    def test_select_label_is_not_concatenated_options(self) -> None:
        """옵션 텍스트를 이어붙이면 옵션 하나 변경이 select 까지 흔들어 이중으로 센다."""
        markup = (
            '<select name="keyfield"><option>전체조회</option><option>장비명</option></select>'
        )
        self.assertEqual(["keyfield"], labels(markup, "control", "select"))


class TestTemplateSymmetry(unittest.TestCase):
    """JSP 와 Thymeleaf 가 같은 요소를 같은 signature 로 내야 한다.

    이관 비교는 **템플릿 대 템플릿**이다. 렌더링 결과로 비교하면 반복 영역이 데이터
    개수만큼 늘어나, 행이 15개면 14개가 발명으로 잡힌다. 원본끼리 견주려면 두 문법이
    같은 모양으로 정규화되어야 한다. 한쪽만 정규화하면 모든 화면이 통째로 어긋난다.
    """

    def assertSame(self, jsp: str, thymeleaf: str) -> None:
        self.assertEqual(keys(jsp), keys(thymeleaf))

    def test_dynamic_value_attribute(self) -> None:
        self.assertSame(
            '<input type="checkbox" name="unitId" value="<%=id%>">',
            '<input type="checkbox" name="unitId" th:value="${unit.id}">',
        )

    def test_dynamic_button_label(self) -> None:
        self.assertSame(
            '<input type="submit" value="<%=btn%>">',
            '<input type="submit" th:value="${btn}">',
        )

    def test_static_link(self) -> None:
        self.assertSame(
            '<a href="/unit/register">추가</a>',
            '<a th:href="@{/unit/register}">추가</a>',
        )

    def test_dynamic_cell_has_no_visible_text(self) -> None:
        """`th:text` 안쪽 글자는 디자인 시안용 더미다. 렌더링되면 사라진다."""
        self.assertSame(
            "<td><%=name%></td>",
            '<td th:text="${unit.name}">이름</td>',
        )
        self.assertEqual([], keys('<td th:text="${unit.name}">이름</td>'))

    def test_static_text_still_counted(self) -> None:
        """과교정 방지. 진짜 고정 문구는 양쪽 다 잡혀야 한다."""
        self.assertEqual(["text:static|고정문구|"], keys("<td>고정문구</td>"))

    def test_nested_dynamic_text_does_not_leak_to_parent_label(self) -> None:
        """자식의 시안용 더미가 부모 링크의 라벨이 되면 안 된다.

        ERFlow 페이징에서 실제로 걸렸다. 레거시는 라벨이 «dyn» 인데 신규만 "1" 이
        되어 없던 링크를 만들어낸 것처럼 보였다.
        """
        self.assertSame(
            "<a href=\"javascript:paging('<%=n%>')\"><li><%=n%></li></a>",
            '<a th:href="|javascript:paging(${n})|"><li th:text="${n}">1</li></a>',
        )
        self.assertEqual(
            [f"nav:link|{DYNAMIC}|javascript:"],
            keys('<a th:href="|javascript:paging(${n})|"><li th:text="${n}">1</li></a>'),
        )

    def test_javascript_href_is_normalized(self) -> None:
        """`javascript:` 는 이동할 곳이 아니라 코드다. 호출문을 문자열로 견주지 않는다."""
        self.assertSame(
            "<a href=\"javascript:block('<%=a%>', '<%=b%>')\">이전</a>",
            '<a th:href="|javascript:paging(${p})|">이전</a>',
        )

    def test_real_link_target_is_still_compared(self) -> None:
        """과교정 방지. 진짜 이동 경로는 여전히 구분되어야 한다."""
        self.assertNotEqual(
            keys('<a href="/unit/list">목록</a>'),
            keys('<a href="/unit/other">목록</a>'),
        )


@unittest.skipUnless(LEGACY_UNIT.is_dir(), "legacy/ERFlow 미배치")
class TestAgainstRealLegacy(unittest.TestCase):
    """실제 unitList.jsp 에서 뽑힌 결과를 고정한다."""

    @classmethod
    def setUpClass(cls) -> None:
        cls.sigs = extract(
            (LEGACY_UNIT / "unitList.jsp").read_text(encoding="utf-8", errors="replace")
        )

    def test_action_buttons_are_captured_with_visible_labels(self) -> None:
        buttons = {sig.label for sig in self.sigs if sig.key.startswith("control:button")}
        self.assertTrue({"삭제", "추가", "수정"} <= buttons, buttons)

    def test_search_combo_members_are_captured(self) -> None:
        options = [sig.label for sig in self.sigs if sig.kind == "option"]
        self.assertEqual(
            ["전체조회", "장비id", "장비명", "관리자명", "문서명", "장비 상태", "장비 제조일자"],
            options,
        )

    def test_grid_columns_are_captured(self) -> None:
        headers = [sig.label for sig in self.sigs if sig.kind == "column"]
        self.assertEqual(8, len(headers), headers)
        self.assertTrue(
            {"장비id", "장비명", "관리자", "장비 상태", "장비 제조일자"} <= set(headers)
        )

    def test_no_java_code_in_signatures(self) -> None:
        """스크립틀릿 잔재가 하나라도 남으면 golden 이 오염된다."""
        poison = ("int ", "for (", "activitycon", "request.getparameter", "%>")
        leaked = [
            sig.human()
            for sig in self.sigs
            if any(token in sig.label for token in poison)
        ]
        self.assertEqual([], leaked)


if __name__ == "__main__":
    unittest.main()
