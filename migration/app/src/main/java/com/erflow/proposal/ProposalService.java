package com.erflow.proposal;

import com.erflow.common.Pagination;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결재 업무.
 *
 * <p>레거시는 페이징 계산을 {@code proposalList.jsp} 스크립틀릿에, DB 접근을
 * {@code ProposalServiceImpl} 에 두었다. 화면에서 로직을 걷어내 여기로 모은다.
 */
@Service
public class ProposalService {

    /** 결재진행중. 레거시 {@code result} 3. */
    public static final int IN_PROGRESS = 3;

    /** 승인. 레거시 {@code result} 1. */
    public static final int APPROVED = 1;

    /** 반려. 레거시 {@code result} 2. */
    public static final int REJECTED = 2;

    private final ProposalMapper proposalMapper;

    /**
     * @param proposalMapper 결재 매퍼
     */
    public ProposalService(ProposalMapper proposalMapper) {
        this.proposalMapper = proposalMapper;
    }

    /**
     * 결재 리스트 한 페이지.
     *
     * @param userId 현재 사용자 사번
     * @param result 상태 필터(3/1/2)
     * @param requestedPage 요청된 페이지
     * @return 목록과 페이징
     */
    @Transactional(readOnly = true)
    public ProposalPage list(String userId, int result, int requestedPage) {
        int total = proposalMapper.countBy(userId, result);
        Pagination pagination = Pagination.of(total, requestedPage);
        List<ProposalRow> rows = proposalMapper.findPage(
                userId, result, pagination.start(), pagination.numPerPage());
        return new ProposalPage(rows, pagination);
    }

    /**
     * 결재 리스트 한 페이지.
     *
     * @param rows 이 페이지의 결재 목록
     * @param pagination 페이징 정보
     */
    public record ProposalPage(List<ProposalRow> rows, Pagination pagination) {
    }
}
