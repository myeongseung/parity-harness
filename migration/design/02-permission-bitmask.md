# 권한 비트마스크

관리자 화면 ③(권한 관리) 을 옮기기 전에 판정 규칙과 저장 구조를 전부 확인한 기록이다.
**이 프로젝트는 이 자리에서 이미 한 번 데었다** — 비트 연산을 SQL 로 옮겼다가 관리자
판정이 뒤집혔다. 편집 화면은 그 값을 직접 쓰는 곳이므로, 붙이기 전에 무엇이 어디에
어떻게 담기는지부터 적어 둔다.

값은 전부 **라이브 DB 에서 읽어 확인**했다(2026-08-13). 추측이 아니다.

---

## 1. 무엇이 어디에 담기는가

```
permission_dept_tbl   (dept_tbl_id, level, permission)
permission_job_tbl    (job_tbl_id,  level, permission)
permission_program_tbl(program_id, program_name, dept_level, job_level)
```

세 표 모두 `BIGINT` 64비트를 비트마스크로 쓴다.

| 컬럼 | 뜻 |
|---|---|
| `level` | **그 부서/직급 자신을 가리키는 비트 하나.** 신분증이다 |
| `permission` | **그 부서/직급이 가진 비트들.** 자기 비트 + 겸하는 다른 부서/직급의 비트 |
| `dept_level`·`job_level` | **그 프로그램에 들어올 수 있는 부서/직급 비트들** |

### 실제 값

부서 8개가 비트 0~7, 직급 9개가 비트 0~8 을 나눠 갖는다. `id = -1` 인 «관리자» 행이
따로 있고 그것이 **비트 63**(`Long.MIN_VALUE`) 이다.

```
dept  11 인사관리부  level=1(bit0)    perm=1
dept  12 영업관리부  level=2(bit1)    perm=2
...
dept  -1 관리자      level=bit63      perm=bit63

job    1 사원        level=1(bit0)    perm=3(bit0,1)   <- 대리 비트를 겸한다
job    2 대리        level=2(bit1)    perm=2
...
job   -1 최고관리자   level=bit63      perm=bit63
```

`permission` 이 `level` 과 다른 유일한 행이 «사원» 이다. 누군가 수정 화면에서 사원에게
대리를 체크해 준 것이며, 그 결과 사원은 대리에게 열린 프로그램에도 들어간다.

프로그램 20개는 모두 `dept_level`·`job_level` 에 **비트 63 이 켜져 있다.** 관리자는
언제나 통과한다는 뜻이고, 아래 4절에서 보듯 편집 화면이 그렇게 만들도록 되어 있다.

---

## 2. 판정 규칙 — 셋뿐이다

```java
// 관리자인가
(deptPermission & Long.MIN_VALUE) == Long.MIN_VALUE
        && (jobPermission & Long.MIN_VALUE) == Long.MIN_VALUE

// 이 프로그램에 들어갈 수 있는가
(program.deptLevel & user.deptPermission) != 0
        && (program.jobLevel & user.jobPermission) != 0

// 이 게시판을 읽을/쓸 수 있는가 — board_tbl 의 네 컬럼으로 같은 계산
(board.permissionReadDeptLevel & user.deptPermission) != 0 && ...
```

부서와 직급이 **둘 다** 걸려야 한다. 한쪽만 맞으면 못 들어간다.

### 왜 Java 에서 계산하는가

MariaDB 의 비트 연산 결과는 **부호 없는** `BIGINT` 다. 라이브 DB 에서 확인했다.

```sql
SELECT -9223372036854775808 & -9223372036854775808;   -- 9223372036854775808
```

같은 식을 Java 로 하면 `-9223372036854775808` 이다. 그래서 관리자 판정을 SQL 로 옮기면
**관리자가 0명**이 된다(실제로 세어 봤다). 레거시는 이 계산을 Java 에서 했고 이관도
Java 에서 한다. 매퍼는 값만 읽어 오고 계산하지 않는다 — `com.erflow.auth.Permissions`.

**편집 화면도 같은 규칙을 따른다.** 체크박스 조합(`|`)과 체크 여부 판정(`&`)을 전부
Java 에서 하고, SQL 에는 계산이 끝난 값 하나만 넘긴다.

---

## 3. 화면 8개가 무엇을 바꾸는가

| 화면 | 바꾸는 것 |
|---|---|
| `jobDeptList` | 없음(목록). 직급·부서 두 표를 한 화면에 나란히 |
| `jobRegister` | `job_tbl` 삽입 + `permission_job_tbl` 에 **새 비트** 한 줄 |
| `deptRegister` | `dept_tbl` 삽입 + `permission_dept_tbl` 에 **새 비트** 한 줄 |
| `jobUpdate` | 직급명 + `permission_job_tbl.permission` |
| `deptUpdate` | 부서명·주소 + `permission_dept_tbl.permission` |
| `programList` | 없음(목록) |
| `programDeptUpdate` | `permission_program_tbl.dept_level` |
| `programJobUpdate` | `permission_program_tbl.job_level` |
| (`jobDeleteProc`·`deptDeleteProc`) | 두 표에서 행 삭제 |

### 체크박스가 값이 되는 방식

수정 화면은 **자기 자신을 뺀** 다른 부서/직급을 체크박스로 늘어놓고, 켜진 것을 `|` 로
합쳐 저장한다. 자기 비트는 조합 전에 미리 넣어 둔다.

```java
result |= 내 level;                       // 자기 비트는 언제나 켜진다
for (체크된 id : permissions) {
    result |= 그 id 의 level;
}
```

프로그램 쪽은 시작값이 다르다.

```java
long result = Long.MIN_VALUE;             // 관리자 비트로 시작한다
for (체크된 id : permissions) { ... }
```

그래서 **관리자는 어떤 프로그램에서도 빠지지 않는다.** 체크를 다 지워도 비트 63 은
남는다. 화면에 «관리자» 체크박스가 없는 것도 같은 이유다 — 목록 조회가 `dept_id <> -1`
로 관리자 행을 빼기 때문이다.

### 새 비트를 고르는 방식

```java
public long next(String flag) {
    long permission = 모든 level 의 OR;
    for (int i = 0; i < 64 && permission != 0L; ++i) {
        if ((permission & 1) == 0) { result = 1 << i; i = 64; }
        permission >>= 1;
    }
}
```

가장 낮은 빈 비트를 준다. 지금 데이터로 돌리면 부서는 `256`(비트 8) 이 나온다 — 맞다.

---

## 4. 발견한 결함

이관 전에 전부 확인했다. **고치지 않고 옮기는 것이 기본**이며, 예외는 근거를 적는다.

### (가) `next()` 는 부서가 31개가 되면 관리자를 만들어 낸다

`1 << i` 에서 `1` 이 `int` 다. `i` 가 31이면 결과가 `Integer.MIN_VALUE` 이고, 그것을
`long` 으로 넓히면 **비트 31~63 이 전부 켜진 값**이 된다. 비트 63 은 관리자 비트다.

시뮬레이션으로 확인했다(자바 셈법 그대로).

| 이미 찬 비트 | `next()` 결과 |
|---|---|
| 0~7 (지금) | `256` — 정상 |
| 0~29 | `1073741824` — 정상 |
| 0~30 | `-2147483648` — **비트 31~63. 새 부서가 관리자가 된다** |
| 0~31 | `1` — **첫 부서와 같은 비트. 남의 권한을 그대로 물려받는다** |

지금은 부서 8개, 직급 9개라 닿지 않는다. 23개를 더 만들어야 터진다.

### (나) 삭제해도 비트를 걷어내지 않는다

`deleteDept` 는 `permission_dept_tbl` 의 그 행만 지운다. 다른 부서의 `permission` 과
프로그램의 `dept_level` 에는 그 비트가 그대로 남는다. 나중에 새 부서를 만들면
`next()` 가 **그 비트를 다시 준다** — 새 부서가 사라진 부서의 권한을 물려받는다.

걷어내는 코드(`revokePermission`)가 있긴 하다. **어느 화면도 부르지 않는다.** 게다가
그 SQL 은 비트 연산을 DB 에서 한다(`permission & ~?`) — 2절의 그 함정 위에 있다.

### (다) 프로그램 검색은 건수와 목록이 어긋난다

```java
getProgramCount : where program_name = ?          // 완전 일치
getPrograms     : where program_name like '%?%'   // 부분 일치
```

«결재» 로 검색하면 목록에는 나오는데 건수는 0이라 페이지가 그려지지 않는다. D-052
(결재 리스트)와 같은 부류다.

### (라) 부서·직급 만들기가 트랜잭션이 아니다

`dept_tbl` 삽입과 `permission_dept_tbl` 삽입이 따로 커밋된다. 사이에서 끊기면 **권한
행이 없는 부서**가 남는다. 그런 부서의 사용자는 `permission` 조회가 0이라 아무 화면도
못 본다.

### (마) 프로그램 권한 수정 폼이 GET 이다

`<form action="programDeptUpdateProc.jsp">` — `method` 가 없다. 주소만 알면 링크
한 번으로 프로그램 권한이 바뀐다.

### (바) 목록의 «번호» 는 부서 번호가 아니다

`jobDeptList` 가 찍는 번호는 `permission_job_tbl.id`(권한 행 번호)이고, 수정 링크에
싣는 값은 `job_tbl.id` 다. 둘이 다르다.

---

## 5. 이관하며 정해야 할 것

### 프로그램 권한을 어느 표에 쓸 것인가 — **이 슬라이스의 핵심 결정**

레거시는 `permission_program_tbl` 을 읽고 쓴다. 우리 앱은 **읽는 표가 다르다.**

```
레거시   permission_program_tbl.dept_level  <- 화면이 고친다
신규     program.dept_level                 <- ScreenAuthorizationManager 가 읽는다
```

`program` 은 `build_menu_seed.py` 가 `permission_program_tbl` 에서 뽑아 만든 **생성물**
이고, seed SQL 은 그 표를 `DROP` 후 다시 만든다. 그대로 두고 편집 화면만 붙이면 둘 중
하나가 된다.

- `permission_program_tbl` 에 쓰면 → **화면은 «수정했습니다» 라고 하는데 아무것도 안 바뀐다**
- `program` 에 쓰면 → seed 를 다시 적용하는 순간 관리자가 바꾼 값이 통째로 사라진다

권한 편집은 «생성물을 손으로 고치지 않는다» 는 규칙과 정면으로 부딪힌다. 프로그램
권한은 이관 시점의 스냅샷이 아니라 **운영 중에 바뀌는 데이터**이기 때문이다.

**결정: 레거시 표를 읽고 쓴다(D-063).** `program` 표에서 권한 두 칸을 뺐다 — 이름표와
FK 대상만 남는다. 부서·직급·게시판 권한이 이미 레거시 표를 직접 보고 있었고,
프로그램만 사본이었다.

### 그 밖에 — 전부 결정했다

| 안건 | 결정 |
|---|---|
| `next()` 의 `int` 결함(4-가) | **고친다**(D-060). 지금 데이터로는 닿지 않는 자리이고, 재현하면 31번째 부서가 관리자가 된다 |
| 삭제해도 비트가 남는 것(4-나) | **그대로 옮긴다**(D-062). 지금도 일어나는 일이라 고치면 화면에 보이는 권한이 달라진다 |
| 검색 건수/목록 불일치(4-다) | **그대로 옮긴다**(D-064) |
| 만들기가 트랜잭션이 아닌 것(4-라) | **묶는다**(D-061) |
| GET 폼(4-마) | **POST 로 바꾼다**(D-065) |
| 목록의 «번호» 가 권한 행 id 인 것(4-바) | **그대로 옮긴다** |

---

## 6. 무엇으로 확인할 것인가

게이트는 이 자리를 **거의 못 본다.** 체크박스는 양쪽에 다 있고, 달라지는 것은 저장되는
숫자다. 그래서 시험으로 박는다.

| 확인할 것 | 방법 |
|---|---|
| 체크 조합이 같은 비트를 만드는가 | 서비스 시험(롤백) |
| 자기 비트가 언제나 켜지는가 | 서비스 시험 |
| 프로그램에서 관리자 비트가 안 빠지는가 | 서비스 시험 |
| 관리자 판정이 뒤집히지 않는가 | `Permissions` 단위 시험(이미 있음) |
| 새 부서가 빈 비트를 받는가 | 서비스 시험(롤백) |
| 권한을 바꾸면 실제로 화면 접근이 달라지는가 | MockMvc 시험 |
