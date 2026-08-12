package com.erflow.message;

import com.erflow.auth.ErflowUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 쪽지 화면.
 *
 * <pre>
 * message/index.jsp  GET /message?class=receiver|sender
 * </pre>
 *
 * <p>받은쪽지함(receiver)과 보낸쪽지함(sender)이 같은 화면을 쓰고 {@code class} 로
 * 갈린다. 쓰기·읽기·답장은 {@code window.open} 으로 여는 팝업이라 다음 단계에서 붙인다.
 *
 * <p>{@code screen} 테이블에는 {@code /message} 만 등록돼 있다 — 팝업은 로그인만
 * 검사한다(레거시도 그렇다).
 */
@Controller
public class MessageController {

    private final MessageService messageService;

    /**
     * @param messageService 쪽지 업무
     */
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 쪽지함 목록.
     *
     * <p>레거시는 {@code class} 가 없으면 receiver 로 보고, receiver/sender 가 아니면
     * 잘못된 접근으로 보냈다.
     *
     * @param className receiver 또는 sender. 없으면 receiver
     * @param keyfield 검색 대상
     * @param keyword 검색어
     * @param nowPage 현재 페이지
     * @param user 로그인 사용자
     * @param model 뷰 모델
     * @return 쪽지함 템플릿. class 가 잘못되면 잘못된 접근 화면
     */
    @GetMapping("/message")
    public String index(
            @RequestParam(name = "class", required = false) String className,
            @RequestParam(required = false) String keyfield,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int nowPage,
            @AuthenticationPrincipal ErflowUserDetails user,
            Model model) {

        String box = className == null || className.isBlank() ? MessageService.RECEIVER : className;
        if (!MessageService.RECEIVER.equals(box) && !MessageService.SENDER.equals(box)) {
            return "redirect:/access-error";
        }

        MessageSearch search = new MessageSearch(
                keyfield == null ? "" : keyfield, keyword == null ? "" : keyword);
        MessageService.MessagePage page = messageService.list(box, user.id(), search, nowPage);

        model.addAttribute("className", box);
        model.addAttribute("messages", page.rows());
        model.addAttribute("page", page.pagination());
        model.addAttribute("keyfield", keyfield == null ? "" : keyfield);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "message/index";
    }
}
