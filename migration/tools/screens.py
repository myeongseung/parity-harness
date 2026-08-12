"""이관 대상 화면 목록. 정답 재생성과 게이트가 같은 것을 본다.

목록이 두 군데 있으면 어긋난다. 어긋나면 재생성에서 빠진 화면이 낡은 정답으로
계속 판정되는데, 그것이 이 프로젝트가 가장 경계하는 실패다.
"""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
HARNESS = ROOT / "parity-harness"
LEGACY = ROOT / "legacy" / "ERFlow" / "src" / "main" / "webapp"
GOLDEN = ROOT / "migration" / "golden"
TEMPLATES = ROOT / "migration" / "app" / "src" / "main" / "resources" / "templates"
ALLOWLIST = ROOT / "migration" / "allowlist.json"
SEED = ROOT / "migration" / "seed" / "menu-seed.json"


@dataclass(frozen=True)
class Screen:
    """화면 하나. 레거시 원본과 신규 구현, 그리고 정답이 놓일 자리.

    경로를 규칙으로 짐작하지 않고 적어 둔다. 레거시는 폴더 구조가 고르지 않아서
    (오류 화면은 최상위에 있고, 일부는 `.jsp` 가 아니라 `.html` 이다) 규칙으로
    맞추려 들면 예외가 규칙보다 많아진다.

    :param name: 정답 파일 이름. 도메인 안에서 겹치지 않으면 된다
    :param domain: 정답과 템플릿이 놓이는 묶음 이름
    :param legacy: `webapp/` 기준 레거시 원본 경로
    :param template: `templates/` 기준 신규 템플릿 경로
    :param done: 대조까지 끝났는지. False 면 정답만 뽑고 대조는 하지 않는다
    """

    name: str
    domain: str
    legacy: str
    template: str
    done: bool

    @property
    def legacy_file(self) -> Path:
        return LEGACY / self.legacy

    @property
    def golden_file(self) -> Path:
        return GOLDEN / self.domain / f"{self.name}.json"

    @property
    def template_file(self) -> Path:
        return TEMPLATES / self.template

    @property
    def label(self) -> str:
        return self.template.removesuffix(".html")


def _screen(domain: str, name: str, template: str, done: bool,
            legacy: str | None = None) -> Screen:
    """흔한 모양을 짧게 적기 위한 도우미.

    기본은 `webapp/<도메인>/<이름>.jsp` -> `templates/<도메인>/<템플릿>.html` 이다.
    벗어나는 화면만 `legacy` 를 적어 준다.
    """
    return Screen(
        name=name,
        domain=domain,
        legacy=legacy or f"{domain}/{name}.jsp",
        template=f"{domain}/{template}.html",
        done=done,
    )


#: 이관 대상 화면.
#:
#: `*Proc.jsp` 는 넣지 않는다. 화면이 아니라 액션이다. `indexHeader`·`footer` 같은
#: 조각도 넣지 않는다. 화면이 아니라 레이아웃이며 `layout/` 정답이 따로 본다.
#:
#: `done=False` 는 아직 이관하지 않은 화면이다. 정답은 뽑아서 커밋하되 대조는
#: 하지 않는다 — 신규 구현이 없으니 판정할 것이 없다.
#:
#: **목록에서 빼지 않고 False 로 두는 이유**가 있다. 빼면 그 화면이 존재한다는
#: 사실 자체가 사라져서, 이관하다 잊어도 아무도 모른다. False 는 "남았다"를
#: 리포트에 계속 띄운다. 조용히 건너뛰는 것과 드러내 놓고 미루는 것은 다르다.
SCREENS = (
    # 로그인 — 레거시 결과 화면 둘은 확장자가 .html 이다
    _screen("login", "login", "login", True),
    _screen("login", "changePassword", "change-password", True),
    _screen("login", "passwordError", "password-error", True,
            legacy="login/passwordError.html"),
    _screen("login", "passwordOk", "password-ok", True,
            legacy="login/passwordOk.html"),
    _screen("login", "findPassword", "find-password", True),
    # 메일 발송 성공 화면. 발송 자체가 이관 범위 밖이라 화면도 아직이다(O-011).
    # 목록에는 넣는다 — 빼면 이 화면이 있다는 사실 자체가 사라진다
    _screen("login", "sendOk", "send-ok", False, legacy="login/sendOk.html"),

    # 오류 화면 — 레거시는 최상위에 있고 신규는 error/ 아래로 묶었다
    _screen("error", "accessError", "access-error", True,
            legacy="accessError.jsp"),
    _screen("error", "permissionError", "permission-error", True,
            legacy="permissionError.jsp"),
    _screen("error", "notFoundError", "not-found-error", True,
            legacy="notFoundError.jsp"),
    _screen("error", "internalServerError", "internal-server-error", True,
            legacy="internalServerError.jsp"),

    # 찾기 팝업 — 레거시는 최상위에 있다. 등록 화면이 window.open 으로 연다
    _screen("find", "findBank", "bank", True, legacy="findBank.jsp"),
    _screen("find", "findWork", "work", True, legacy="findWork.jsp"),
    _screen("find", "findDocument", "document", True, legacy="findDocument.jsp"),
    _screen("find", "findUser", "user", True, legacy="findUser.jsp"),
    _screen("find", "findCompany", "company", True, legacy="findCompany.jsp"),
    _screen("find", "findProduct", "product", True, legacy="findProduct.jsp"),
    _screen("find", "findMultiProduct", "multi-product", True,
            legacy="findMultiProduct.jsp"),
    _screen("find", "findEachUser", "each-user", True, legacy="findEachUser.jsp"),
    _screen("find", "findProposalRoute", "proposal-route", True,
            legacy="findProposalRoute.jsp"),

    _screen("unit", "unitList", "list", True),
    _screen("unit", "unitRegister", "register", True),
    _screen("unit", "unitUpdate", "update", True),

    _screen("company", "companyList", "list", True),
    _screen("company", "companyRegister", "register", True),
    _screen("company", "companyUpdate", "update", True),

    # 업무(수·발주) — 목록 둘은 파일이 나뉘어 있다(경로도 /task/*-task)
    _screen("task", "purchaseTask", "purchase-task", True, legacy="task/purchaseTask.jsp"),
    _screen("task", "sellTask", "sell-task", True, legacy="task/sellTask.jsp"),
    _screen("task", "taskRegister", "register", True, legacy="task/taskRegister.jsp"),
    _screen("task", "createModal", "history-modal", True, legacy="task/createModal.jsp"),

    _screen("post", "boardList", "board-list", True),
    _screen("post", "postList", "list", True),
    _screen("post", "postView", "view", True),
    _screen("post", "postRegister", "register", True),
    _screen("post", "postReply", "reply", True),
    _screen("post", "postUpdate", "update", True),
)

#: 대조까지 끝난 화면만.
MIGRATED = tuple(s for s in SCREENS if s.done)

#: 정답은 뽑았지만 아직 신규 구현이 없는 화면.
PENDING = tuple(s for s in SCREENS if not s.done)

#: (레이아웃 정답 이름, 배치 위치)
LAYOUTS = (("menu", "SIDE"), ("header", "HEADER"))
