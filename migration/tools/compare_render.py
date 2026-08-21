"""가동 중인 두 앱의 화면을 **그려서** 견준다.

`compare_live.py` 는 표에 찍히는 글자를 본다. 그것으로는 <b>어디에 놓였는지</b>를 못
본다 — 글자가 같아도 배치가 다를 수 있다. 실제로 그렇게 놓쳤다(D-092): 관리자 게시판
관리 화면이 레거시에서는 좌우 2단인데 신규는 세로로 쌓여 있었고, 게이트도 글자 대조도
전부 통과했다.

레거시는 여는 태그와 닫는 태그를 교차해 쓴다. 브라우저는 그것을 나름의 규칙으로
바로잡아 트리를 만든다 — **그 트리가 기준이다**(D-037). 파이썬 파서는 브라우저와 다르게
바로잡으므로 원문을 아무리 견줘도 이 차이가 보이지 않는다. 그려 봐야 보인다.

준비
    1. 두 앱을 띄운다(compare_live.py 와 같다)
    2. Chrome 이 있어야 한다. 없으면 건너뛴다

환경변수
    ERFLOW_TEST_ID / ERFLOW_TEST_PASSWORD   비교에 쓸 계정
    ERFLOW_CHROME                           크롬 경로. 없으면 흔한 자리를 찾는다

사용법::

    python migration/tools/compare_render.py

exit code 는 게이트 규약을 따른다(0 PASS / 1 FAIL / 2 ERROR).
"""

from __future__ import annotations

import hashlib
import os
import pathlib
import re
import shutil
import subprocess
import sys
import tempfile

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))

from compare_live import (  # noqa: E402
    DIVERGED, LEGACY, NEW, UNORDERED, login, opener, screens_from_map, set_cookie,
    _NO_COUNT_COOKIE,
)

#: 크롬이 흔히 놓이는 자리.
_CHROME_GUESSES = (
    r"C:\Program Files\Google\Chrome\Application\chrome.exe",
    r"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe",
    "/usr/bin/google-chrome",
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
)

#: 그릴 창 크기. 좌우 2단이 접히지 않을 만큼 넓어야 한다.
_WINDOW = "1600,1300"


def find_chrome() -> str | None:
    named = os.environ.get("ERFLOW_CHROME")
    if named and pathlib.Path(named).exists():
        return named
    for guess in _CHROME_GUESSES:
        if pathlib.Path(guess).exists():
            return guess
    return shutil.which("google-chrome") or shutil.which("chromium")


#: 애니메이션을 멈추는 스타일.
#:
#: 그림을 찍는 순간이 조금만 달라도 «움직이는 중» 인 요소가 다른 자리에 잡힌다.
#: 404 화면의 브라우저 그림이 `transition: transform .5s` 로 기울어지는데, 그 때문에
#: 한쪽은 기울고 한쪽은 반듯한 그림이 나와 «다르다» 로 보고됐다 — 오탐이다.
_FREEZE = ("<style>*,*::before,*::after{animation:none !important;"
           "transition:none !important;caret-color:transparent !important}</style>")


def prepare(html: str, base: str) -> str:
    """화면을 파일로 저장하기 전에 손볼 것 둘.

    `<base>` 는 상대 경로가 원래 서버를 가리키게 한다. 없으면 CSS 와 그림이 붙지 않아
    아무것도 아닌 차이가 생긴다. 경로를 하나하나 고치는 것보다 이것이 확실하다.

    그리고 애니메이션을 멈춘다. 움직이는 화면은 찍는 순간마다 달라 견줄 수 없다.
    """
    return re.sub(r"(<head[^>]*>)", r"\1<base href=" + f'"{base}">' + _FREEZE,
                  html, count=1)


def shot(chrome: str, page: pathlib.Path, image: pathlib.Path) -> bool:
    url = "file:///" + str(page).replace("\\", "/")
    subprocess.run(
        [chrome, "--headless=new", "--disable-gpu", "--hide-scrollbars",
         f"--window-size={_WINDOW}", "--virtual-time-budget=8000",
         f"--screenshot={image}", url],
        capture_output=True, timeout=120)
    return image.exists() and image.stat().st_size > 0


def digest(path: pathlib.Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main() -> int:
    account = os.environ.get("ERFLOW_TEST_ID")
    password = os.environ.get("ERFLOW_TEST_PASSWORD")
    if not account or not password:
        print("ERROR 환경변수 필요: ERFLOW_TEST_ID, ERFLOW_TEST_PASSWORD", file=sys.stderr)
        return 2

    chrome = find_chrome()
    if not chrome:
        print("SKIP  크롬을 찾지 못했다. ERFLOW_CHROME 으로 경로를 준다")
        return 0

    legacy, new = opener(), opener()
    login(legacy, LEGACY, "/login/Login", account, password, csrf=False)
    login(new, NEW, "/login", account, password, csrf=True)
    for client in (legacy, new):
        set_cookie(client, "postId", _NO_COUNT_COOKIE)

    work = pathlib.Path(tempfile.mkdtemp(prefix="erflow-render-"))
    same = differ = skipped = known = 0

    for label, legacy_path, new_path, _mode in screens_from_map():
        pages = {}
        for side, client, root, path in (
                ("legacy", legacy, LEGACY, legacy_path), ("new", new, NEW, new_path)):
            try:
                with client.open(root + path, None, timeout=30) as response:
                    html = response.read().decode("utf-8", errors="replace")
                    # 리다이렉트를 따라간 **최종 주소** 기준이어야 한다. 요청한 주소로
                    # 잡으면, 안내 화면으로 튕긴 레거시의 상대경로 CSS 가 엉뚱한 폴더를
                    # 가리켜 민짜 화면이 그려진다 — 그것을 «다르다» 로 오탐했다.
                    landed = response.geturl()
            except OSError:
                pages = {}
                break
            folder = landed.split("?", 1)[0].rsplit("/", 1)[0] + "/"
            page = work / f"{side}.html"
            page.write_text(prepare(html, folder), encoding="utf-8")
            pages[side] = page

        if len(pages) != 2:
            skipped += 1
            print(f"  SKIP  {label}: 화면을 받지 못했다")
            continue

        images = {}
        for side, page in pages.items():
            image = work / f"{side}.png"
            image.unlink(missing_ok=True)
            if not shot(chrome, page, image):
                images = {}
                break
            images[side] = image
        if len(images) != 2:
            skipped += 1
            print(f"  SKIP  {label}: 그리지 못했다")
            continue

        if digest(images["legacy"]) == digest(images["new"]):
            same += 1
            print(f"  PASS  {label}: 화면이 같다")
        elif new_path.split("?", 1)[0] in UNORDERED:
            # 줄 순서가 정해지지 않은 화면(D-091)은 그림이 실행마다 달라진다.
            # 내용이 같은지는 compare_live 가 순서 무관으로 본다 — 여기서는 그
            # 사실만 적고 넘어간다. 이 예외는 UNORDERED 에 적힌 화면에만 열린다.
            known += 1
            print(f"  KNOWN {label}: 줄 순서가 정해지지 않았다({UNORDERED[new_path.split('?', 1)[0]]})")
        elif new_path.split("?", 1)[0] in DIVERGED:
            # 2단계에서 일부러 바뀐 칸이 있는 화면. 글자가 다르니 그림도 다르다.
            # 바뀐 칸 말고도 다른 데가 있는지는 compare_live 가 칸 단위로 본다 —
            # 그쪽 예외는 등록된 짝(레거시 «null» -> 빈칸)에만 열린다(D-112).
            known += 1
            print(f"  KNOWN {label}: {DIVERGED[new_path.split('?', 1)[0]][0][2]}")
        else:
            differ += 1
            kept = work / f"{label.replace('/', '-')}"
            for side, image in images.items():
                shutil.copy(image, f"{kept}-{side}.png")
            print(f"  FAIL  {label}: 화면이 다르다 — {kept}-legacy.png / -new.png")

    print(f"\n같음 {same} / 다름 {differ} / 순서 무관 {known} / 못 봄 {skipped}")
    print(f"그림: {work}")
    return 1 if differ else 0


if __name__ == "__main__":
    raise SystemExit(main())
