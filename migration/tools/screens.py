"""이관 대상 화면 목록. 정답 재생성과 게이트가 같은 것을 본다.

목록이 두 군데 있으면 어긋난다. 어긋나면 재생성에서 빠진 화면이 낡은 정답으로
계속 판정되는데, 그것이 이 프로젝트가 가장 경계하는 실패다.
"""

from __future__ import annotations

from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
HARNESS = ROOT / "parity-harness"
LEGACY = ROOT / "legacy" / "ERFlow" / "src" / "main" / "webapp"
GOLDEN = ROOT / "migration" / "golden"
TEMPLATES = ROOT / "migration" / "app" / "src" / "main" / "resources" / "templates"
ALLOWLIST = ROOT / "migration" / "allowlist.json"
SEED = ROOT / "migration" / "seed" / "menu-seed.json"

#: (도메인, 레거시 JSP 이름, 신규 템플릿 이름, 이관 완료 여부)
#:
#: `*Proc.jsp` 는 넣지 않는다. 화면이 아니라 액션이다.
#:
#: 마지막 칸이 False 면 아직 이관하지 않은 화면이다. 정답은 뽑아서 커밋하되
#: 대조는 하지 않는다 — 신규 구현이 없으니 판정할 것이 없다.
#:
#: **목록에서 빼지 않고 False 로 두는 이유**가 있다. 빼면 그 화면이 존재한다는
#: 사실 자체가 사라져서, 이관하다 잊어도 아무도 모른다. False 는 "남았다"를
#: 리포트에 계속 띄운다. 조용히 건너뛰는 것과 드러내 놓고 미루는 것은 다르다.
SCREENS = (
    ("unit", "unitList", "list", True),
    ("unit", "unitRegister", "register", True),
    ("unit", "unitUpdate", "update", True),
    ("company", "companyList", "list", True),
    ("company", "companyRegister", "register", True),
    ("company", "companyUpdate", "update", True),
    ("post", "boardList", "board-list", True),
    ("post", "postList", "list", True),
    ("post", "postView", "view", True),
    ("post", "postRegister", "register", True),
    ("post", "postReply", "reply", True),
    ("post", "postUpdate", "update", True),
)

#: 대조까지 끝난 화면만.
MIGRATED = tuple(s for s in SCREENS if s[3])

#: 정답은 뽑았지만 아직 신규 구현이 없는 화면.
PENDING = tuple(s for s in SCREENS if not s[3])

#: (레이아웃 정답 이름, 배치 위치)
LAYOUTS = (("menu", "SIDE"), ("header", "HEADER"))


def legacy_path(domain: str, jsp: str) -> Path:
    return LEGACY / domain / f"{jsp}.jsp"


def golden_path(domain: str, jsp: str) -> Path:
    return GOLDEN / domain / f"{jsp}.json"


def template_path(domain: str, template: str) -> Path:
    return TEMPLATES / domain / f"{template}.html"
