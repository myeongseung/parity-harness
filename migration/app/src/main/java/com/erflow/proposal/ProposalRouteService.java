package com.erflow.proposal;

import com.erflow.common.Pagination;
import com.erflow.user.UserMapper;
import com.erflow.user.UserRow;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결재라인 관리 업무.
 *
 * <p>레거시는 페이징 계산을 {@code proposalRouteList.jsp} 스크립틀릿에, DB 접근을
 * {@code ProposalRouteServiceImpl} 에 두었다. 결재경로 문자열을 사람마다 푸는 가공도
 * 화면에서 했다 — 여기로 모은다.
 */
@Service
public class ProposalRouteService {

    private final ProposalRouteMapper proposalRouteMapper;
    private final UserMapper userMapper;

    /**
     * @param proposalRouteMapper 결재라인 매퍼
     * @param userMapper 사용자 조회(결재경로 사람 풀이용)
     */
    public ProposalRouteService(ProposalRouteMapper proposalRouteMapper, UserMapper userMapper) {
        this.proposalRouteMapper = proposalRouteMapper;
        this.userMapper = userMapper;
    }

    /**
     * 내 결재라인 한 페이지. 결재경로를 사람마다 풀어 돌려준다.
     *
     * @param userId 사번
     * @param search 검색 조건
     * @param requestedPage 요청된 페이지
     * @return 목록과 페이징
     */
    @Transactional(readOnly = true)
    public RoutePage list(String userId, ProposalRouteSearch search, int requestedPage) {
        int total = proposalRouteMapper.countBy(userId, search);
        Pagination pagination = Pagination.of(total, requestedPage);
        List<ProposalRouteListRow> rows = proposalRouteMapper.findPage(
                userId, search, pagination.start(), pagination.numPerPage());

        List<ProposalRouteListItem> items = new ArrayList<>();
        for (ProposalRouteListRow row : rows) {
            items.add(new ProposalRouteListItem(
                    row.id(), row.nickname(), routeChain(row.route()), row.createdAt()));
        }
        return new RoutePage(items, pagination);
    }

    /**
     * 사번을 {@code ;} 로 이은 결재경로를 «[부서/직급] 이름(사번) -&gt; ...» 로 푼다.
     *
     * <p>레거시가 화면에서 사번마다 {@code getUserView} 를 불러 만들던 문자열이다.
     * 사번이 사라진 사용자를 가리키면 레거시는 {@code null} 에서 예외로 죽었다 — 여기서는
     * 그 사번만 «(사번)» 으로 두어 화면이 뜨게 한다.
     *
     * @param route 사번을 {@code ;} 로 이은 문자열
     * @return 사람마다 푼 결재경로
     */
    private String routeChain(String route) {
        if (route == null || route.isBlank()) {
            return "";
        }
        return java.util.Arrays.stream(route.split(";"))
                .map(sabun -> {
                    UserRow user = userMapper.findUserView(sabun);
                    return user == null
                            ? "(" + sabun + ")"
                            : "[" + user.deptName() + "/" + user.jobName() + "] "
                                    + user.name() + "(" + user.id() + ")";
                })
                .collect(Collectors.joining(" -> "));
    }

    /**
     * 결재라인 목록 한 페이지.
     *
     * @param items 이 페이지의 결재라인(가공 후)
     * @param pagination 페이징 정보
     */
    public record RoutePage(List<ProposalRouteListItem> items, Pagination pagination) {
    }
}
