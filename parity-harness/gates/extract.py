"""마크업 -> Signature 추출기.

의도적으로 "모든 요소"를 추출하지 않는다. 순수 레이아웃 wrapper 까지 비교하면
레거시와 신규의 DOM 구조 차이만으로 노이즈가 폭발한다. 사용자가 인지하거나
상호작용하는 요소만 골라낸다.

표준 라이브러리만 사용한다 (git clone 후 바로 실행 가능해야 한다).
"""

from __future__ import annotations

import re
from html.parser import HTMLParser

from .model import DYNAMIC, Signature, norm
from .preprocess import preprocess

VOID_TAGS = {
    "area", "base", "br", "col", "embed", "hr", "img",
    "input", "link", "meta", "param", "source", "track", "wbr",
}

#: 사용자에게 보이지 않는 영역. 통째로 건너뛴다.
IGNORE_TAGS = {
    "script", "style", "head", "meta", "link", "title",
    "base", "noscript", "template", "svg", "path",
}

INTERACTIVE_TAGS = {"button", "input", "select", "textarea", "a"}

#: 위젯 프레임워크가 남기는 흔적. 태그가 div 여도 컨트롤로 취급한다.
_WIDGET_CLASS = re.compile(r"\b(dx-[a-z][\w-]*|ui-[a-z][\w-]*|widget)\b", re.I)
_WIDGET_ATTRS = ("data-widget", "data-role", "data-dx", "data-component")

#: 서버 렌더링/바인딩 표현식. 값을 정적으로 알 수 없으므로 DYNAMIC 으로 환원한다.
#: 요소의 **표시 텍스트**를 서버가 채우는 속성. 라벨을 알 수 없다는 뜻이다.
#: `th:value` 는 여기 없다 — 전처리가 `value="«dyn»"` 로 바꿔 두므로, 그 value 가
#: 라벨이 되는 요소(버튼류)에서만 자연스럽게 DYNAMIC 이 된다.
_DYNAMIC_ATTRS = ("th:text", "th:utext", "data-bind", "v-text")
_DYNAMIC_EXPR = re.compile(r"(\$\{|#\{|<%|\{\{|\[\[)")

#: 버튼처럼 동작하는 input type
_BUTTON_INPUT_TYPES = {"button", "submit", "reset", "image"}

#: 라벨의 출처가 될 수 있는 속성. 동적값 판정은 이 범위 안에서만 한다.
#: data-id 나 href 에 표현식이 있다고 라벨까지 동적인 것은 아니다.
#: `<button data-id="<%=id%>">수정</button>` 의 라벨은 "수정"이다.
_LABEL_ATTRS = ("aria-label", "value", "placeholder", "title", "alt")

#: 안쪽 텍스트가 라벨이 아닌 태그.
#: select 의 텍스트는 option 들이고(각각 별도 signature), textarea 의 텍스트는 기본값이다.
#: 이어붙이면 옵션 하나만 바뀌어도 부모 signature 까지 흔들려 같은 변경을 두 번 센다.
_TEXT_IS_NOT_LABEL = {"select", "textarea"}


class _Node:
    __slots__ = ("tag", "attrs", "children", "text", "line", "parent")

    def __init__(self, tag: str, attrs: dict, line: int, parent=None):
        self.tag = tag
        self.attrs = attrs
        self.children: list[_Node] = []
        self.text: list[str] = []
        self.line = line
        self.parent = parent

    def own_text(self) -> str:
        return " ".join(self.text)

    def deep_text(self) -> str:
        """자손까지 훑어 표시 텍스트를 모은다.

        자식의 글자를 서버가 채우면(`th:text`) 그 자리에 남아 있는 글자는 디자인
        시안용 더미다. 그대로 끌어올리면 부모의 라벨이 더미가 된다 — 레거시의
        `<a><li><%=n%></li></a>` 는 라벨이 «dyn» 인데 신규만 "1" 이 되는 식이다.
        """
        parts = list(self.text)
        for child in self.children:
            if child.tag in IGNORE_TAGS:
                continue
            if any(attr in child.attrs for attr in _DYNAMIC_ATTRS):
                parts.append(DYNAMIC)
            else:
                parts.append(child.deep_text())
        return " ".join(part for part in parts if part)


class _DomBuilder(HTMLParser):
    """레거시 마크업은 닫히지 않은 태그가 흔하다. 관대하게 파싱한다."""

    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.root = _Node("#root", {}, 0)
        self._stack = [self.root]

    def _open(self, tag: str, attrs: list, push: bool) -> None:
        parent = self._stack[-1]
        node = _Node(
            tag.lower(),
            {key.lower(): (value or "") for key, value in attrs},
            self.getpos()[0],
            parent,
        )
        parent.children.append(node)
        if push:
            self._stack.append(node)

    def handle_starttag(self, tag, attrs):
        self._open(tag, attrs, push=tag.lower() not in VOID_TAGS)

    def handle_startendtag(self, tag, attrs):
        self._open(tag, attrs, push=False)

    def handle_endtag(self, tag):
        tag = tag.lower()
        for index in range(len(self._stack) - 1, 0, -1):
            if self._stack[index].tag == tag:
                del self._stack[index:]
                return
        # 짝 없는 닫는 태그는 무시한다.

    def handle_data(self, data):
        if data.strip():
            self._stack[-1].text.append(data)


def _is_dynamic(node: _Node) -> bool:
    if any(attr in node.attrs for attr in _DYNAMIC_ATTRS):
        return True
    if DYNAMIC in node.own_text() or _DYNAMIC_EXPR.search(node.own_text()):
        return True
    # value="<%=id%>" 처럼 라벨 자리에 들어온 동적값도 라벨이 될 수 없다.
    return any(DYNAMIC in node.attrs.get(attr, "") for attr in _LABEL_ATTRS)


def _widget_name(node: _Node) -> str:
    """위젯 표식을 읽되, 잎 요소일 때만 인정한다.

    JS 로 초기화되는 위젯 자리는 `<div id="deptSelect"></div>` 처럼 비어 있다.
    반면 `<div class="dx-field"><input></div>` 나 `<table class="dx-datagrid">`
    는 안쪽 컨트롤/컬럼이 이미 signature 를 만들었으므로, 껍데기까지 세면 신규가
    요소를 두 배로 만든 것처럼 보인다. 이관 시 래퍼 class 는 거의 항상 바뀌므로
    이 규칙이 없으면 충실한 이관도 전부 발명으로 오탐한다.
    """
    if node.children:
        return ""
    for attr in _WIDGET_ATTRS:
        if node.attrs.get(attr):
            return norm(node.attrs[attr])
    match = _WIDGET_CLASS.search(node.attrs.get("class", ""))
    return norm(match.group(0)) if match else ""


def _label_for(
    node: _Node,
    label_map: dict[str, str],
    prefer_text: bool = False,
    use_value: bool = True,
) -> tuple[str, str]:
    """컨트롤의 접근 가능한 이름을 결정한다. 반환값은 (정규화, 원문).

    후보를 순서대로 훑어 처음 채워진 것이 라벨이다. 그 자리에 동적 표현식이
    들어 있으면 라벨을 알 수 없다는 뜻이므로 DYNAMIC 으로 환원한다.

    Args:
        prefer_text: 안쪽 텍스트를 value 보다 우선한다. `<button>` 용이다.
            `<button value="delete">삭제</button>` 에서 사용자가 보는 것은 "삭제"이고
            value 는 서버로 보내는 코드값이다.
        use_value: value 를 라벨 후보로 쓸지. 기본은 쓴다.
            `<input type="submit" value="조회">` 는 안쪽 텍스트가 없어 value 가 곧
            표시 라벨이기 때문이다. 반면 일반 입력의 value 는 *데이터*다.
            `<input type="text" name="unitName" value="<%=name%>">` 를 value 로 읽으면
            수정 폼의 모든 입력이 «dyn» 이 되어 정답이 아무것도 말해주지 않는다.
    """
    # `th:text` 는 요소의 **안쪽 글자**를 서버가 채운다는 뜻이다. 그 글자가 라벨인
    # 요소에서만 라벨을 알 수 없게 만든다.
    #
    # textarea 와 select 는 안쪽 글자가 라벨이 아니다(각각 기본값과 option 들이다).
    # 레거시 `<textarea placeholder="댓글수정"><%=comment%></textarea>` 는 표현식이
    # 텍스트 자리라 라벨이 placeholder 로 잡힌다. 신규가 같은 자리를 `th:text` 로
    # 채웠다고 라벨까지 «dyn» 이 되면, 같은 요소가 서로 다른 signature 를 내
    # 없던 차이가 보고된다.
    if node.tag not in _TEXT_IS_NOT_LABEL and any(
            attr in node.attrs for attr in _DYNAMIC_ATTRS):
        return DYNAMIC, DYNAMIC

    node_id = node.attrs.get("id", "")
    inner = "" if node.tag in _TEXT_IS_NOT_LABEL else node.deep_text()
    candidates = [
        node.attrs.get("aria-label", ""),
        label_map.get(node_id, "") if node_id else "",
    ]
    if prefer_text:
        candidates.append(inner)
    if use_value:
        candidates.append(node.attrs.get("value", ""))
    candidates += [
        node.attrs.get("placeholder", ""),
        node.attrs.get("title", ""),
        node.attrs.get("alt", ""),
        inner,
        node.attrs.get("name", ""),
        node_id,
    ]
    for candidate in candidates:
        if not candidate:
            continue
        if DYNAMIC in str(candidate) or _DYNAMIC_EXPR.search(str(candidate)):
            return DYNAMIC, DYNAMIC
        normalized = norm(candidate)
        if normalized:
            return normalized, " ".join(str(candidate).split())
    return "", ""


def _option_label(node: _Node) -> tuple[str, str]:
    """콤보 항목의 이름을 결정한다.

    option 의 value 는 사용자에게 보이지 않는 코드값이다(예: value="10" -> "경영지원").
    일반 컨트롤과 같은 순서로 value 를 먼저 보면 코드값끼리 비교하게 되어,
    표시 문구가 바뀌어도 통과하고 코드 체계만 바뀌어도 발명으로 오탐한다.
    """
    if _is_dynamic(node):
        return DYNAMIC, DYNAMIC
    for candidate in (node.attrs.get("aria-label", ""), node.attrs.get("label", ""), node.deep_text()):
        normalized = norm(candidate)
        if normalized:
            return normalized, " ".join(str(candidate).split())
    return "", ""


def _href_detail(href: str) -> str:
    """쿼리스트링과 프래그먼트를 떼고 경로만 남긴다.

    `javascript:` 는 이동할 곳이 아니라 코드다. 호출문을 문자열로 견주면 인자 표기나
    함수 분리 같은 구현 차이가 전부 불일치로 잡히는데, 그것은 마크업 정합성이 아니라
    동작의 문제이고 실브라우저 검증이 볼 몫이다. 링크가 있다는 사실만 남긴다.
    """
    if href.strip().lower().startswith("javascript:"):
        return "javascript:"
    path = href.split("#", 1)[0].split("?", 1)[0]
    return norm(path)


def _signature_for(node: _Node, label_map: dict[str, str]) -> Signature | None:
    tag = node.tag
    input_type = norm(node.attrs.get("type", "text")) or "text" if tag == "input" else ""
    # value 가 표시 라벨인 것은 버튼류와 **고칠 수 없는 입력**이다.
    #
    # 편집 가능한 입력의 value 는 데이터다 — 사용자가 지우고 다시 쓴다. 반면
    # readonly/disabled 입력의 value 는 그 자리에 박혀 있는 글자다. 사용자가
    # 읽기만 하므로 화면에 찍힌 라벨과 다를 바 없다.
    #
    # 이것을 보지 않아서 놓친 결함이 있다. 레거시 글쓰기 화면이 게시판 이름을
    # `value="자유게시판"` 으로 박아 두었는데(D-032), 어느 게시판에서 열어도 같은
    # 글자가 뜬다. 답변·수정 화면은 같은 자리에 `<%=boardName%>` 를 쓴다.
    # value 를 안 보면 셋이 전부 `control:input||text` 라 구별되지 않았다.
    fixed_text = "readonly" in node.attrs or "disabled" in node.attrs
    use_value = tag != "input" or input_type in _BUTTON_INPUT_TYPES or fixed_text
    label, raw = _label_for(
        node, label_map, prefer_text=(tag == "button"), use_value=use_value
    )

    if tag == "a":
        href = node.attrs.get("href", "")
        # href 없는 `<a>` 는 이동이 아니라 JS 로 동작하는 버튼이다. 레거시 댓글의
        # "수정하기"·"답글달기" 가 그렇다 — 라벨이 있으면 화면에 보이고 눌린다.
        # `href="#"` 짜리와 사용자에게 같은 것이고 detail 도 똑같이 비므로,
        # 같은 signature 를 낸다. 둘 사이를 오가는 것은 차이가 아니다.
        #
        detail = _href_detail(href)
        # 라벨도 대상도 없으면 `<a href="#comment"></a>` 같은 앵커 표적이다.
        # 화면에 안 보이고, 아무것도 구별하지 못하면서, 신규 구현에는 빈 앵커를
        # 만들라고 강요한다 — 정답이 발명을 요구하는 셈이 된다.
        if not label and not detail:
            return None
        return Signature("nav", "link", label, detail, raw, node.line)

    if tag == "button":
        return Signature("control", "button", label, norm(node.attrs.get("type", "submit")), raw, node.line)

    if tag == "input":
        if input_type == "hidden":
            # hidden 의 value 는 CSRF 토큰처럼 매 요청 달라진다. 값으로 키를 만들면
            # allowlist 등록이 다음 렌더에서 무효가 된다. 이름으로만 식별한다.
            name = node.attrs.get("name") or node.attrs.get("id") or ""
            return Signature("control", "hidden", norm(name), input_type, name, node.line)
        role = "button" if input_type in _BUTTON_INPUT_TYPES else "input"
        return Signature("control", role, label, input_type, raw, node.line)

    if tag in ("select", "textarea"):
        return Signature("control", tag, label, "", raw, node.line)

    if tag == "th":
        return Signature("column", "header", label, "", raw, node.line)

    if tag == "img":
        # alt 가 비어 있으면 장식이다. 접근성 관례가 그렇고, 그 관례가 맞다 —
        # 로고나 여백용 이미지까지 세면 대조가 소음으로 덮인다.
        # alt 가 있으면 정보를 전달하는 이미지다. 첨부파일 아이콘이 그렇다.
        alt = norm(node.attrs.get("alt", "")) or norm(node.attrs.get("title", ""))
        if not alt:
            return None
        # detail 은 src 다. 이관하면서 경로가 바뀌는 것은 정상이며, 그때
        # "라벨은 같고 대상만 다름"으로 분류돼 라벨을 건드리지 않게 된다.
        src = node.attrs.get("src", "")
        raw_alt = node.attrs.get("alt", "") or node.attrs.get("title", "")
        return Signature("media", "image", alt, _href_detail(src), raw_alt, node.line)

    widget = _widget_name(node)
    if widget:
        return Signature("control", "widget", label, widget, raw, node.line)

    # 어떤 signature 도 못 내는 요소인데 title 이 붙어 있으면, 그 글자는 화면에
    # 뜬다(툴팁). 레거시의 돋보기 아이콘 `<i title="검색하기">` 이 그렇다 —
    # 22개 화면에 있는데 추출기가 한 번도 본 적이 없었다.
    #
    # 상호작용 요소는 여기 오지 않는다. 그쪽은 title 을 이미 라벨로 쓴다.
    tooltip = norm(node.attrs.get("title", ""))
    if tooltip and not _is_dynamic(node):
        return Signature("text", "tooltip", tooltip, "", node.attrs["title"], node.line)

    return None


def parse_dom(markup: str) -> _Node:
    """전처리 후 마크업을 관대한 DOM 트리로 파싱한다. 구조를 봐야 하는 추출기용."""
    builder = _DomBuilder()
    builder.feed(preprocess(markup))
    builder.close()
    return builder.root


def extract(markup: str, include_text: bool = True) -> list[Signature]:
    """마크업 문자열에서 의미 있는 요소의 Signature 목록을 뽑는다.

    Args:
        markup: HTML / JSP / ASPX / Thymeleaf 등 HTML 계열 마크업.
        include_text: 정적 표시 텍스트도 추출할지 여부.
    """
    builder = _DomBuilder()
    builder.feed(preprocess(markup))
    builder.close()
    root = builder.root

    label_map: dict[str, str] = {}
    stack = [root]
    while stack:
        node = stack.pop()
        if node.tag == "label" and node.attrs.get("for"):
            label_map[node.attrs["for"]] = node.deep_text()
        stack.extend(node.children)

    found: list[Signature] = []

    def visit(node: _Node) -> None:
        if node.tag in IGNORE_TAGS or "data-parity-ignore" in node.attrs:
            return

        signature = _signature_for(node, label_map)
        if signature is not None:
            found.append(signature)

        if node.tag == "select":
            for child in node.children:
                if child.tag != "option":
                    continue
                label, raw = _option_label(child)
                found.append(Signature("option", "item", label, "", raw, child.line))
            return

        # 이미 의미 있는 signature 를 낸 요소의 텍스트는 그 signature 의 라벨이다.
        # 다시 text:static 으로 세면 같은 요소를 두 번 세는 것이 된다.
        #
        # `th:text` 가 걸린 요소의 안쪽 글자는 디자인 시안용 더미다. 렌더링되면
        # 사라지므로 화면에 보이는 텍스트가 아니다. 레거시의 `<%=name%>` 자리와
        # 같은 것이며, 한쪽만 세면 같은 셀이 서로 다른 signature 를 낸다.
        if (
            include_text
            and signature is None
            and not _is_dynamic(node)
            and node.tag not in INTERACTIVE_TAGS
            and node.tag != "label"
        ):
            text = node.own_text()
            if text and DYNAMIC not in text and not _DYNAMIC_EXPR.search(text):
                normalized = norm(text)
                if normalized:
                    found.append(
                        Signature("text", "static", normalized, "", " ".join(text.split()), node.line)
                    )

        for child in node.children:
            visit(child)

    for child in root.children:
        visit(child)
    return found


def extract_file(path, include_text: bool = True) -> list[Signature]:
    from pathlib import Path

    return extract(Path(path).read_text(encoding="utf-8", errors="replace"), include_text)
