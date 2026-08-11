# 01 · 레이아웃과 권한 설계

레거시의 헤더·사이드메뉴를 **데이터로 옮기고**, 화면에 흩어진 권한 검사를
**한 곳으로 모은다.**

관련 결정: [D-002, D-003, D-004, D-005, D-008](00-decisions.md)

---

## 배경 — 무엇이 문제였나

레거시는 세 곳에 사실이 흩어져 있고 서로 맞지 않는다.

```
indexSide.jsp           메뉴 17개 (하드코딩, 145개 화면에 include)
각 화면 JSP 상단         PROGRAM_CODE 상수 (39개 화면)
permission_program_tbl  권한 프로그램 20개
```

**이름으로는 연결되지 않는다.** 메뉴 라벨과 프로그램명이 일치하는 것은 17개 중
7개뿐이다("기안 작성" vs "문서 작성", "게시판" vs "게시판 목록 조회"). 이름으로 맞추려
들면 틀린다.

유일한 정답은 **각 화면에 하드코딩된 `PROGRAM_CODE`** 다. 메뉴가 가리키는 JSP 를 열어
그 상수를 읽으면 매핑이 **17/17 전부 확정**된다. 추측이 필요 없다.

## 왜 디렉터리 접두어로 못 묶는가

"도메인 하나에 권한 하나"면 `/unit/**` 같은 패턴 한 줄로 끝난다. 실제로 `unit` 은
6개 JSP 전부 같은 코드를 쓴다. 그런데 전체를 훑으면 다르다.

| 디렉터리 | 권한 프로그램 |
|---|---|
| `product` | 원재료 관리, 가공품 관리, 출고제품 관리 |
| `bound` | 입고 관리, 출고 관리 |
| `document` | 문서 관리, 문서 작성 |
| `proposal` | 결재 리스트, 결재관리 관리 |
| `task` | 발주 관리, 수주 관리 |

**12개 도메인 중 5개가 내부에 권한이 여러 개다.** 접두어 규칙은 쓸 수 없고,
화면 단위 매핑이 필요하다.

---

## 스키마

세 테이블로 나눈다. 레거시 구조에 그대로 대응한다.

```
program   권한 정의            <- permission_program_tbl
   ^
screen    화면 -> 권한 매핑     <- 각 JSP 의 PROGRAM_CODE (39건)
   ^
menu      표시 구조            <- indexSide.jsp / indexHeader.jsp (27건)
```

### `program` — 권한 정의

| 컬럼 | 설명 |
|---|---|
| `program_id` PK | 레거시 SHA256 값 **보존**. 재생성 금지 ([D-003](00-decisions.md)) |
| `name` | 권한 프로그램명. **메뉴 라벨과 다를 수 있다** ([D-004](00-decisions.md)) |
| `dept_level`, `job_level` | 부서/직급 비트마스크 |

### `screen` — 화면과 권한

| 컬럼 | 설명 |
|---|---|
| `screen_id` PK | |
| `route` UK | 권한 매칭 대상 경로. 쿼리 제외 |
| `legacy_jsp` | 출처. 추적용 |
| `program_id` FK | 필수 |

39건. 목록뿐 아니라 `/unit/register`, `/unit/register-proc` 처럼 **메뉴에 없는
화면까지** 담는다. 메뉴로 들어오든 URL 을 직접 치든 같은 권한이 걸려야 한다.

### `menu` — 표시 구조

| 컬럼 | 설명 |
|---|---|
| `menu_id` PK | |
| `placement` | `SIDE` \| `HEADER` |
| `parent_id` FK | NULL 이면 최상위 |
| `sort_order` | 같은 부모 안에서의 순서 |
| `label` | 화면에 보이는 문구. **레거시 원문 유지** |
| `visibility` | `ALWAYS` \| `ADMIN` |
| `url` | 클릭 시 이동할 주소. **쿼리 포함** (`/company/list?flag=1`) |
| `screen_id` FK | NULL 이면 그룹이거나 권한 대상 아님 |

`url` 과 `screen.route` 를 둘 다 두는 이유: 메뉴 링크는 쿼리가 필요하고
(`?flag=1` 구매 / `?flag=0` 영업), 권한 매칭은 쿼리를 봐선 안 된다.

### `visibility` 가 따로 있는 이유

헤더의 `설정` 메뉴는 권한 프로그램이 아니라 `isAdmin()` 으로 제어된다.

```jsp
<% if (adminTester.isAdmin(session)) { %>
  <a class="dropdown-item" href="/ERFlow/admin/admin.jsp">설정</a>
<% } %>
```

**이 조건은 스크립틀릿 안에 있어 마크업만으로는 알 수 없다.** 전처리가 스크립틀릿을
지우기 때문이다. 추측하지 않고 원본을 확인해 생성기에 고정했다
(`build_menu_seed.py` 의 `ADMIN_ONLY`, 근거 주석에 원본 줄 번호 포함).

---

## 권한 검사 — 화면마다에서 한 곳으로

**레거시:** 39개 JSP 상단에 같은 코드가 반복된다.

```jsp
final String PROGRAM_CODE = "8A4364846CD2FC49...";
if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
    response.sendRedirect("../permissionError.jsp");
    return;
}
```

**신규:** `screen` 테이블이 있으므로 인터셉터 한 곳에서 처리한다.

```
요청 경로 -> screen.route 조회 -> program_id -> 사용자 권한 비교
        -> 미달이면 차단
```

화면 코드에서 권한 코드가 사라진다. 새 화면을 추가하면서 권한 검사를 빠뜨리는
실수도 구조적으로 막힌다 — `screen` 에 행이 없으면 접근 대상이 아니고, 있으면
반드시 검사된다.

**주의:** 이것은 동작 개선이 아니라 **같은 동작의 재배치**여야 한다. 레거시가
막던 것은 막고 통과시키던 것은 통과시켜야 한다. slice 1 에서는 stub
([D-010](00-decisions.md)), 권한 트랙에서 실제 검증한다.

## 렌더링

```
templates/
  layout/base.html          <- 공통 뼈대
  fragments/header.html     <- placement=HEADER
  fragments/side-menu.html  <- placement=SIDE
```

`MenuService.tree(user, placement)` 가 권한으로 걸러진 트리를 돌려주고 fragment 가
렌더링한다. 화면 템플릿은 메뉴를 알 필요가 없다.

---

## 게이트 — 마크업에서 데이터로

메뉴를 테이블로 빼면 정합성 검증의 성격이 바뀐다.

| | 레거시 | 신규 |
|---|---|---|
| 메뉴가 있는 곳 | 145개 화면에 include | 테이블 1벌 |
| 검증 방식 | 화면마다 마크업 대조 | seed 를 정답과 대조 |
| 검증량 | 145회 | **2회** (SIDE / HEADER) |

```bash
python -m gates.check_menu_parity \
    --golden migration/golden/layout/menu.json \
    --seed   migration/seed/menu-seed.json --placement SIDE
```

검사 항목: 누락 / 발명 / 표시 순서 / 출처(`legacy_url`) 일치 / 권한 미연결.

권한 미연결은 사유를 남기면 통과한다(allowlist 와 같은 규칙, 10자 이상).
로그아웃은 액션이라 권한이 없고, 설정은 `visibility=ADMIN` 으로 제어된다 — 둘 다
정당하지만 **근거 없이 비워둔 것과는 구분되어야 한다.**

## 재생성

seed 와 SQL 은 **생성물이다.** 직접 고치지 말고 생성기를 고쳐 다시 만든다.

```bash
# 1. 레거시 레이아웃에서 정답 추출
cd parity-harness
python -m gates.extract_menu --legacy ../legacy/ERFlow/src/main/webapp/indexSide.jsp \
    -o ../migration/golden/layout/menu.json
python -m gates.extract_menu --legacy ../legacy/ERFlow/src/main/webapp/indexHeader.jsp \
    -o ../migration/golden/layout/header.json

# 2. seed + DDL 생성
python ../migration/tools/build_menu_seed.py

# 3. 게이트
python -m gates.check_menu_parity --golden ../migration/golden/layout/menu.json \
    --seed ../migration/seed/menu-seed.json --placement SIDE
python -m gates.check_menu_parity --golden ../migration/golden/layout/header.json \
    --seed ../migration/seed/menu-seed.json --placement HEADER
```

산출물

```
migration/golden/layout/menu.json     사이드 메뉴 정답 (23건, 레거시 원형 그대로)
migration/golden/layout/header.json   헤더 메뉴 정답 (6건)
migration/seed/menu-seed.json         program 20 / screen 39 / menu 27
migration/seed/V2__layout_and_permission.sql   DDL + INSERT
```

정답 파일은 레거시를 **손대지 않고** 담는다. 사용자 위젯(동적 라벨 드롭다운)이나
아이콘 전용 항목을 걷어내는 정규화는 **비교 시점에만** 적용하며, seed 생성기와 게이트가
같은 함수(`prune_chrome`)를 쓴다. 한쪽만 적용하면 걷어낸 항목이 통째로 누락·발명으로
잡힌다.
