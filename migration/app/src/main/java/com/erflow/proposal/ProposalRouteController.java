package com.erflow.proposal;

import com.erflow.auth.ErflowUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 결재라인 관리 화면.
 *
 * <pre>
 * proposalRouteList.jsp  GET /proposal/route-list
 * </pre>
 *
 * <p>내가 만든 결재라인만 보인다 — 조회가 사번으로 좁혀져 있다. 등록·수정·삭제는 다음
 * 단계에서 붙인다.
 */
@Controller
@RequestMapping("/proposal")
public class ProposalRouteController {

    private final ProposalRouteService proposalRouteService;

    /**
     * @param proposalRouteService 결재라인 업무
     */
    public ProposalRouteController(ProposalRouteService proposalRouteService) {
        this.proposalRouteService = proposalRouteService;
    }

    /**
     * 결재라인 목록.
     *
     * @param keyfield 검색 대상
     * @param keyword 검색어
     * @param nowPage 현재 페이지
     * @param user 로그인 사용자
     * @param model 뷰 모델
     * @return 결재라인 목록 템플릿
     */
    @GetMapping("/route-list")
    public String routeList(
            @RequestParam(required = false) String keyfield,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int nowPage,
            @AuthenticationPrincipal ErflowUserDetails user,
            Model model) {

        ProposalRouteSearch search = new ProposalRouteSearch(
                keyfield == null ? "" : keyfield, keyword == null ? "" : keyword);
        ProposalRouteService.RoutePage page =
                proposalRouteService.list(user.id(), search, nowPage);

        model.addAttribute("routes", page.items());
        model.addAttribute("page", page.pagination());
        model.addAttribute("keyfield", keyfield == null ? "" : keyfield);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "proposal/route-list";
    }
}
