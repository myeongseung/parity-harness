package com.erflow.task;

import com.erflow.common.Pagination;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수·발주 업무.
 *
 * <p>레거시는 페이징 계산과 파라미터 정리를 {@code purchaseTask.jsp}/{@code sellTask.jsp}
 * 스크립틀릿에, DB 접근을 {@code TaskServiceImpl} 에 두었다. 화면에서 로직을 걷어내
 * 여기로 모은다.
 */
@Service
public class TaskService {

    /** 수주. 레거시 {@code type} 0. */
    public static final int SELL = 0;

    /** 발주. 레거시 {@code type} 1. */
    public static final int PURCHASE = 1;

    private final TaskMapper taskMapper;

    /**
     * @param taskMapper 수·발주 매퍼
     */
    public TaskService(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    /**
     * 목록 한 페이지와 페이징 정보를 함께 돌려준다.
     *
     * @param type 0 수주 / 1 발주
     * @param search 검색 조건
     * @param requestedPage 요청된 페이지
     * @return 목록과 페이징
     */
    @Transactional(readOnly = true)
    public TaskPage list(int type, TaskSearch search, int requestedPage) {
        // 레거시는 status 검색어가 정수가 아니면 예외를 삼켜 빈 목록을 냈다. 매퍼까지
        // 가기 전에 걸러 같은 결과(빈 목록)를 낸다 — TaskSearch.isBrokenStatus 참조.
        if (search != null && search.isBrokenStatus()) {
            return new TaskPage(List.of(), Pagination.of(0, requestedPage));
        }
        int total = taskMapper.countBy(type, search);
        Pagination pagination = Pagination.of(total, requestedPage);
        List<TaskRow> rows =
                taskMapper.findPage(type, search, pagination.start(), pagination.numPerPage());
        return new TaskPage(rows, pagination);
    }

    /**
     * 목록 한 페이지.
     *
     * @param rows 이 페이지의 수·발주 목록
     * @param pagination 페이징 정보
     */
    public record TaskPage(List<TaskRow> rows, Pagination pagination) {
    }
}
