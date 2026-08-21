# app/ — ERFlow (Spring Boot)

`legacy/ERFlow` (JSP + Servlet, Model 1) 의 이관 대상 애플리케이션.

## 스택

| | 버전 | 확인 |
|---|---|---|
| Spring Boot | **4.0.6** | 기동 로그에서 Spring Framework 7.0.7 확인 |
| Java | **21** (Temurin 21.0.11) | toolchain 으로 고정 |
| MyBatis Starter | **4.0.1** | |
| MariaDB Driver | 3.5.x | |
| Gradle | 9.7.0 (wrapper) | |
| Checkstyle | 13.10.0 | `check` 에 물려 있음 |
| Tomcat | 11.0.21 (내장) | |

Spring Boot 4.0.x 의 최신 패치는 **4.0.7** 이다. 4.0.6 은
[D-001](../design/00-decisions.md) 로 고정한 값이며, 올릴 때는 결정 로그를 갱신한다.

## 실행

Gradle 은 설치할 필요 없다. wrapper 를 쓴다.

```bash
./gradlew build          # 컴파일 + Checkstyle + 테스트
./gradlew bootRun        # 기동
./gradlew check          # 게이트만
```

포트가 물려 있으면 `--server.port=18080` 으로 바꾼다.

## DB

| | |
|---|---|
| 서버 | MariaDB 10.6.27 |
| 신규 | `erflow_mig` |
| 레거시 | `erflow` — **읽기 전용.** 앱 계정에 SELECT 권한만 있다 |

접속 정보는 `src/main/resources/application-local.yml` 에 두고 **`.gitignore` 로
제외한다.** 커밋하지 않는다. 템플릿을 복사해 값을 채운다.

```bash
cd src/main/resources
cp application-local.yml.example application-local.yml   # 값을 채운다
```

`.example` 쪽에는 값을 적지 않는다 — 그 파일은 커밋된다. 실수로 올라가는 것은
`check_no_secrets.py` 가 막는다(`run_gates.py` 가 매번 돌린다). 문자열 대조와
별개로 **파일 자체가 추적되는지**도 본다. `git add -f` 는 `.gitignore` 를
무시하기 때문이다.

프로필을 켜서 쓴다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
./gradlew test        # application-local.yml 이 없으면 DB 테스트는 건너뛴다
```

환경변수로 줄 수도 있다(`application.yml` 의 기본값 참조).

```bash
ERFLOW_DB_URL=... ERFLOW_DB_USER=... ERFLOW_DB_PASSWORD=... ./gradlew bootRun
```

**레거시를 쓰기로 건드릴 수 없다.** 관례가 아니라 권한이다 —
`CREATE TABLE erflow.__probe` 는 에러 1142 로 거부된다. 자세한 내용은
[D-011](../design/00-decisions.md).

스키마 적용:

```bash
ERFLOW_DB_HOST=... ERFLOW_DB_PORT=... ERFLOW_DB_NAME=erflow_mig \
ERFLOW_DB_USER=... ERFLOW_DB_PASSWORD=... \
python ../tools/apply_sql.py ../seed/V2__layout_and_permission.sql
```

## 구성 규칙

**도메인별 패키지.** 계층별이 아니다. 이관이 도메인 슬라이스 단위로 진행되므로
한 도메인이 한 패키지에 모여 있어야 슬라이스가 통째로 옮겨졌는지 확인된다.

```
com.erflow
  layout/     헤더·사이드메뉴·권한 (전 화면 공통)
  unit/       생산 설비 관리   <- slice 1
  company/    협력업체 관리     <- slice 2 (대조군)
```

계층 의존은 한 방향이다 — `Controller -> Service -> Mapper`. 자세한 규칙은
`src/main/java/com/erflow/package-info.java`.

**SQL 은 XML 매퍼에만 쓴다.** 애너테이션 SQL 을 금지하는 이유는 취향이 아니라,
이관한 쿼리가 레거시의 어느 SQL 에서 왔는지 한곳에서 대조할 수 있어야 하기 때문이다.

**Checkstyle 위반은 빌드 실패다.** 경고로 두면 아무도 안 고친다. 규칙은 스타일 취향이
아니라 이관 중 실제로 문제가 되는 것만 담았다(공개 API Javadoc, 와일드카드 import,
빈 catch). 규칙을 늘리려면 `config/checkstyle/checkstyle.xml` 에 근거를 주석으로 남긴다.

## 현재 상태 — 완결 (2026-08-21)

**89화면 전부 이관됐다.** 화면 목록의 단일 출처는 `../tools/screens.py` 의
`SCREENS` 다. 2단계에서 레거시 결함 16건(D-100~D-115)도 해소했다 — 레거시와
일부러 달라진 자리는 전부 [결정 로그](../design/00-decisions.md)에 있다.
테스트 **348건**(그중 273건이 실제 DB 상대)이 통과한다.

| | |
|---|---|
| 레거시 복제 | 테이블 32개(2,077행) + 뷰 15개 — 행수·정렬 결과 일치 확인 |
| 레이아웃 | `program` 20 / `screen` 39 / `menu` 27 |

레이아웃도 구현했다. `MenuService` 가 `menu` 테이블에서 트리를 읽고 Thymeleaf
fragment 가 그린다. 화면 템플릿은 메뉴를 알 필요가 없다.

```
templates/fragments/head.html        공통 <head> (CSS/JS 순서 유지)
templates/fragments/header.html      placement=HEADER
templates/fragments/side-menu.html   placement=SIDE
```

정적 자산(`css/`, `js/`, `images/`)은 레거시에서 그대로 옮겼다. class 이름을 바꾸면
`aside.css` / `header.css` 가 안 걸려 화면이 무너지므로 손대지 않는다.

인증·권한도 붙였다(Spring Security). 화면마다 반복되던 권한 검사가 사라지고
`screen` 테이블 한 곳에서 판정한다.

| | |
|---|---|
| 비밀번호 | 레거시 해시 1:1 재현. 기존 사용자 그대로 로그인된다 ([D-014](../design/00-decisions.md)) |
| 권한 | 부서·직급 비트마스크. **판정은 Java 에서** — SQL 로 옮기면 뒤집힌다 ([D-015](../design/00-decisions.md)) |
| CSRF | 레거시에 없던 유일한 추가. allowlist 에 사유와 함께 등록 ([D-013](../design/00-decisions.md)) |

로그인은 `/login`. 로그인 후 `/index`(관리자는 `/admin`)로 간다. 유일하게 이관
범위 밖인 화면은 `login/sendOk.html` — 메일 발송(SMTP)이 딸려 있어 일부러
뺐다(O-011). golden 은 화면별로 `../golden/` 아래에 있다.

### 레이아웃 렌더링 확인

`LayoutRenderTest` 가 fragment 를 렌더링해 `build/rendered/` 에 남긴다.
정합성 게이트에 그대로 넣을 수 있다.

```bash
./gradlew test
cd ../../parity-harness
python -m gates.extract_golden --legacy ../legacy/ERFlow/src/main/webapp/indexSide.jsp \
    --screen side-menu -o /tmp/side-golden.json
python -m gates.check_no_invention --golden /tmp/side-golden.json \
    --new ../migration/app/build/rendered/side-menu.html
```

스키마를 다시 복제하려면:

```bash
ERFLOW_DB_HOST=... ERFLOW_DB_PORT=... ERFLOW_DB_USER=... ERFLOW_DB_PASSWORD=... \
python ../tools/clone_legacy_schema.py [--force]
```

`SHOW CREATE VIEW` 에는 `SHOW VIEW` 권한이 필요해 이 작업만 관리 계정으로 돈다.
앱 계정 권한은 그대로 둔다 — 런타임 안전장치이지 도구용이 아니다.
