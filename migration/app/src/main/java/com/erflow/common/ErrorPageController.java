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
     * 없는 페이지 안내.
     *
     * <p>실제 404 응답에도 이 화면이 뜬다({@link ErrorViewConfig}). 매핑을 따로 두는
     * 것은 레거시에서 {@code notFoundError.jsp} 가 직접 열리는 주소였기 때문이다.
     *
     * @return 안내 템플릿
     */
    @GetMapping("/not-found-error")
    public String notFoundError() {
        return "error/not-found-error";
    }

    /**
     * 내부 서버 오류 안내.
     *
     * @return 안내 템플릿
     */
    @GetMapping("/internal-server-error")
    public String internalServerError() {
        return "error/internal-server-error";
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
}
