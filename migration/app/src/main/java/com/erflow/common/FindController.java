package com.erflow.common;

import com.erflow.company.CompanyCodes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 코드 찾기 팝업.
 *
 * <pre>
 * findBank.jsp  GET /find/bank
 * findWork.jsp  GET /find/work
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

    /**
     * @param codes 은행·업종 코드표
     */
    public FindController(CompanyCodes codes) {
        this.codes = codes;
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
}
