"""메뉴/권한 seed 생성기 (ERFlow 전용).

레거시 세 곳에 흩어진 사실을 하나로 합친다.

    indexSide.jsp            메뉴 계층·순서·라벨·URL
    각 화면 JSP 의 PROGRAM_CODE  메뉴 -> 권한 프로그램 매핑 (이름 추측 불필요)
    permission_program_tbl   권한 레벨

메뉴 라벨과 프로그램명은 일치하지 않는다("기안 작성" vs "문서 작성"). 이름으로
맞추려 들면 틀린다. 화면에 하드코딩된 PROGRAM_CODE 가 유일한 정답이다.

산출물은 seed JSON 과 DDL SQL 이며, 둘 다 생성물이다. 손으로 고치지 말고
이 스크립트를 고쳐서 다시 생성한다.
"""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "parity-harness"))

from gates.extract_menu import flatten, prune_chrome  # noqa: E402

WEBAPP = ROOT / "legacy" / "ERFlow" / "src" / "main" / "webapp"
DB_SQL = ROOT / "legacy" / "ERFlow-DB.sql"
GOLDEN = ROOT / "migration" / "golden" / "layout"
OUT_JSON = ROOT / "migration" / "seed" / "menu-seed.json"
OUT_SQL = ROOT / "migration" / "seed" / "V2__layout_and_permission.sql"

#: 레이아웃 위치별 정답. 사이드바와 헤더는 서로 다른 마크업에서 온다.
SOURCES = (("SIDE", GOLDEN / "menu.json"), ("HEADER", GOLDEN / "header.json"))

_PROGRAM_CODE = re.compile(r'PROGRAM_CODE\s*=\s*"([0-9A-Fa-f]{64})"')
_CONTEXT = "/ERFlow/"
_DYNAMIC = "«dyn»"

#: 표시 조건이 스크립틀릿 안에 있어 마크업만으로는 알 수 없는 항목.
#: 전처리가 `<% if (adminTester.isAdmin(session)) { %>` 를 지우기 때문이다.
#: 자동 추출이 불가능하므로 추측하지 않고, 원본을 확인해 여기 고정한다.
#: 근거: legacy/ERFlow/src/main/webapp/indexHeader.jsp:44
ADMIN_ONLY = {"/ERFlow/admin/admin.jsp"}

#: 한 화면이 요청 파라미터에 따라 서로 다른 권한을 요구하는 경우.
#:
#: 레거시는 화면 안에서 `switch (paramFlag)` 로 PROGRAM_CODE 를 골랐다. 10개 화면이
#: 그렇다. 파일에서 첫 코드만 읽으면 절반이 엉뚱한 권한으로 매핑되므로, 판별 규칙을
#: 확인해 여기 고정한다. 분기 조건은 자바 코드 안에 있어 기계로 뽑을 수 없다.
#:
#: 형식: 레거시 경로 -> (파라미터명, {값: 프로그램명})
CONDITIONAL_PROGRAMS = {
    # company/*.jsp: switch (paramFlag) { case "0": OUT; case "1": IN; }
    "/ERFlow/company/companyList.jsp": ("flag", {"1": "구매 협력업체 관리", "0": "영업 협력업체 관리"}),
    "/ERFlow/company/companyRegister.jsp": ("flag", {"1": "구매 협력업체 관리", "0": "영업 협력업체 관리"}),
    "/ERFlow/company/companyUpdate.jsp": ("flag", {"1": "구매 협력업체 관리", "0": "영업 협력업체 관리"}),
    # task/*.jsp: isSell ? SELL : (isPurchase ? PURCHASE : "")
    "/ERFlow/task/taskRegister.jsp": ("flag", {"sell": "수주 관리", "purchase": "발주 관리"}),
    "/ERFlow/task/taskUpdate.jsp": ("flag", {"sell": "수주 관리", "purchase": "발주 관리"}),
    # bound/*.jsp: isInbound ? INBOUND : OUTBOUND
    # 주의: 레거시는 이 화면들에서 입고·출고 권한을 **둘 다** 요구한 뒤 flag 별로 한 번 더
    # 검사한다. 아래 매핑은 두 번째 검사만 담는다. 첫 번째 조건은 bound 도메인을
    # 이관할 때 함께 옮긴다 — 그때까지 이 경로들은 404 라 영향이 없다.
    "/ERFlow/bound/boundRegister.jsp": ("flag", {"inbound": "입고 관리", "outbound": "출고 관리"}),
    "/ERFlow/bound/boundRegisterProc.jsp": ("flag", {"inbound": "입고 관리", "outbound": "출고 관리"}),
    "/ERFlow/bound/boundUpdate.jsp": ("flag", {"inbound": "입고 관리", "outbound": "출고 관리"}),
    "/ERFlow/bound/boundUpdateProc.jsp": ("flag", {"inbound": "입고 관리", "outbound": "출고 관리"}),
    "/ERFlow/bound/boundDeleteProc.jsp": ("flag", {"inbound": "입고 관리", "outbound": "출고 관리"}),
}

#: 화면이지만 program 권한을 갖지 않는 것들. 사유를 남겨야 게이트가 통과시킨다.
#: 사유 없이 비워두는 것과 근거를 대고 비워두는 것은 다르다.
PROGRAM_EXEMPT = {
    "/ERFlow/login/logoutProc.jsp": "로그아웃은 화면이 아니라 세션 종료 액션이다. 레거시도 권한을 검사하지 않는다.",
    "/ERFlow/admin/admin.jsp": "관리자 전용 화면. program 권한이 아니라 isAdmin 검사로 제어된다(visibility=ADMIN).",
}


def load_programs() -> dict[str, dict]:
    """permission_program_tbl 의 INSERT 문에서 프로그램 목록을 읽는다."""
    sql = DB_SQL.read_text(encoding="utf-8", errors="replace")
    block = re.search(r"INSERT INTO `permission_program_tbl`.*?;", sql, re.S)
    if block is None:
        raise SystemExit("permission_program_tbl INSERT 문을 찾지 못했다")
    rows = re.findall(
        r"\(\d+,\s*'([0-9A-F]+)',\s*'([^']+)',\s*(-?\d+),\s*(-?\d+)\)", block.group(0)
    )
    return {
        pid: {
            "program_id": pid,
            "name": name,
            "dept_level": int(dept),
            "job_level": int(job),
        }
        for pid, name, dept, job in rows
    }


def program_code_of(href: str) -> str | None:
    """메뉴가 가리키는 화면 JSP 에 박힌 PROGRAM_CODE 를 읽는다."""
    path = WEBAPP / href.split("?")[0].replace(_CONTEXT, "")
    if not path.is_file():
        return None
    found = _PROGRAM_CODE.search(path.read_text(encoding="utf-8", errors="replace"))
    return found.group(1).upper() if found else None


def to_route(href: str) -> str:
    """레거시 JSP 경로를 신규 라우트로 옮긴다.

    레거시는 URL 이 곧 파일 경로라 대응 정답이 없다. 그래서 임의 판단을 없애려고
    기계적 규칙을 고정한다.

        /ERFlow/{domain}/{screen}.jsp  ->  /{domain}/{screen}

    화면명이 도메인명으로 시작하면(unit/unitList) 중복이므로 떼어낸다. 단 떼어낸
    자리가 camelCase 경계일 때만이다. `productedProduct` 는 "product" + "edProduct"
    가 아니라 그냥 한 단어이므로 건드리면 안 된다.
    남는 게 없으면(work/work) 도메인 루트로 보낸다. 쿼리스트링은 보존한다
    (`?flag=1` 과 `?flag=0` 은 서로 다른 메뉴다).
    """
    path, _, query = href.replace(_CONTEXT, "").partition("?")
    domain, _, screen = path.removesuffix(".jsp").partition("/")

    if screen.lower().startswith(domain.lower()):
        rest = screen[len(domain):]
        if not rest or rest[0].isupper():
            screen = rest
    if screen.lower() == "index":
        screen = ""

    kebab = re.sub(r"(?<!^)(?=[A-Z])", "-", screen).strip("-").lower()
    route = f"/{domain}/{kebab}" if kebab else f"/{domain}"

    # `?id=<%=headerUserId%>` 처럼 값이 동적인 파라미터는 라우트에 담을 수 없다.
    # 현재 사용자는 서버가 세션에서 채운다.
    keep = [p for p in query.split("&") if p and _DYNAMIC not in p]
    return f"{route}?{'&'.join(keep)}" if keep else route


def _menu_items(placement: str, golden_path: Path) -> list[dict]:
    """정답 트리를 메뉴 테이블에 담을 항목만 남기고 편다.

    걷어내는 규칙은 게이트와 공유한다(`prune_chrome`). 한쪽만 적용하면 걷어낸
    항목이 통째로 누락/발명으로 잡힌다.
    """
    tree = json.loads(golden_path.read_text(encoding="utf-8"))["items"]
    rows = flatten(prune_chrome(tree))
    for row in rows:
        row["placement"] = placement
    return rows


def scan_screens(programs: dict[str, dict]) -> list[dict]:
    """권한 검사를 하는 모든 화면을 훑어 route -> program 매핑을 만든다.

    메뉴만으로는 부족하다. 메뉴는 진입점 17개뿐이고 `/unit/register` 같은 하위
    화면은 메뉴에 없다. 그렇다고 디렉터리 접두어로 묶을 수도 없다 — `product/` 만
    해도 원재료/가공품/출고제품 셋이 서로 다른 권한이다(12개 도메인 중 5개가 그렇다).

    그래서 화면 단위 매핑을 별도 테이블로 둔다. 레거시가 각 JSP 상단에
    PROGRAM_CODE 를 박아둔 구조와 정확히 대응한다.
    """
    by_name = {entry["name"]: pid for pid, entry in programs.items()}
    rows: list[dict] = []

    for path in sorted(WEBAPP.rglob("*.jsp")):
        codes = [
            code.upper()
            for code in _PROGRAM_CODE.findall(path.read_text(encoding="utf-8", errors="replace"))
        ]
        codes = [code for code in dict.fromkeys(codes) if code in programs]
        if not codes:
            continue

        legacy = _CONTEXT + path.relative_to(WEBAPP).as_posix()
        route = to_route(legacy).split("?")[0]

        if len(codes) == 1:
            rows.append(_screen(len(rows) + 1, route, legacy, codes[0], None, None))
            continue

        # 코드가 둘 이상이면 화면 안에서 조건으로 고른다. 규칙을 모르면 멈춘다 —
        # 아무거나 고르면 절반이 엉뚱한 권한을 갖게 되고, 그 사실이 드러나지 않는다.
        rule = CONDITIONAL_PROGRAMS.get(legacy)
        if rule is None:
            raise SystemExit(
                f"{legacy} 는 PROGRAM_CODE 를 {len(codes)}개 쓴다. 어느 조건에서 무엇을 "
                "쓰는지 확인해 CONDITIONAL_PROGRAMS 에 적어야 한다."
            )
        param, mapping = rule
        for value, name in mapping.items():
            program_id = by_name.get(name)
            if program_id is None:
                raise SystemExit(f"{legacy}: 프로그램 «{name}» 을 찾을 수 없다")
            rows.append(_screen(len(rows) + 1, route, legacy, program_id, param, value))
    return rows


def _screen(screen_id, route, legacy, program_id, param_name, param_value) -> dict:
    return {
        "screen_id": screen_id,
        "route": route,
        "legacy_jsp": legacy,
        "param_name": param_name,
        "param_value": param_value,
        "program_id": program_id,
    }


def build() -> dict:
    programs = load_programs()
    screens = scan_screens(programs)
    rows: list[dict] = []
    unresolved: list[str] = []

    def screen_for(url: str | None) -> int | None:
        """메뉴 링크가 가리키는 화면을 찾는다.

        한 경로가 파라미터에 따라 서로 다른 권한을 갖는 경우가 있다
        (`/company/list?flag=1` 구매 / `?flag=0` 영업). 경로만으로 고르면 절반이
        엉뚱한 권한에 붙는다.
        """
        if not url:
            return None
        path, _, query = url.partition("?")
        params = dict(part.split("=", 1) for part in query.split("&") if "=" in part)
        candidates = [row for row in screens if row["route"] == path]
        for row in candidates:
            if row["param_name"] and params.get(row["param_name"]) == row["param_value"]:
                return row["screen_id"]
        for row in candidates:
            if not row["param_name"]:
                return row["screen_id"]
        return None

    for placement, golden_path in SOURCES:
        stack: dict[int, int] = {}
        for item in _menu_items(placement, golden_path):
            menu_id = len(rows) + 1
            # 권한은 화면에 붙는다. 메뉴는 화면을 가리킬 뿐이다.
            screen_id = screen_for(to_route(item["href"]) if item["href"] else None)
            program_id = next(
                (row["program_id"] for row in screens if row["screen_id"] == screen_id), None)
            if item["href"] and program_id is None:
                unresolved.append(f"{placement} / {item['path']}")

            stack[item["depth"]] = menu_id
            rows.append(
                {
                    "menu_id": menu_id,
                    "placement": placement,
                    "parent_id": stack.get(item["depth"] - 1),
                    "sort_order": item["order"],
                    "label": item["label"],
                    "visibility": "ADMIN" if item["href"] in ADMIN_ONLY else "ALWAYS",
                    "icon": item.get("icon"),
                    "separator_before": bool(item.get("separator_before")),
                    "legacy_url": item["href"],
                    "url": to_route(item["href"]) if item["href"] else None,
                    "screen_id": screen_id,
                    "program_id": program_id,
                    "program_exempt_reason": PROGRAM_EXEMPT.get(item["href"] or ""),
                }
            )

    used = {row["program_id"] for row in screens}
    return {
        "schema": "erflow/menu-seed@3",
        "generated_by": "migration/tools/build_menu_seed.py",
        "sources": {
            placement: str(path.relative_to(ROOT)).replace("\\", "/")
            for placement, path in SOURCES
        }
        | {"programs": str(DB_SQL.relative_to(ROOT)).replace("\\", "/")},
        "programs": sorted(programs.values(), key=lambda p: p["name"]),
        "screens": screens,
        "menus": rows,
        "diagnostics": {
            "unresolved_program": unresolved,
            "orphan_programs": sorted(
                programs[pid]["name"] for pid in programs.keys() - used
            ),
        },
    }


def _sql_str(value: object) -> str:
    if value is None:
        return "NULL"
    return "'" + str(value).replace("\\", "\\\\").replace("'", "''") + "'"


def to_sql(seed: dict) -> str:
    lines = [
        "-- 생성물. 직접 고치지 말고 migration/tools/build_menu_seed.py 를 고쳐 재생성한다.",
        "-- 세 테이블 모두 생성물이므로 통째로 다시 만든다. FK 때문에 삭제 순서가 있다.",
        "DROP TABLE IF EXISTS `menu`;",
        "DROP TABLE IF EXISTS `screen`;",
        "DROP TABLE IF EXISTS `program`;",
        "",
        "-- 레거시는 utf8mb4_0900_ai_ci(MySQL 8) 였다. MariaDB 에 없는 collation 이라",
        "-- utf8mb4_general_ci 로 옮긴다. 한글 정렬 순서가 달라질 수 있어 별도 확인 대상이다.",
        "",
        "CREATE TABLE IF NOT EXISTS `program` (",
        "  `program_id` VARCHAR(128)  NOT NULL COMMENT '레거시 SHA256 값 보존. 재생성 금지',",
        "  `name`       VARCHAR(255)  NOT NULL COMMENT '권한 프로그램명. 메뉴 라벨과 다를 수 있다',",
        "  `dept_level` BIGINT        NOT NULL,",
        "  `job_level`  BIGINT        NOT NULL,",
        "  PRIMARY KEY (`program_id`)",
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",
        "",
        "CREATE TABLE IF NOT EXISTS `screen` (",
        "  `screen_id`  INT           NOT NULL,",
        "  `route`      VARCHAR(512)  NOT NULL COMMENT '권한 매칭 대상 경로. 쿼리 제외',",
        "  `param_name`  VARCHAR(64)  NULL COMMENT '같은 경로가 파라미터로 권한이 갈릴 때',",
        "  `param_value` VARCHAR(64)  NULL COMMENT '그 파라미터의 값',",
        "  `legacy_jsp` VARCHAR(512)  NOT NULL COMMENT '출처. 추적용',",
        "  `program_id` VARCHAR(128)  NOT NULL,",
        "  PRIMARY KEY (`screen_id`),",
        "  UNIQUE KEY `ux_screen_route` (`route`, `param_name`, `param_value`),",
        "  CONSTRAINT `fk_screen_program` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`)",
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",
        "",
        "CREATE TABLE IF NOT EXISTS `menu` (",
        "  `menu_id`    INT           NOT NULL,",
        "  `placement`  VARCHAR(16)   NOT NULL COMMENT 'SIDE | HEADER',",
        "  `parent_id`  INT           NULL COMMENT 'NULL 이면 최상위',",
        "  `sort_order` INT           NOT NULL COMMENT '같은 부모 안에서의 표시 순서',",
        "  `label`      VARCHAR(255)  NOT NULL COMMENT '화면에 보이는 문구. 레거시 원문 유지',",
        "  `visibility` VARCHAR(16)   NOT NULL DEFAULT 'ALWAYS' COMMENT 'ALWAYS | ADMIN',",
        "  `icon`       VARCHAR(64)   NULL COMMENT '아이콘 class. 글자가 없어 놓치기 쉬운 표시 요소다',",
        "  `separator_before` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '이 항목 앞에 구분선(<hr>)',",
        "  `url`        VARCHAR(512)  NULL COMMENT '클릭 시 이동할 주소. 쿼리 포함',",
        "  `screen_id`  INT           NULL COMMENT '가리키는 화면. NULL 이면 그룹이거나 권한 대상 아님',",
        "  PRIMARY KEY (`menu_id`),",
        "  KEY `ix_menu_render` (`placement`, `parent_id`, `sort_order`),",
        "  CONSTRAINT `fk_menu_parent` FOREIGN KEY (`parent_id`) REFERENCES `menu` (`menu_id`),",
        "  CONSTRAINT `fk_menu_screen` FOREIGN KEY (`screen_id`) REFERENCES `screen` (`screen_id`)",
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;",
        "",
        "INSERT INTO `program` (`program_id`, `name`, `dept_level`, `job_level`) VALUES",
    ]
    lines.append(
        ",\n".join(
            f"  ({_sql_str(p['program_id'])}, {_sql_str(p['name'])}, "
            f"{p['dept_level']}, {p['job_level']})"
            for p in seed["programs"]
        )
        + ";"
    )
    lines += [
        "",
        "INSERT INTO `screen` (`screen_id`, `route`, `param_name`, `param_value`, "
        "`legacy_jsp`, `program_id`) VALUES",
    ]
    lines.append(
        ",\n".join(
            f"  ({s['screen_id']}, {_sql_str(s['route'])}, "
            f"{_sql_str(s['param_name'])}, {_sql_str(s['param_value'])}, "
            f"{_sql_str(s['legacy_jsp'])}, {_sql_str(s['program_id'])})"
            for s in seed["screens"]
        )
        + ";"
    )
    lines += [
        "",
        "INSERT INTO `menu` (`menu_id`, `placement`, `parent_id`, `sort_order`, "
        "`label`, `visibility`, `icon`, `separator_before`, `url`, `screen_id`) VALUES",
    ]
    lines.append(
        ",\n".join(
            f"  ({m['menu_id']}, {_sql_str(m['placement'])}, "
            f"{m['parent_id'] if m['parent_id'] else 'NULL'}, "
            f"{m['sort_order']}, {_sql_str(m['label'])}, {_sql_str(m['visibility'])}, "
            f"{_sql_str(m['icon'])}, {1 if m['separator_before'] else 0}, "
            f"{_sql_str(m['url'])}, {m['screen_id'] if m['screen_id'] else 'NULL'})"
            for m in seed["menus"]
        )
        + ";"
    )
    return "\n".join(lines) + "\n"


def main() -> int:
    seed = build()
    OUT_JSON.parent.mkdir(parents=True, exist_ok=True)
    OUT_JSON.write_text(
        json.dumps(seed, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )
    OUT_SQL.write_text(to_sql(seed), encoding="utf-8")

    groups = sum(1 for m in seed["menus"] if m["url"] is None)
    linked = sum(1 for m in seed["menus"] if m["screen_id"])
    print(f"menu    {len(seed['menus'])}건 (그룹 {groups} / 링크 {len(seed['menus']) - groups})")
    print(f"        화면 연결 {linked}건, 권한 미해결 "
          f"{len(seed['diagnostics']['unresolved_program'])}건")
    print(f"screen  {len(seed['screens'])}건 (권한 검사하는 화면 전부)")
    print(f"program {len(seed['programs'])}건, 어느 화면도 안 쓰는 것 "
          f"{len(seed['diagnostics']['orphan_programs'])}건")
    for name in seed["diagnostics"]["orphan_programs"]:
        print(f"      · {name}")
    print(f"\n생성: {OUT_JSON.relative_to(ROOT)}")
    print(f"생성: {OUT_SQL.relative_to(ROOT)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
