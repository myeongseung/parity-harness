package com.erflow.task;

/**
 * 목록 화면에 뿌리는 수·발주 한 건.
 *
 * <p>레거시 {@code task_view} 한 행 중 목록이 쓰는 칸만 담는다. 뷰가
 * {@code task_tbl} 에 직원·부서·회사·문서를 조인해 두었다.
 *
 * @param id 의뢰 번호({@code task_view.task_id})
 * @param userName 담당 직원명
 * @param deptName 부서명
 * @param companyName 회사명
 * @param subject 문서 제목. 없으면 {@code null}
 * @param taskAt 의뢰 시각. 레거시가 문자열로 저장하고 그대로 찍는다
 * @param status 상태 코드. 라벨은 {@link TaskStatus} 가 푼다
 */
public record TaskRow(
        int id,
        String userName,
        String deptName,
        String companyName,
        String subject,
        String taskAt,
        int status) {

    /**
     * 화면에 찍히는 상태 글자.
     *
     * @return 레거시 {@code TaskRepository.getTaskStatusCode(status)} 와 같은 값
     */
    public String statusLabel() {
        return TaskStatus.label(status);
    }
}
