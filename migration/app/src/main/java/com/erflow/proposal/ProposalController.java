package com.erflow.proposal;

import com.erflow.auth.ErflowUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 결재 화면.
 *
 * <pre>
 * proposalList.jsp      GET  /proposal/list
 * proposalRegister.jsp  GET  /proposal/register
 * proposalRegisterProc  POST /proposal/register-proc
 * </pre>
 *
 * <p>결재라인 관리({@link ProposalRouteController})와 같은 {@code /proposal} 아래지만
 * 프로그램(권한)이 다르다. 문서 상세·승인/반려는 다음 단계에서 붙인다.
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

    /**
     * 결재 등록 화면.
     *
     * <p>문서 찾기·결재라인 찾기 팝업이 값을 채운다. 처음엔 비어 있다.
     *
     * @param user 로그인 사용자(기안자)
     * @param model 뷰 모델
     * @return 등록 템플릿
     */
    @GetMapping("/proposal/register")
    public String registerForm(@AuthenticationPrincipal ErflowUserDetails user, Model model) {
        model.addAttribute("userId", user.id());
        model.addAttribute("documentId", "");
        model.addAttribute("documentName", "");
        return "proposal/register";
    }

    /**
     * 결재 등록 처리.
     *
     * <p>레거시 {@code proposalRegisterProc.jsp} 를 옮겼다. 네 값(기안자·문서·결재라인·
     * 결재경로)이 모두 있어야 등록한다. 저장에는 기안자·문서·결재라인만 쓴다 — 결재경로
     * 문자열은 화면 표시용이라 넣지 않는다. 성공하면 그 결재의 문서 상세로 간다.
     *
     * @param documentId 문서번호
     * @param routeId 결재라인 번호
     * @param route 결재경로(표시용). 있어야 하지만 저장하지 않는다
     * @param user 로그인 사용자(기안자)
     * @param model 뷰 모델
     * @return 결과 템플릿
     */
    @PostMapping("/proposal/register-proc")
    public String register(
            @RequestParam(required = false) Long documentId,
            @RequestParam(required = false) Integer routeId,
            @RequestParam(required = false) String route,
            @AuthenticationPrincipal ErflowUserDetails user,
            Model model) {

        long created = documentId != null && routeId != null && route != null
                ? proposalService.create(user.id(), documentId, routeId)
                : 0;

        model.addAttribute("message", created > 0 ? "등록에 성공했습니다." : "등록에 실패했습니다.");
        model.addAttribute("nextPage", created > 0
                ? "/proposal/document?proposalId=" + created
                : "/proposal/register");
        return "proposal/route-result";
    }
}
