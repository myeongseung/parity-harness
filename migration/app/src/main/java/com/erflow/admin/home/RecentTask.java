package com.erflow.admin.home;

import com.erflow.task.TaskStatus;

/**
 * 대시보드 «수/발주 내역» 칸 한 줄. {@code task_view} 한 행.
 *
 * <p>목록 화면과 달리 <b>수주와 발주를 함께</b> 보여준다 — 레거시가 type 조건을 빼고
 * 조회한다.
 *
 * @param userName 등록인
 * @param type 0 수주 / 1 발주
 * @param createdAt 요청일
 * @param status 상태 코드
 */
public record RecentTask(String userName, int type, String createdAt, int status) {

    /**
     * @return 수주 또는 발주
     */
    public String typeLabel() {
        return type == 0 ? "수주" : "발주";
    }

    /**
     * @return 상태 라벨. 수·발주 목록과 같은 표를 쓴다
     */
    public String statusLabel() {
        return TaskStatus.label(status);
    }
}
