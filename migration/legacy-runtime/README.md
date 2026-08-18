# legacy-runtime/ — 레거시를 살아있는 정답으로 띄운다

정합성 게이트는 템플릿 원본까지만 본다. 값이 서버에서 채워지는 자리는 `«dyn»` 으로
넘기므로 **표에 실제로 찍히는 문자열**은 보지 못한다. CSS 가 깨졌는지, 화면이 같아
보이는지도 마찬가지다.

그것을 보려면 레거시가 돌아야 한다. **비교할 대상이 있어야 비교가 성립한다.**

## legacy/ 를 고치지 않는다

레거시는 정답이다. 소스를 건드리면 판정 기준이 흔들린다.

Gradle 이 원본을 읽어 `build/patched-src` 로 복사하면서 DB 접속 자리표시자
(`[YOUR_DB_REPOSITORY]` 등) 세 줄만 채운다. 원본은 그대로다.

주입 값은 **태스크 입력으로 선언돼 있다.** 안 하면 소스가 그대로일 때 Gradle 이
복사를 건너뛰고, 접속 대상을 바꿨는데도 예전 스키마를 보는 WAR 가 나온다. 실제로 한 번
당했다 — 그런 WAR 로 비교하면 무엇을 비교했는지 알 수 없다.

## 띄우기

```bash
cd migration/legacy-runtime

export ERFLOW_LEGACY_DB_URL="호스트:포트/스키마"
export ERFLOW_LEGACY_DB_USER="..."
export ERFLOW_LEGACY_DB_PASSWORD="..."

./gradlew deployLegacy       # 통을 받고 + WAR 를 만들고 + 올린다
CATALINA_HOME=$PWD/tomcat tomcat/bin/catalina.bat run
```

`deployLegacy` 가 하는 일은 셋이다.

| | |
|---|---|
| `fetchTomcat` | Tomcat 9 를 받아 `tomcat/` 에 편다. 포트도 옮긴다 |
| `war` | 레거시 소스에 접속 정보를 채워 `build/libs/ERFlow.war` 를 만든다 |
| (복사) | 그 WAR 를 `tomcat/webapps/` 에 올린다 |

**통은 커밋하지 않는다.** 14MB 짜리 바이너리라 git 에 넣을 것이 아니고, 그래서 새로
받은 작업 공간에는 언제나 없다. 없으면 `fetchTomcat` 이 받아 온다 — «레거시를 띄우자»
가 «먼저 Tomcat 을 받으세요» 로 바뀌는 자리를 없애기 위해서다. 이미 있으면 건너뛴다.

자리를 잡았는지는 폴더가 있는지가 아니라 `conf/server.xml` 이 있는지로 본다. 폴더
유무로 보면 한 번 실패해 빈 폴더가 남았을 때 영영 건너뛴다 — 실제로 그렇게 당했다.

WAR 만 필요하면 `./gradlew war` 다.

의존성은 `WEB-INF/lib` 이 비어 있어 소스의 `import` 로 역추적했다 — Gson, JavaMail,
commons-fileupload, MySQL 커넥터 넷뿐이다.

레거시는 Java 17 / Tomcat 9 를 겨냥했지만 여기서는 **21 로 컴파일**한다. 신규 앱과 같은
JDK 를 쓰면 설치를 하나만 두면 되고 Tomcat 9 도 21 위에서 돈다. 레거시 코드는 17 문법을
넘지 않으므로 결과가 같다. 이 프로젝트에서 레거시는 *비교 대상*이지 운영 배포물이 아니다.

## 알아둘 것

포트는 신규 앱(18080)과 겹치지 않게 옮겨 둔다. `fetchTomcat` 이 `conf/server.xml` 에서
`8080 -> 19090`, `8005 -> 19005`, `8009 -> 19009` 로 바꾼다.

| | |
|---|---|
| 레거시 | http://localhost:19090/ERFlow/login/login.jsp |
| 신규 | http://localhost:18080/login |

Tomcat 10 이상으로 못 올린다. 레거시가 `javax.servlet` 을 쓰는데 10 부터
`jakarta.servlet` 이다.

Windows 에서는 `catalina.bat` 을 쓴다. `catalina.sh` 는 `CATALINA_HOME` 에 콜론(`C:`)이
있으면 실행을 거부한다.

로그인 화면은 `/ERFlow/login/login.jsp` 다. `/ERFlow/` 로 바로 들어가면 세션이 없어
권한 오류로 튕긴다 — 레거시 원래 동작이다.

## 비교

**두 앱이 같은 스키마를 보게 한다.** 그래야 데이터 차이가 아니라 화면 차이만 남는다.
정답 스키마(`erflow`)는 읽기 전용으로 두고, 레거시도 이관 대상(`erflow_mig`)을 보게
띄우면 계정 비밀번호 같은 것을 자유롭게 만질 수 있다.

```bash
ERFLOW_TEST_ID=... ERFLOW_TEST_PASSWORD=... \
python ../tools/compare_live.py
```

두 앱에 같은 계정으로 로그인해 같은 화면을 받아 표 내용을 견준다.

```
PASS  생산 설비 관리: 15행 일치
PASS  협력업체 관리 (구매): 15행 일치
PASS  협력업체 관리 (영업): 15행 일치
```
