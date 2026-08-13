package com.erflow.bound;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 입·출고 관리 화면.
 *
 * <pre>
 * inbound.jsp   GET /bound/inbound    (입고, type=0)
 * outbound.jsp  GET /bound/outbound   (출고, type=1)
 * </pre>
 *
 * <p>입고와 출고는 거의 같은 화면인데 {@code type} 과 권한이 갈린다. 레거시가 파일과
 * 경로를 둘로 나눠 두었고({@code screen} 테이블도 {@code /bound/inbound}·
 * {@code /bound/outbound}) 화면 글자(입고/출고, 입고자/출고자)까지 달라 템플릿도 둘로 둔다.
 *
 * <p>등록·수정·삭제는 다음 단계에서 붙인다. 레거시는 그 액션들을 {@code -proc} 경로로
 * 따로 두었고({@code /bound/register-proc} 등) 권한도 flag 로 갈린다.
 */
@Controller
@RequestMapping("/bound")
public class BoundController {

    private final BoundService boundService;

    /**
     * @param boundService 입·출고 업무
     */
    public BoundController(BoundService boundService) {
        this.boundService = boundService;
    }

    /**
     * 입고 목록.
     *
     * @param keyfield 검색 대상
     * @param keyword 검색어
     * @param nowPage 현재 페이지
     * @param model 뷰 모델
     * @return 입고 목록 템플릿
     */
    @GetMapping("/inbound")
    public String inbound(
            @RequestParam(required = false) String keyfield,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int nowPage,
            Model model) {
        return listView(BoundService.INBOUND, "bound/inbound", keyfield, keyword, nowPage, model);
    }

    /**
     * 출고 목록.
     *
     * @param keyfield 검색 대상
     * @param keyword 검색어
     * @param nowPage 현재 페이지
     * @param model 뷰 모델
     * @return 출고 목록 템플릿
     */
    @GetMapping("/outbound")
    public String outbound(
            @RequestParam(required = false) String keyfield,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int nowPage,
            Model model) {
        return listView(BoundService.OUTBOUND, "bound/outbound", keyfield, keyword, nowPage, model);
    }

    private String listView(
            int type, String view, String keyfield, String keyword, int nowPage, Model model) {
        BoundSearch search = new BoundSearch(
                keyfield == null ? "" : keyfield, keyword == null ? "" : keyword);
        BoundService.BoundPage page = boundService.list(type, search, nowPage);

        model.addAttribute("bounds", page.rows());
        model.addAttribute("page", page.pagination());
        model.addAttribute("keyfield", keyfield == null ? "" : keyfield);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return view;
    }
}
