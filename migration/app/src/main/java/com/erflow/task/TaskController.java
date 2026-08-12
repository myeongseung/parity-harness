package com.erflow.task;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 수·발주 관리 화면.
 *
 * <pre>
 * purchaseTask.jsp  GET /task/purchase-task   (발주, type=1)
 * sellTask.jsp      GET /task/sell-task        (수주, type=0)
 * </pre>
 *
 * <p>발주와 수주는 거의 같은 화면인데 {@code type} 과 권한이 갈린다. 협력업체 관리의
 * 구매/영업과 같은 구조지만, 레거시가 파일과 경로를 아예 둘로 나눠 두어({@code screen}
 * 테이블도 {@code /task/purchase-task}·{@code /task/sell-task} 로 등록) 화면 글자
 * («발주»/«수주», «의뢰 시각»/«수주 시각»)까지 달라서 템플릿도 둘로 둔다.
 *
 * <p>등록·수정·삭제·내역 모달은 다음 단계에서 붙인다.
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    /**
     * @param taskService 수·발주 업무
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 발주 관리 목록.
     *
     * @param keyfield 검색 대상
     * @param keyword 검색어
     * @param nowPage 현재 페이지
     * @param model 뷰 모델
     * @return 발주 목록 템플릿
     */
    @GetMapping("/purchase-task")
    public String purchase(
            @RequestParam(required = false) String keyfield,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int nowPage,
            Model model) {
        return listView(TaskService.PURCHASE, "task/purchase-task", keyfield, keyword, nowPage, model);
    }

    /**
     * 수주 관리 목록.
     *
     * @param keyfield 검색 대상
     * @param keyword 검색어
     * @param nowPage 현재 페이지
     * @param model 뷰 모델
     * @return 수주 목록 템플릿
     */
    @GetMapping("/sell-task")
    public String sell(
            @RequestParam(required = false) String keyfield,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int nowPage,
            Model model) {
        return listView(TaskService.SELL, "task/sell-task", keyfield, keyword, nowPage, model);
    }

    private String listView(
            int type, String view, String keyfield, String keyword, int nowPage, Model model) {
        TaskSearch search = new TaskSearch(
                keyfield == null ? "" : keyfield, keyword == null ? "" : keyword);
        TaskService.TaskPage page = taskService.list(type, search, nowPage);

        model.addAttribute("tasks", page.rows());
        model.addAttribute("page", page.pagination());
        model.addAttribute("keyfield", keyfield == null ? "" : keyfield);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return view;
    }
}
