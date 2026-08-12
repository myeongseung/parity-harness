package com.erflow.common;

import com.erflow.auth.ErflowUserDetails;
import com.erflow.company.CompanyCodes;
import com.erflow.document.DocumentFinder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 코드 찾기 팝업.
 *
 * <pre>
 * findBank.jsp      GET /find/bank
 * findWork.jsp      GET /find/work
 * findDocument.jsp  GET /find/document
 * </pre>
 *
 * <p>등록 화면에서 {@code window.open} 으로 여는 작은 창이다. 고른 값을
 * {@code window.opener.receiveBankInfo(코드, 이름)} 로 부모에게 돌려주고 닫힌다.
 * 그 흐름은 레거시 그대로다 — 요즘 방식으로 바꾸면 부모 화면까지 손대야 한다.
 *
 * <h2>검색 전과 검색어가 빈 것은 다르다</h2>
 *
 * <p>레거시는 {@code request.getParameter("search")} 가 {@code null} 이면 도움말을
 * 보여주고, 빈 문자열이면 <b>전체 목록</b>을 보여준다. 창을 처음 열었을 때와
 * 검색창을 비운 채 누른 것을 구별하는 것이다. 그대로 옮긴다.
 */
@Controller
@RequestMapping("/find")
public class FindController {

    private final CompanyCodes codes;
    private final DocumentFinder documents;

    /**
     * @param codes 은행·업종 코드표
     * @param documents 문서 조회
     */
    public FindController(CompanyCodes codes, DocumentFinder documents) {
        this.codes = codes;
        this.documents = documents;
    }

    /**
     * 은행 찾기.
     *
     * @param search 검색어. 파라미터 자체가 없으면 {@code null}
     * @param model 뷰 모델
     * @return 팝업 템플릿
     */
    @GetMapping("/bank")
    public String bank(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("search", search);
        model.addAttribute("entries", search == null ? null : codes.bankTable().search(search));
        return "find/bank";
    }

    /**
     * 업종 찾기.
     *
     * @param search 검색어. 파라미터 자체가 없으면 {@code null}
     * @param model 뷰 모델
     * @return 팝업 템플릿
     */
    @GetMapping("/work")
    public String work(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("search", search);
        model.addAttribute("entries", search == null ? null : codes.fieldTable().search(search));
        return "find/work";
    }

    /**
     * 문서 찾기.
     *
     * <p>은행·업종과 달리 <b>도움말이 없다.</b> 레거시가 검색어를 빈 문자열로
     * 채워 놓고 {@code null} 인지 검사해서, 도움말 갈래가 한 번도 실행되지 않는다.
     * 그대로 옮긴다 — D-036 참조.
     *
     * @param search 검색어
     * @param user 로그인 사용자
     * @param model 뷰 모델
     * @return 팝업 템플릿
     */
    @GetMapping("/document")
    public String document(
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal ErflowUserDetails user,
            Model model) {
        // 레거시가 Optional.orElse("") 로 채운다. 그래서 도움말 조건이 절대 참이
        // 되지 않는다. 여기서 null 을 그대로 넘기면 레거시에 없던 도움말이 뜬다.
        model.addAttribute("search", search == null ? "" : search);
        model.addAttribute("entries", documents.search(user.id(), search));
        return "find/document";
    }
}
