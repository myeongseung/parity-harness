# parity-harness / ERFlow 이관

레거시를 정답(golden)으로 삼아 이관 결과를 기계적으로 검증하는 프로젝트.

```
legacy/          이관 대상 원본. 정답이다. 절대 수정하지 않는다
parity-harness/  정합성 게이트 (범용)
migration/       ERFlow 이관 (프로젝트 전용)
  app/           신규 Spring Boot 앱
  design/        결정 로그와 설계
  golden/        레거시에서 기계 추출한 정답
  seed/          생성물. 직접 고치지 말고 tools/ 를 고쳐 재생성
  tools/         ERFlow 전용 생성기
```

## 지켜야 할 것

**레거시가 정답이다.** 화면에 보이는 것을 더하거나 빼지 않는다. 동작을 바꿔야 한다면
`migration/design/00-decisions.md` 에 근거를 남긴다. 근거 없는 개선은 이관이 아니다.

**정답은 읽기 전용이다.** 관례가 아니라 권한으로 막혀 있다 — 앱 DB 계정은 레거시
스키마에 `SELECT` 만 갖는다.

**게이트는 정답이 없으면 통과시키지 않는다.** exit code 3(ESCALATE)은 "위반 없음"이
아니라 "판정 불가"다. 이것을 0으로 바꾸지 않는다.

**생성물을 손으로 고치지 않는다.** `seed/` 와 `golden/` 은 생성기가 만든다.
결과가 틀리면 생성기를 고치고 다시 만든다.

**추출 규칙을 바꾸면 정답을 다시 뽑는다.** manifest 에 추출기 버전이 박혀 있어
어긋나면 게이트가 ESCALATE 한다. 낡은 기준으로 판정하는 것이 가장 나쁜 실패다.

## 자주 쓰는 명령

```bash
# 게이트 전부 — 커밋 전에 이것부터 돌린다. CI 가 돌리는 것과 같다
python migration/tools/run_gates.py

# 앱
cd migration/app
./gradlew build                                          # 컴파일 + Checkstyle + 테스트
./gradlew bootRun --args='--spring.profiles.active=local --server.port=18080'

# 하네스
cd parity-harness
python -m unittest discover -s tests -t .

# 정답 추출 -> 게이트
python -m gates.extract_golden --legacy ../legacy/ERFlow/src/main/webapp/unit/unitList.jsp \
    --screen unit-list -o ../migration/golden/unit/unitList.json
python -m gates.check_no_invention --golden ../migration/golden/unit/unitList.json \
    --new ../migration/app/src/main/resources/templates/unit/list.html \
    --allowlist ../migration/allowlist.json

# 메뉴 seed 재생성 -> 게이트
python ../migration/tools/build_menu_seed.py
python -m gates.check_menu_parity --golden ../migration/golden/layout/menu.json \
    --seed ../migration/seed/menu-seed.json --placement SIDE
```

## 알아둘 함정

이미 걸려서 고친 것들이다. 다시 밟지 않는다.

| | |
|---|---|
| **비트마스크 판정** | MariaDB 비트 연산은 부호가 없다. SQL 로 옮기면 관리자 판정이 뒤집힌다. Java 에서 계산한다 |
| **비밀번호 해시** | 레거시 알고리즘을 바꾸면 기존 사용자 전원이 로그인 못 한다 |
| **템플릿 vs 렌더링** | 게이트는 템플릿 원본끼리 대조한다. 렌더링 결과로 대조하면 반복 영역이 데이터 개수만큼 늘어난다 |
| **런타임 주입** | CSRF 처럼 렌더링 시점에 붙는 요소는 템플릿 대조로 안 보인다. 반복 없는 화면만 렌더링 대조 |
| **`value` 는 라벨이 아니다** | `<button value="delete">삭제</button>`. 버튼류 input 만 예외 |
| **`*Proc.jsp`** | 화면이 아니라 액션이다. 화면 게이트 대상이 아니다 |
| **게이트의 한계** | 게이트는 "정답 대비 다른가"만 본다. 정답 추출이 원본의 일부만 보고 있으면 조용히 통과한다. 실제로 한 번 당했다(D-008 철회) |
| **CI 커버리지** | 앱 테스트 53건 중 CI 에서 도는 것은 24건. DB 를 상대로 하는 29건은 로컬에서만 돈다 |

## 접속 정보

`migration/app/src/main/resources/application-local.yml` 에만 둔다. gitignore 되어 있다.
문서·주석·테스트에 비밀번호나 호스트명을 쓰지 않는다.
