# legacy/ — 이관 대상 원본 (수정 금지)

이 디렉터리는 **정답(golden)** 이다. 여기 있는 파일은 읽기만 한다.
한 줄이라도 고치면 정합성 판정의 기준 자체가 흔들린다.

## 출처

| | |
|---|---|
| 프로젝트 | ERFlow — JSP + Servlet 기반 ERP/그룹웨어 |
| 원저작자 | jUqItEr |
| 라이선스 | MIT (`LICENSE` 참조) |
| 원본 개발 | 2023-10-11 ~ 2023-11-10 |
| 경유 | https://github.com/myeongseung/JSPERP (원본 소스는 zip 으로 보관되어 있어 풀어서 배치) |

`parity-harness` 는 이 코드를 **입력**으로 쓴다. 이관 대상 앱이지 이 저장소의 성과물이 아니다.

## 구성

```
ERFlow/src/main/java/      134 파일 / 16,844 LOC
  controller/              25  JSP 가 <jsp:useBean> 으로 직접 생성하는 POJO 파사드
  service/implementation/  25  raw JDBC + PreparedStatement
  model/                   22  Bean
  driver/                   7  자체 커넥션 풀, 암호화, ID 생성
ERFlow/src/main/webapp/    145 JSP
ERFlow-DB.sql              47 테이블. **저장소에 없다** — 아래 참조
```

## 저장소에 없는 것

`ERFlow-DB.sql` 은 커밋하지 않는다. 사용자 55명의 이름·주민등록번호·주소·휴대전화·
이메일이 들어 있다. "레거시는 수정하지 않는다"는 이 프로젝트의 원칙이지만, 그 원칙이
개인정보를 공개하는 근거가 될 수는 없다.

이관에 필요한 것은 권한 프로그램 20건뿐이고 거기에는 개인정보가 없다.
`migration/tools/extract_programs.py` 가 뽑아 `migration/seed/programs.json` 으로
커밋한다. 스키마와 데이터는 라이브 DB 에서 복제한다(`clone_legacy_schema.py`).

메일 발송 코드의 자격증명과 인프라 호스트명도 가렸다
(`migration/tools/redact_legacy.py`). 정합성 게이트가 보는 화면 마크업은 그대로다.

## 구조상 알아둘 것

- **Model 1.** 라우팅 계층이 없다. URL 이 곧 JSP 파일 경로다.
- **`*Proc.jsp` 는 화면이 아니다.** 처리 전용이라 렌더링 결과가 없다.
  화면 정합성 게이트의 대상으로 넣으면 전량 오탐한다.
- **권한이 횡단한다.** `permissionCon` 이 145개 중 91개 JSP(63%)에 박혀 있다.
- **`javax.servlet` 네임스페이스.** Spring Boot 3+ 이관 시 `jakarta.*` 전량 치환 대상.

## 슬라이스 진행

| 순서 | 도메인 | 화면 | 역할 |
|---|---|---|---|
| 1 | `unit` | unitList / unitRegister / unitUpdate | 하네스 만들면서 이관 (기준선) |
| 2 | `company` | companyList / companyRegister / companyUpdate | 완성된 하네스로 이관 (**대조군**) |

두 도메인의 구조가 동일하다. before/after 측정이 공정하려면 이 대조 설계를 유지해야 한다.
