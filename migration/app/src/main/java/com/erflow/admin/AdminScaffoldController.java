package com.erflow.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 아직 옮기지 않은 관리자 화면의 임시 발판.
 *
 * <p>관리자 사이드메뉴는 네 갈래를 하드코딩으로 들고 있다. 링크를 빼면 레거시에 있던
 * 메뉴가 사라지므로 링크는 살려 두고, 갈 곳이 없는 동안 발판을 보여준다
 * ({@code common.ErrorPageController} 의 {@code /index}·{@code /profile} 과 같은 방식).
 *
 * <p><b>화면을 옮길 때마다 여기서 한 줄씩 지운다.</b> 이 클래스가 비면 관리자 화면이
 * 전부 옮겨진 것이다.
 */
@Controller
public class AdminScaffoldController {

    /**
     * 게시판 관리. {@code admin/board/adminBoardList.jsp}
     *
     * @param model 뷰 모델
     * @return 이관 전 안내 발판
     */
    @GetMapping("/admin/board/list")
    public String boardList(Model model) {
        return scaffold(model, "게시판 관리");
    }

    private String scaffold(Model model, String screenName) {
        model.addAttribute("screenName", screenName);
        return "scaffold/not-migrated";
    }
}
