package com.erflow.proposal;

import com.erflow.auth.ErflowUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 결재 화면.
 *
 * <pre>
 * proposalList.jsp  GET /proposal/list
 * </pre>
 *
 * <p>결재라인 관리({@link ProposalRouteController})와 같은 {@code /proposal} 아래지만
 * 프로그램(권한)이 다르다. 등록·문서 상세·승인/반려는 다음 단계에서 붙인다.
 */
@Controller
public class ProposalController {

    private final ProposalService proposalService;

    /**
     * @param proposalService 결재 업무
     */
    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    /**
     * 결재 리스트.
     *
     * @param keyfield 상태 필터(3 결재진행중 / 1 승인 / 2 반려). 없으면 3
     * @param nowPage 현재 페이지
     * @param user 로그인 사용자
     * @param model 뷰 모델
     * @return 결재 리스트 템플릿
     */
    @GetMapping("/proposal/list")
    public String list(
            @RequestParam(defaultValue = "3") int keyfield,
            @RequestParam(defaultValue = "1") int nowPage,
            @AuthenticationPrincipal ErflowUserDetails user,
            Model model) {

        ProposalService.ProposalPage page = proposalService.list(user.id(), keyfield, nowPage);

        model.addAttribute("proposals", page.rows());
        model.addAttribute("page", page.pagination());
        model.addAttribute("keyfield", keyfield);
        return "proposal/list";
    }
}
