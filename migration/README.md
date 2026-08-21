# migration/ — ERFlow 이관 작업 공간

`legacy/ERFlow` (JSP + Servlet, Model 1) → **Spring Boot 4.0.6 / Java 21 / MyBatis 4.0.1 / MariaDB**

```
app/        신규 애플리케이션 (Spring Boot). ./gradlew build
design/     결정 로그와 설계 문서
golden/     레거시에서 기계 추출한 정답. 손으로 쓰지 않는다.
seed/       생성물. 직접 고치지 말고 tools/ 를 고쳐 재생성한다.
tools/      ERFlow 전용 생성기·검사기 (하네스는 범용, 여기는 프로젝트 전용)
legacy-runtime/  레거시를 WAR 로 묶어 띄운다. 실화면 대조용
```

> **완결 (2026-08-21).** 1단계 — 89화면 전부 1:1 정합, 검증 4종 초록.
> 2단계 — 결함 수정 16건(D-100~D-115). 최종 수치: 게이트 PASS ·
> 스타일시트 88/88 · 실화면 그림 같음 57/다름 0/순서 무관 3 · 앱 테스트 348건.
> 디자인 변경·기능 업그레이드는 새 저장소에서 이어진다 —
> [결정 로그의 종결 절](design/00-decisions.md) 참조.
> 아래의 슬라이스 표는 하네스를 만들던 초기 기록으로 남긴다.

| 문서 | 내용 |
|---|---|
| [design/00-decisions.md](design/00-decisions.md) | 결정 로그 (D-001~D-115) + 미결 안건 + 종결 |
| [design/01-menu-layout.md](design/01-menu-layout.md) | 레이아웃·권한 설계 (program / screen / menu) |
| [app/README.md](app/README.md) | 스택·실행법·구성 규칙 |
| [legacy-runtime/README.md](legacy-runtime/README.md) | 레거시 기동과 실화면 대조 |

## 슬라이스 현황

| 순서 | 도메인 | 화면 | golden | 이관 | 게이트 |
|---|---|---|---|---|---|
| — | 스키마 (32테이블·15뷰) | — | 라이브 레거시 | ✅ 복제 | ✅ 행수·정렬 일치 |
| 0 | 레이아웃 (헤더·메뉴) | — | ✅ 29 항목 | ✅ 구현 | ✅ PASS / 발명 0건 |
| 0.5 | **인증·권한** (횡단) | 4 | — | ✅ 구현 | ✅ 해시 42명 재현 / CSRF만 예외 |
| 1 | `unit` (생산 설비 관리) | 3 | ✅ 47 요소 | ✅ 구현 | ✅ **3화면 PASS / 차이 0건** |
| 2 | `company` (**대조군**) | 3 | ✅ 64 요소 | ✅ 구현 | ✅ **3화면 PASS / 차이 1건(라우트)** |
| — | 실화면 대조 (레거시 기동) | 3 | 살아있는 레거시 | — | ✅ **45행 완전 일치** |

## 대조군 측정

`unit` 은 하네스를 만들어 가며 이관했고, `company` 는 완성된 하네스로 이관했다.
두 도메인은 화면 구성이 같아(목록·등록·수정 + 처리 3종) 비교가 공정하다.

| | `unit` | `company` |
|---|---|---|
| **범용 게이트 수정** (`parity-harness/`) | **11회** | **2회** |
| 정답 추출 성공까지 | 6회 수정 | **0회** — 한 번에 |
| 이관 도구 수정 (`migration/tools/`) | — | 1회 |
| 최종 게이트 | PASS · 차이 0 | PASS · 차이 1 (라우트 변경) |

수치보다 **성격의 차이**가 중요하다.

`unit` 에서 고친 11건은 대부분 **판정 자체가 틀린 것**이었다 — "`value` 는 라벨이
아니다", "자식의 시안용 더미가 부모 라벨로 새면 안 된다" 같은. 하나를 고치면 다른
화면도 같이 옳아진다.

`company` 에서 고친 2건은 **처음 만난 문법**이었다 — 라벨이 양쪽 다 `«dyn»` 일 때의
정확 매칭, Thymeleaf `@{/path(a=b)}` 의 괄호 문법. 판정 논리는 건드리지 않았다.

이관 도구 수정 1건은 하네스가 아니라 **도메인 지식**의 문제였다(D-016). 그리고 그것을
드러낸 것은 게이트가 아니라 사람이 원본을 읽은 일이다 — 아래 참조.

## 게이트가 못 잡는 것

`company` 를 시작하며 [D-008 을 철회](design/00-decisions.md)했다. "영업 화면이 구매
권한으로 제어된다"고 기록했는데 틀렸고, 원인은 seed 생성기가 화면 파일에서 **첫
`PROGRAM_CODE` 만 읽은 것**이었다. 같은 실수로 10개 화면이 잘못 매핑돼 있었다.

**게이트는 이것을 잡지 못한다.** 게이트는 "정답 대비 다른가"를 보지 "정답이 맞는가"는
보지 않는다. 정답 추출이 원본의 일부만 보고 있으면 조용히 통과한다.

그래서 생성기를 **모르면 멈추도록** 고쳤다. 코드가 둘 이상인 화면에 판별 규칙이
없으면 그 자리에서 중단한다 — 아무거나 고르면 절반이 엉뚱한 권한을 갖고 그 사실이
드러나지 않는다.

### slice 1 게이트 결과

```
list.html      PASS  차이 0
register.html  PASS  차이 0
update.html    PASS  차이 0
```

게이트는 **템플릿 원본끼리** 대조한다. 렌더링 결과로 대조하면 반복 영역이 데이터
개수만큼 늘어나 15행이면 14개가 발명으로 잡힌다. JSP 도 Thymeleaf 도 반복을 한 번만
적는 템플릿이므로 원본끼리 견주는 편이 맞다.

```bash
cd parity-harness
for f in unitList unitRegister unitUpdate; do
  python -m gates.extract_golden --legacy "../legacy/ERFlow/src/main/webapp/unit/$f.jsp" \
      --screen "unit-${f#unit}" -o "../migration/golden/unit/$f.json"
done
T=../migration/app/src/main/resources/templates/unit
python -m gates.check_no_invention --golden ../migration/golden/unit/unitList.json --new $T/list.html
```

2번은 1번과 화면 구조가 동일하다. **하네스가 완성된 상태에서 이관해 리드타임과 1차 게이트
통과율을 1번과 비교한다.** 이 대조 설계를 깨면 before/after 수치의 근거가 사라진다.

## golden 추출 결과 (slice 1)

| 화면 | 요소 | 구성 |
|---|---|---|
| `unitList` | 29 | 컬럼 8, 검색 콤보 7항목, 버튼 3(삭제/추가/수정), 페이징 3, hidden 3 |
| `unitRegister` | 8 | 입력 5, 버튼 3(관리자 선택/문서 선택/제출) |
| `unitUpdate` | 10 | 입력 3, 콤보 1(멈춤/가동중), 버튼 3, hidden 1 |

재생성:

```bash
cd parity-harness
for f in unitList unitRegister unitUpdate; do
  python -m gates.extract_golden \
    --legacy "../legacy/ERFlow/src/main/webapp/unit/$f.jsp" \
    --screen "unit-${f#unit}" -o "../migration/golden/unit/$f.json"
done
```

## 실제 레거시를 붙이며 드러난 것

가상 fixture 로는 나오지 않던 결함들. 전부 회귀 테스트로 고정했다
(`tests/test_jsp_extraction.py`).

| # | 증상 | 원인 | 조치 |
|---|---|---|---|
| 1 | 자바 코드가 화면 텍스트로 잡힘 | HTML 파서에게 `<%` 는 텍스트 | `preprocess.py` 신설 |
| 2 | 라벨이 «>전체조회» 로 나옴 | `%>` 의 `>` 가 태그를 조기 종료 | 위와 동일 |
| 3 | 삭제 버튼 라벨이 «delete» | `<button>` 의 value 를 텍스트보다 우선 | 버튼은 안쪽 텍스트 우선 |
| 4 | 수정 버튼·페이징 링크가 «dyn» | `data-id`/`href` 의 표현식을 라벨 동적으로 오판 | 동적 판정을 라벨 출처 속성으로 한정 |
| 5 | 콤보 라벨이 옵션 전체 연결 | `<select>` 의 deep_text 사용 | select/textarea 는 안쪽 텍스트를 라벨로 안 씀 |
| 6 | 수정 폼 입력이 전부 «dyn» | `value="<%=name%>"` 를 라벨로 사용 | 일반 입력의 value 는 데이터. name 으로 식별 |

**노이즈 41 → 29건**, 잔여 전부 유효.

### slice 1 에서 추가로 드러난 것

템플릿 원본끼리 대조하려면 JSP 와 Thymeleaf 가 같은 요소를 같은 signature 로 내야 한다.
한쪽만 정규화하면 화면이 통째로 어긋난다.

| # | 증상 | 원인 | 조치 |
|---|---|---|---|
| 7 | 같은 체크박스가 서로 다른 signature | `value="<%=id%>"` 와 `th:value="${id}"` | 전처리에서 Thymeleaf 동적 속성을 같은 모양으로 환원 |
| 8 | `th:text` 의 시안용 더미가 라벨로 잡힘 | 렌더링되면 사라지는 글자 | 동적 텍스트 요소는 정적 텍스트로 세지 않음 |
| 9 | 자식의 더미가 부모 링크 라벨로 새어 올라옴 | `deep_text` 가 자식의 `th:text` 를 모름 | 자식이 동적이면 자리표시자로 대체 |
| 10 | `javascript:` 호출문을 문자열로 비교 | 이동 대상이 아니라 코드 | `javascript:` 로 환원. 동작 검증은 e2e 몫 |
| 11 | 낡은 정답으로 판정 | 추출기를 고치고 정답을 안 뽑음 | manifest 에 추출기 버전 기록, 어긋나면 **ESCALATE(3)** |

11번이 특히 중요하다. 추출 규칙을 바꾸고 정답을 다시 뽑지 않으면 낡은 기준으로
판정하게 되는데, 이는 **"정답이 틀렸는데 통과"** 라 가장 나쁜 실패다.

### 부수 발견 — 레거시의 접근성 결함

`unitUpdate.jsp:68`

```html
<select class="form-select form-select-lg mb-3" aria-label=".form-select-sm example" name="status">
```

`aria-label` 에 Bootstrap 문서의 예제 문구가 그대로 복사돼 있다. 스크린리더가 장비 상태
콤보를 ".form-select-sm example" 로 읽는다. 게이트가 정답을 추출하다 발견했다.

이관 시 `name="status"` 기반으로 교정하면 게이트가 발명으로 잡을 것이다.
**교정이 맞으므로 allowlist 에 사유와 함께 등록한다** — 예외 처리의 정당한 용례다.

## 이관 시 알아둘 것

- **`javax.servlet` → `jakarta.servlet` 전량 치환.** Spring Boot 3+ 는 jakarta 네임스페이스다.
- **라우팅을 새로 설계해야 한다.** 레거시는 URL 이 곧 JSP 경로라 대응 정답이 없다.
  게이트는 화면 내용만 보고, URL 은 별도 매핑표로 추적한다. 섞으면 게이트가 무의미해진다.
- **권한은 첫 슬라이스에서 stub 으로 둔다.** `permissionCon` 이 145개 중 91개 JSP(63%)에
  박혀 있어, 그대로 끌고 가면 첫 슬라이스가 권한 시스템 전체로 번진다.
- **`*Proc.jsp` 는 화면이 아니라 액션이다.** `GET/POST` 한 쌍으로 합쳐진다.
