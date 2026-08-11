"""내비게이션 트리 추출기.

메뉴는 다른 요소와 성격이 다르다. **계층과 순서 자체가 정보**이기 때문에
평면 signature 목록으로 환원하면 "구매 > 협력업체 관리"와 "영업 > 협력업체 관리"가
구분되지 않는다. 그래서 트리를 그대로 뽑는다.

쿼리스트링도 보존한다. `companyList.jsp?flag=1` 과 `?flag=0` 은 각각 구매/영업의
서로 다른 메뉴 항목이다. 일반 링크 signature 는 쿼리를 떼지만 메뉴는 떼면 안 된다.

사용법::

    python -m gates.extract_menu --legacy legacy/.../indexSide.jsp \\
        -o migration/golden/layout/menu.json
"""

from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path

from . import EXIT_ERROR, EXIT_PASS
from .extract import _Node, parse_dom
from .model import DYNAMIC

SCHEMA_ID = "parity-harness/menu-manifest@1"

_LIST_TAGS = {"ul", "ol"}


def _outside_nested_lists(node: _Node):
    """중첩 목록 안으로는 내려가지 않고 후손을 순회한다.

    자식 메뉴는 별도 항목으로 처리해야 하므로, 부모 항목의 라벨/링크를 찾을 때
    자식 목록 내용이 섞여 들어오면 안 된다.
    """
    for child in node.children:
        if child.tag in _LIST_TAGS:
            continue
        yield child
        yield from _outside_nested_lists(child)


def _own_label(li: _Node) -> str:
    parts = list(li.text)
    for node in _outside_nested_lists(li):
        parts.extend(node.text)
    return " ".join(" ".join(parts).split())


def _child_lists(li: _Node) -> list[_Node]:
    return [child for child in li.children if child.tag in _LIST_TAGS]


def _icon_of(anchor: _Node) -> str | None:
    """링크 안의 아이콘 클래스를 찾는다.

    아이콘은 글자가 없어 일반 signature 를 만들지 않는다. 메뉴에서는 사용자가 보는
    요소이므로 따로 붙잡아 두지 않으면 이관 때 조용히 사라진다.
    """
    for node in _outside_nested_lists(anchor):
        if node.tag == "i" and node.attrs.get("class"):
            return " ".join(node.attrs["class"].split())
    return None


def _has_separator_before(li: _Node, anchor: _Node) -> bool:
    """이 링크 바로 앞에 구분선(<hr>)이 있는지 본다."""
    siblings = li.children if anchor in li.children else []
    previous = None
    for node in siblings:
        if node is anchor:
            return previous is not None and previous.tag == "hr"
        previous = node
    return False


def _parse_items(list_node: _Node) -> list[dict]:
    items: list[dict] = []
    for li in list_node.children:
        # 레거시 마크업은 <li> 를 건너뛰고 <a> 를 <ul> 바로 아래 두는 일이 흔하다.
        # 규격 위반이지만 브라우저가 받아주므로 실제로 그렇게 쓰인다.
        if li.tag == "a" and li.attrs.get("href"):
            items.append(
                {
                    "label": " ".join(li.deep_text().split()),
                    "href": li.attrs["href"].strip(),
                    "icon": _icon_of(li),
                    "separator_before": _has_separator_before(list_node, li),
                    "children": [],
                }
            )
            continue
        if li.tag != "li":
            continue

        children: list[dict] = []
        for nested in _child_lists(li):
            children.extend(_parse_items(nested))

        anchors = [
            node
            for node in _outside_nested_lists(li)
            if node.tag == "a" and node.attrs.get("href")
        ]

        if anchors:
            # 하나의 <li> 에 링크가 여러 개인 경우가 있다(레거시 전자결재).
            # 각각을 독립 항목으로 편다. 첫 항목만 자식을 물려받는다.
            for index, anchor in enumerate(anchors):
                items.append(
                    {
                        "label": " ".join(anchor.deep_text().split()),
                        "href": anchor.attrs["href"].strip(),
                        "icon": _icon_of(anchor),
                        "separator_before": _has_separator_before(li, anchor),
                        "children": children if index == 0 else [],
                    }
                )
        else:
            items.append(
                {
                    "label": _own_label(li),
                    "href": None,
                    "icon": None,
                    "separator_before": False,
                    "children": children,
                }
            )
    return items


def extract_menu(markup: str) -> list[dict]:
    """마크업에서 가장 큰 내비게이션 트리를 뽑는다."""
    root = parse_dom(markup)

    candidates: list[_Node] = []

    def visit(node: _Node, inside_list: bool) -> None:
        if node.tag in _LIST_TAGS and not inside_list:
            candidates.append(node)
        for child in node.children:
            visit(child, inside_list or node.tag in _LIST_TAGS)

    visit(root, False)
    if not candidates:
        return []

    trees = [_parse_items(node) for node in candidates]
    return max(trees, key=_count)


def _count(items: list[dict]) -> int:
    return sum(1 + _count(item["children"]) for item in items)


def prune_chrome(items: list[dict]) -> list[dict]:
    """메뉴가 아닌 항목을 걷어낸다.

    정답 파일 자체는 레거시 그대로 둔다. 이 정규화는 **비교 시점에만** 적용하고,
    seed 생성기와 게이트가 같은 함수를 쓴다. 한쪽만 적용하면 걷어낸 항목이 통째로
    누락/발명으로 잡힌다.

    - **동적 라벨** — 헤더 드롭다운 토글은 로그인한 사람의 ID/이름이다. 메뉴가 아니라
      사용자 위젯이므로 자식을 한 단계 끌어올리고 자신은 버린다.
    - **라벨 없는 항목** — 알림 벨처럼 아이콘만 있고 글자도 기능도 없다(href="#").
      메뉴로 다루면 라벨이 빈 항목이 된다. 레이아웃 chrome 으로 남긴다.
    """
    kept: list[dict] = []
    for item in items:
        children = prune_chrome(item["children"])
        label = item["label"].strip()
        if DYNAMIC in label:
            kept.extend(children)
        elif label:
            kept.append({**item, "children": children})
    return kept


def flatten(items: list[dict], trail: tuple[str, ...] = ()) -> list[dict]:
    """트리를 경로 붙은 평면 목록으로 편다. 대조와 seed 생성에 쓴다."""
    rows: list[dict] = []
    for order, item in enumerate(items, start=1):
        path = trail + (item["label"],)
        rows.append(
            {
                "path": " > ".join(path),
                "label": item["label"],
                "href": item["href"],
                "icon": item.get("icon"),
                "separator_before": item.get("separator_before", False),
                "depth": len(path),
                "order": order,
            }
        )
        rows.extend(flatten(item["children"], path))
    return rows


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        prog="extract_menu", description="레거시 내비게이션 -> 메뉴 정답 manifest"
    )
    parser.add_argument("--legacy", required=True, help="메뉴가 들어있는 마크업 경로")
    parser.add_argument("-o", "--out", default=None, help="출력 경로 (생략 시 표준출력)")
    args = parser.parse_args(argv)

    source = Path(args.legacy)
    if not source.is_file():
        print(f"ERROR 파일 없음: {source}", file=sys.stderr)
        return EXIT_ERROR

    items = extract_menu(source.read_text(encoding="utf-8", errors="replace"))
    payload = {
        "schema": SCHEMA_ID,
        "source": str(source).replace("\\", "/"),
        "items": items,
    }
    text = json.dumps(payload, ensure_ascii=False, indent=2) + "\n"

    if args.out:
        out = Path(args.out)
        out.parent.mkdir(parents=True, exist_ok=True)
        out.write_text(text, encoding="utf-8")
        print(f"메뉴 정답 생성: {out}  (항목 {_count(items)}건)", file=sys.stderr)
    else:
        sys.stdout.write(text)
    return EXIT_PASS


if __name__ == "__main__":
    sys.exit(main())
