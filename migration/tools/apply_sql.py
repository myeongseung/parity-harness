"""SQL 파일을 대상 DB에 적용한다.

`mysql` 클라이언트가 없는 환경을 위한 최소 도구다. 문장 분리를 직접 하므로
주의할 점이 하나 있다 — **주석은 문장에서 떼어내되 문장을 버리면 안 된다.**
파일 첫머리의 헤더 주석이 붙어 있다는 이유로 첫 CREATE TABLE 을 통째로 건너뛰면,
뒤따르는 FK 가 존재하지 않는 테이블을 참조해 errno 150 으로 죽는다.

접속 정보는 환경변수로만 받는다. 파일에 남기지 않는다.

    ERFLOW_DB_HOST ERFLOW_DB_PORT ERFLOW_DB_NAME ERFLOW_DB_USER ERFLOW_DB_PASSWORD

사용법::

    python migration/tools/apply_sql.py migration/seed/V2__layout_and_permission.sql
"""

from __future__ import annotations

import os
import re
import sys
from pathlib import Path

try:
    import pymysql
except ImportError:  # pragma: no cover
    raise SystemExit("pymysql 이 필요하다: pip install pymysql")

_LINE_COMMENT = re.compile(r"^\s*--.*$", re.M)


def split_statements(sql: str) -> list[str]:
    """SQL 텍스트를 실행 가능한 문장 목록으로 나눈다.

    줄 주석을 먼저 제거한 뒤 세미콜론으로 나눈다. 순서를 뒤집으면
    주석이 붙은 문장이 통째로 사라진다.
    """
    stripped = _LINE_COMMENT.sub("", sql)
    return [part.strip() for part in stripped.split(";") if part.strip()]


def connect():
    """환경변수로 대상 DB에 접속한다.

    @return 자동 커밋이 켜진 커넥션
    """
    missing = [
        name
        for name in ("ERFLOW_DB_HOST", "ERFLOW_DB_NAME", "ERFLOW_DB_USER", "ERFLOW_DB_PASSWORD")
        if not os.environ.get(name)
    ]
    if missing:
        raise SystemExit(f"환경변수 누락: {', '.join(missing)}")
    return pymysql.connect(
        host=os.environ["ERFLOW_DB_HOST"],
        port=int(os.environ.get("ERFLOW_DB_PORT", "3306")),
        user=os.environ["ERFLOW_DB_USER"],
        password=os.environ["ERFLOW_DB_PASSWORD"],
        database=os.environ["ERFLOW_DB_NAME"],
        charset="utf8mb4",
        connect_timeout=20,
        autocommit=True,
    )


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        print(__doc__)
        return 2

    path = Path(argv[1])
    if not path.is_file():
        print(f"ERROR 파일 없음: {path}", file=sys.stderr)
        return 2

    statements = split_statements(path.read_text(encoding="utf-8"))
    connection = connect()
    cursor = connection.cursor()

    print(f"{path.name} -> {os.environ['ERFLOW_DB_NAME']}  ({len(statements)}개 문장)")
    for index, statement in enumerate(statements, start=1):
        head = " ".join(statement.split()[:5])
        try:
            cursor.execute(statement)
        except Exception as exc:
            print(f"  {index:3}. FAIL {head}\n       {exc}", file=sys.stderr)
            connection.close()
            return 1
        print(f"  {index:3}. OK   {head}")

    connection.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
