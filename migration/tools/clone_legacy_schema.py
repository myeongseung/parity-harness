"""레거시 스키마와 데이터를 신규 DB로 복제한다.

`erflow` (레거시) -> `erflow_mig` (신규).

덤프 파일이 아니라 **살아 있는 레거시 DB** 에서 복제한다. 덤프는 MySQL 8 에서 떠서
`utf8mb4_0900_ai_ci` 처럼 MariaDB 에 없는 collation 이 박혀 있는 반면, 라이브 DB 는
이미 MariaDB 에서 돌고 있어 그대로 옮길 수 있다. 그리고 라이브 DB 가 정합성 판정의
정답이므로, 정답과 같은 것을 복제하는 편이 옳다.

**레거시는 읽기만 한다.** 이 스크립트가 레거시를 대상으로 실행하는 문장은 SHOW 와
SELECT 뿐이며, 실행 전에 검사한다(`_assert_readonly`).

이름은 바꾸지 않는다. `*_tbl` 접미사가 거슬려도 1:1 이관이 먼저다 (D-012).

사용법::

    ERFLOW_DB_HOST=... ERFLOW_DB_PORT=... ERFLOW_DB_USER=... ERFLOW_DB_PASSWORD=... \\
    python migration/tools/clone_legacy_schema.py [--force]
"""

from __future__ import annotations

import os
import re
import sys

try:
    import pymysql
except ImportError:  # pragma: no cover
    raise SystemExit("pymysql 이 필요하다: pip install pymysql")

SOURCE = "erflow"
TARGET = "erflow_mig"

_DEFINER = re.compile(r"DEFINER=`[^`]*`@`[^`]*`\s*", re.I)
_SECURITY = re.compile(r"SQL SECURITY DEFINER", re.I)
_READONLY = re.compile(r"^\s*(SHOW|SELECT|DESC|DESCRIBE)\b", re.I)


def _assert_readonly(statement: str) -> None:
    """레거시를 향한 문장이 읽기 전용인지 확인한다.

    앱 계정은 SELECT 권한만 가지므로 서버가 이미 막아주지만, 이 스크립트는 관리
    계정으로도 돌 수 있다. 정답을 오염시키는 사고는 한 번이면 복구가 불가능하므로
    호출 지점에서도 막는다.
    """
    if not _READONLY.match(statement):
        raise RuntimeError(f"레거시에는 읽기만 허용된다: {statement[:80]}")


def connect():
    """환경변수로 서버에 접속한다.

    @return 자동 커밋이 켜진 커넥션
    """
    missing = [
        name
        for name in ("ERFLOW_DB_HOST", "ERFLOW_DB_USER", "ERFLOW_DB_PASSWORD")
        if not os.environ.get(name)
    ]
    if missing:
        raise SystemExit(f"환경변수 누락: {', '.join(missing)}")
    return pymysql.connect(
        host=os.environ["ERFLOW_DB_HOST"],
        port=int(os.environ.get("ERFLOW_DB_PORT", "3306")),
        user=os.environ["ERFLOW_DB_USER"],
        password=os.environ["ERFLOW_DB_PASSWORD"],
        charset="utf8mb4",
        connect_timeout=30,
        autocommit=True,
    )


def read(cursor, statement: str):
    """레거시를 상대로 읽기 문장을 실행한다."""
    _assert_readonly(statement)
    cursor.execute(statement)
    return cursor.fetchall()


def rewrite_view(ddl: str) -> str:
    """뷰 정의를 신규 스키마용으로 고친다.

    - `DEFINER=` 제거 — 특정 계정에 묶이면 그 계정이 사라질 때 뷰가 깨진다
    - `SQL SECURITY DEFINER` -> `INVOKER` — 조회하는 계정의 권한으로 동작시킨다
    - 스키마 참조를 신규로 바꾼다
    """
    ddl = _DEFINER.sub("", ddl)
    ddl = _SECURITY.sub("SQL SECURITY INVOKER", ddl)
    return ddl.replace(f"`{SOURCE}`.", f"`{TARGET}`.")


def main(argv: list[str]) -> int:
    force = "--force" in argv
    connection = connect()
    cursor = connection.cursor()

    objects = read(
        cursor,
        f"SELECT table_name, table_type FROM information_schema.tables "
        f"WHERE table_schema='{SOURCE}' ORDER BY table_name",
    )
    tables = [name for name, kind in objects if kind == "BASE TABLE"]
    views = [name for name, kind in objects if kind != "BASE TABLE"]

    existing = {r[0] for r in read(cursor, f"SHOW TABLES FROM `{TARGET}`")}
    print(f"{SOURCE} -> {TARGET}   테이블 {len(tables)} / 뷰 {len(views)}")
    if existing:
        print(f"  기존 객체: {', '.join(sorted(existing))}")

    # FK 는 0개지만 뷰가 뷰를 참조한다. 생성 순서 문제는 아래 재시도 루프로 푼다.
    cursor.execute("SET FOREIGN_KEY_CHECKS=0")

    print("\n=== 테이블 ===")
    copied, skipped = 0, 0
    for name in tables:
        if name in existing and not force:
            print(f"  SKIP {name}  (이미 있음, --force 로 재생성)")
            skipped += 1
            continue
        ddl = read(cursor, f"SHOW CREATE TABLE `{SOURCE}`.`{name}`")[0][1]
        if force:
            cursor.execute(f"DROP TABLE IF EXISTS `{TARGET}`.`{name}`")
        cursor.execute(ddl.replace(f"CREATE TABLE `{name}`", f"CREATE TABLE `{TARGET}`.`{name}`", 1))
        cursor.execute(
            f"INSERT INTO `{TARGET}`.`{name}` SELECT * FROM `{SOURCE}`.`{name}`"
        )
        rows = read(cursor, f"SELECT COUNT(*) FROM `{SOURCE}`.`{name}`")[0][0]
        print(f"  OK   {name:32} {rows:6}행")
        copied += 1

    print("\n=== 뷰 ===")
    pending = [v for v in views if force or v not in existing]
    for view in views:
        if view in existing and not force:
            print(f"  SKIP {view}")
    # 뷰가 뷰를 참조하므로 만들어질 때까지 반복한다. 한 바퀴에 하나도 못 만들면 중단.
    while pending:
        progressed = []
        for view in list(pending):
            ddl = rewrite_view(read(cursor, f"SHOW CREATE VIEW `{SOURCE}`.`{view}`")[0][1])
            try:
                cursor.execute(f"DROP VIEW IF EXISTS `{TARGET}`.`{view}`")
                cursor.execute(ddl)
            except pymysql.err.Error:
                continue
            print(f"  OK   {view}")
            pending.remove(view)
            progressed.append(view)
        if not progressed:
            print(f"  FAIL 의존성을 풀 수 없다: {pending}", file=sys.stderr)
            cursor.execute("SET FOREIGN_KEY_CHECKS=1")
            connection.close()
            return 1

    cursor.execute("SET FOREIGN_KEY_CHECKS=1")

    print("\n=== 행수 대조 ===")
    mismatch = []
    for name in tables:
        src = read(cursor, f"SELECT COUNT(*) FROM `{SOURCE}`.`{name}`")[0][0]
        cursor.execute(f"SELECT COUNT(*) FROM `{TARGET}`.`{name}`")
        dst = cursor.fetchone()[0]
        if src != dst:
            mismatch.append((name, src, dst))
    if mismatch:
        for name, src, dst in mismatch:
            print(f"  불일치 {name}: 레거시 {src} vs 신규 {dst}", file=sys.stderr)
        connection.close()
        return 1
    print(f"  전체 {len(tables)}개 테이블 행수 일치")
    print(f"\n복제 {copied} / 건너뜀 {skipped} / 뷰 {len(views)}")

    connection.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
