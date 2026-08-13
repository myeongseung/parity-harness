"""정답과 이관 결과를 견주는 공통 규칙.

발명(신규에만 있음)과 누락(정답에만 있음)은 **방향만 다르고 판정 규칙이 같다.**
어느 쪽이든 "대응물이 아예 없는가 / 이름이 조금 다른가 / 이름은 같고 대상만 다른가"를
가려야 하며, 셋은 조치가 완전히 다르다.

규칙을 한 곳에 두어 두 게이트가 어긋나지 않게 한다.
"""

from __future__ import annotations

import difflib
from collections import Counter

from .model import DYNAMIC, Signature

#: 이 비율 이상 닮은 라벨이 있으면 없어진/생긴 것이 아니라 표기가 틀어진 것으로 본다.
NEAR_MISS_RATIO = 0.82

#: 대응물이 아예 없다.
NONE = "none"

#: 닮은 라벨이 있다. 표기를 임의로 바꾼 것이다.
LABEL_DRIFT = "label_drift"

#: 라벨이 정확히 같고 보조 키만 다르다. 링크 대상이 바뀐 경우가 대부분이다.
DETAIL_CHANGE = "detail_change"


def surplus(source: list[Signature], other: list[Signature]) -> list[Signature]:
    """`source` 에만 있고 `other` 에는 없는 요소를 돌려준다.

    개수까지 센다. 정답에 버튼이 1개인데 신규에 3개면 2개가 남고, 반대로 정답에
    3개인데 신규에 1개면 2개가 모자란다.

    @param source 기준이 되는 쪽
    @param other 견줄 쪽
    @return 짝을 찾지 못한 `source` 의 요소들. 원래 순서를 지킨다
    """
    budget = Counter(signature.key for signature in other)
    left_over = []
    for signature in source:
        if budget[signature.key] > 0:
            budget[signature.key] -= 1
        else:
            left_over.append(signature)
    return left_over


def classify(signature: Signature, counterparts: list[Signature]) -> tuple[str, str | None]:
    """짝을 찾지 못한 요소가 왜 그런지 가른다.

    @param signature 짝이 없는 요소
    @param counterparts 반대편에서 **짝을 찾지 못한** 요소들. 전체 목록을 넘기면
        안 된다 — 이미 짝이 맞은 요소가 힌트로 잡혀 사람을 엉뚱한 곳으로 보낸다.
        실제로 게시글 목록에서 «dyn» 라벨 링크가 셋인데, 그중 짝이 맞은 것을
        가리켜 "대상이 (없음)으로 바뀌었다"고 보고했다. 진짜 짝은 다른 링크였다.
    @return (사유, 힌트). 힌트는 가장 가까운 반대편 값
    """
    same_role = [
        other
        for other in counterparts
        if other.kind == signature.kind and other.role == signature.role
    ]

    # 라벨이 정확히 같으면 대상만 바뀐 것이다. 라벨을 서버가 채우는 경우(«dyn»)도,
    # 아예 없는 경우(빈 라벨)도 마찬가지다 — 값을 모를 뿐 같은 자리의 같은 요소다.
    #
    # 빈 라벨을 여기서 빼면 «글자 없는 링크의 대상이 바뀐 것»이 «사라졌다»로 잡힌다.
    # 로고 링크가 그렇다 — `<a href="..."><img alt=""></a>` 는 라벨이 없고, 이관하며
    # 주소만 바뀐다. 관리자 헤더를 옮기다 실제로 HIGH 로 잡혔다.
    for other in same_role:
        if other.label == signature.label:
            return DETAIL_CHANGE, other.detail or "(없음)"

    # 닮은 라벨 찾기는 실제 글자가 있어야 뜻이 있다. 양쪽 모두 — 글자 없는 쪽을
    # 후보로 넘기면 difflib 이 빈 문자열과의 유사도를 재게 된다.
    if signature.label in ("", DYNAMIC):
        return NONE, None

    matches = difflib.get_close_matches(
        signature.raw or signature.label,
        [other.raw or other.label for other in same_role if other.label],
        n=1,
        cutoff=NEAR_MISS_RATIO,
    )
    return (LABEL_DRIFT, matches[0]) if matches else (NONE, None)
