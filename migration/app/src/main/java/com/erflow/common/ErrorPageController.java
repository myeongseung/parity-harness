package com.erflow.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 레거시 안내 화면과, 아직 옮기지 않은 화면의 임시 발판.
 *
 * <p>레거시는 화면마다 {@code response.sendRedirect("../permissionError.jsp")} 처럼
 * 안내 화면으로 보냈다. 도메인마다 필요하므로 공통으로 둔다.
 */
@Controller
public class ErrorPageController {

    /**
     * 권한 없음 안내.
     *
     * @return 안내 템플릿
     */
    @GetMapping("/permission-error")
    public String permissionError() {
        return "error/permission-error";
    }

    /**
     * 잘못된 접근 안내.
     *
     * @return 안내 템플릿
     */
    @GetMapping("/access-error")
    public String accessError() {
        return "error/access-error";
    }

    /**
     * 홈. 레거시 {@code index.jsp} 가 이관되면 이 매핑을 지운다.
     *
     * @param model 뷰 모델
     * @return 이관 전 안내 발판
     */
    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("screenName", "메인");
        return "scaffold/not-migrated";
    }

    /**
     * 관리자 홈. 레거시 {@code admin/admin.jsp} 가 이관되면 이 매핑을 지운다.
     *
     * @param model 뷰 모델
     * @return 이관 전 안내 발판
     */
    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("screenName", "관리자");
        return "scaffold/not-migrated";
    }
}
