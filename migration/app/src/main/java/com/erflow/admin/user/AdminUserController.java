package com.erflow.admin.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 사원 관리 화면.
 *
 * <pre>
 * admin/user/userList.jsp     GET /admin/user/list
 * admin/user/userAddress.jsp  GET /admin/user/address?id=
 * </pre>
 *
 * <p>권한은 여기서 보지 않는다. 레거시는 화면마다 {@code isAdmin(session)} 을 물었지만,
 * 이 아래는 {@code SecurityConfig} 의 {@code /admin/**} 규칙이 통째로 막는다(D-053).
 */
@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * @param adminUserService 사원 관리 업무
     */
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * 사원 리스트.
     *
     * @param keyfield 검색할 칸(사번/이름/부서/직급). 없으면 전체
     * @param keyword 검색어
     * @param nowPage 현재 페이지
     * @param model 뷰 모델
     * @return 사원 리스트 템플릿
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(required = false) String keyfield,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int nowPage,
            Model model) {

        AdminUserSearch search = new AdminUserSearch(
                keyfield == null ? "" : keyfield, keyword == null ? "" : keyword);
        AdminUserService.UserPage page = adminUserService.list(search, nowPage);

        model.addAttribute("users", page.rows());
        model.addAttribute("page", page.pagination());
        model.addAttribute("keyfield", search.keyfield());
        model.addAttribute("keyword", search.keyword());
        return "admin/user/list";
    }

    /**
     * 주소 팝업.
     *
     * <p>레거시는 사원을 찾지 못하면 로그인 화면으로 보내려 했다. 그런데 가리키는
     * 주소가 없는 파일이고({@code ../../login.jsp}), {@code return} 이 없어 그 전에
     * {@code NullPointerException} 으로 죽는다. 죽음은 옮기지 않고 다른 화면과 같은
     * 잘못된 접근 안내로 보낸다(D-033 과 같은 판단).
     *
     * @param id 사번
     * @param model 뷰 모델
     * @return 주소 팝업 템플릿. 대상이 없으면 잘못된 접근 화면
     */
    @GetMapping("/address")
    public String address(@RequestParam(required = false) String id, Model model) {
        AdminUserAddress address = id == null ? null : adminUserService.address(id);
        if (address == null) {
            return "redirect:/access-error";
        }
        model.addAttribute("address", address);
        return "admin/user/address";
    }
}
