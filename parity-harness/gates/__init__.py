"""레거시 정합성 게이트 모음.

각 게이트는 CLI로 실행되며 exit code 규약을 따른다.

    0  PASS      정합성 확인됨
    1  FAIL      위반 발견 (자동 보강 가능) -> self-loop 진입
    2  ERROR     도구/설정 오류 (게이트 자체가 실행 불가)
    3  ESCALATE  정답(golden)이 없어 판정 불가 -> 사람 판단 필요

3번을 1번과 분리한 것이 중요하다. 정답이 없는데 통과시키지 않는다.
"""

EXIT_PASS = 0
EXIT_FAIL = 1
EXIT_ERROR = 2
EXIT_ESCALATE = 3
