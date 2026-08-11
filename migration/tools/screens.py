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

#: (도메인, 레거시 JSP 이름, 신규 템플릿 이름)
#:
#: `*Proc.jsp` 는 넣지 않는다. 화면이 아니라 액션이다.
SCREENS = (
    ("unit", "unitList", "list"),
    ("unit", "unitRegister", "register"),
    ("unit", "unitUpdate", "update"),
    ("company", "companyList", "list"),
    ("company", "companyRegister", "register"),
    ("company", "companyUpdate", "update"),
)

#: (레이아웃 정답 이름, 배치 위치)
LAYOUTS = (("menu", "SIDE"), ("header", "HEADER"))


def legacy_path(domain: str, jsp: str) -> Path:
    return LEGACY / domain / f"{jsp}.jsp"


def golden_path(domain: str, jsp: str) -> Path:
    return GOLDEN / domain / f"{jsp}.json"


def template_path(domain: str, template: str) -> Path:
    return TEMPLATES / domain / f"{template}.html"
