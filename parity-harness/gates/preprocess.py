"""서버 템플릿 구문을 HTML 파서가 읽을 수 있는 형태로 전처리한다.

HTML 파서에게 `<%` 는 태그가 아니라 그냥 텍스트다. 그래서 JSP 를 그대로 먹이면
두 가지가 동시에 망가진다.

1. 스크립틀릿의 자바 코드가 화면 텍스트로 새어 들어온다.
   `<% int totalRecord = ...; %>` -> text:static «int totalRecord = ...»
2. `%>` 의 `>` 가 태그를 조기 종료시킨다.
   `<option value="" <%=x%>>전체조회</option>` -> 라벨이 «>전체조회» 가 된다.

레거시(JSP)와 신규(Thymeleaf) 양쪽에 동일하게 적용해야 대칭이 유지된다.
표현식은 지우지 않고 DYNAMIC 자리표시자로 남긴다. "여기 값이 하나 출력된다"는
사실 자체는 정합성 판정에 필요하기 때문이다.
"""

from __future__ import annotations

import re

from .model import DYNAMIC

#: Thymeleaf 의 동적 속성을 레거시와 같은 모양으로 되돌린다.
#:
#: 이관 비교는 **템플릿 대 템플릿**이다. 렌더링 결과로 비교하면 반복 영역이
#: 데이터 개수만큼 늘어나, 행이 15개면 14개가 발명으로 잡힌다. JSP 도 Thymeleaf 도
#: 반복 영역을 한 번만 적는 템플릿이므로 원본끼리 견주는 편이 맞다.
#:
#: 그러려면 `value="<%=id%>"` 와 `th:value="${unit.id}"` 가 같은 모양이 되어야 한다.
#: 한쪽만 정규화하면 같은 요소가 서로 다른 signature 를 낸다.
#: `|...|` 는 Thymeleaf 의 리터럴 치환 문법이다. 파이프는 구문이지 내용이 아니다.
_TH_ATTR = re.compile(
    r'\bth:(href|src|value|field|action|placeholder|title|alt)\s*=\s*"[|@]?\{?([^"}]*?)\}?\|?"'
)

# 순서가 중요하다. 포괄적인 <% ... %> 를 마지막에 둔다.
_RULES: tuple[tuple[re.Pattern, str], ...] = (
    (re.compile(r"<%--.*?--%>", re.S), ""),        # JSP 주석
    (re.compile(r"<%@.*?%>", re.S), ""),           # 지시자 (page, include, taglib)
    (re.compile(r"<%!.*?%>", re.S), ""),           # 선언부
    (re.compile(r"<%=.*?%>", re.S), DYNAMIC),      # 표현식 -> 자리표시자
    (re.compile(r"<%.*?%>", re.S), ""),            # 스크립틀릿
    (re.compile(r"\$\{.*?\}", re.S), DYNAMIC),     # EL / Thymeleaf 표현식
    (re.compile(r"#\{.*?\}", re.S), DYNAMIC),      # 메시지 표현식
    (re.compile(r"\[\[.*?\]\]", re.S), DYNAMIC),   # Thymeleaf 인라인
)


def preprocess(markup: str) -> str:
    """JSP/ASP/Thymeleaf 서버 구문을 제거하거나 자리표시자로 환원한다.

    `<%` 나 `${` 가 없는 순수 HTML 에는 아무 영향이 없다.
    """
    for pattern, replacement in _RULES:
        markup = pattern.sub(replacement, markup)
    # 표현식이 이미 자리표시자로 바뀐 뒤에 속성 이름을 정규화한다.
    return _TH_ATTR.sub(r'\1="\2"', markup)
