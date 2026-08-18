package com.erflow.profile;

import com.erflow.auth.ErflowUserDetails;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 프로필 화면.
 *
 * <pre>
 * profile.jsp             GET  /profile?id=&amp;date=
 * passwordCheck.jsp       GET  /profile/password-check
 * passwordCheckProc.jsp   POST /profile/password-check-proc
 * profileUpdate.jsp       GET  /profile/update
 * profileUpdateProc.jsp   POST /profile/update-proc
 * </pre>
 *
 * <p>보기 화면은 <b>남의 프로필도 열 수 있다</b> — 게시판의 작성자 이름과 주소록이
 * 여기로 걸린다. 다만 근무 현황과 «Edit» 은 자기 프로필에서만 나온다.
 *
 * <p>수정으로 들어가는 길목에 비밀번호를 다시 묻는 화면이 있다. 그러나 <b>수정 화면
 * 자체는 그 확인을 요구하지 않는다</b> — 주소를 직접 치면 그냥 열린다. 레거시가
 * 그러하며 그대로 옮긴다(D-079).
 */
@Controller
public class ProfileController {

    private final ProfileService profileService;

    /**
     * @param profileService 프로필 업무
     */
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * 프로필 보기.
     *
     * @param id 볼 사람의 사번
     * @param date {@code yyyy-MM}. 없으면 이번 달
     * @param user 로그인 사용자
     * @param model 뷰 모델
     * @return 보기 템플릿
     */
    @GetMapping("/profile")
    public String view(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String date,
            @AuthenticationPrincipal ErflowUserDetails user,
            Model model) {

        ProfileUser owner = profileService.get(id);
        if (owner == null) {
            return "redirect:/access-error";
        }
        YearMonth month = profileService.thisMonth();
        if (date != null && !date.isBlank()) {
            try {
                month = YearMonth.parse(date);
            } catch (DateTimeParseException e) {
                // 레거시도 읽을 수 없는 달이면 화면을 열지 않는다.
                return "redirect:/access-error";
            }
        }
        model.addAttribute("owner", owner);
        model.addAttribute("editable", user.id().equals(owner.id()));
        model.addAttribute("month", month.toString());
        model.addAttribute("minMonth", profileService.minMonth().toString());
        model.addAttribute("calendar",
                profileService.calendar(user.id(), owner.id(), month));
        return "profile/view";
    }

    /**
     * 비밀번호 재확인 화면.
     *
     * @param user 로그인 사용자
     * @param model 뷰 모델
     * @return 확인 템플릿
     */
    @GetMapping("/profile/password-check")
    public String passwordCheckForm(
            @AuthenticationPrincipal ErflowUserDetails user, Model model) {

        model.addAttribute("userId", user.id());
        return "profile/password-check";
    }

    /**
     * 비밀번호 재확인 처리.
     *
     * <p>맞으면 수정 화면으로, 틀리면 알린 뒤 프로필로 돌아간다.
     *
     * @param password 입력한 비밀번호
     * @param user 로그인 사용자
     * @param model 뷰 모델
     * @return 결과 템플릿
     */
    @PostMapping("/profile/password-check-proc")
    public String passwordCheck(
            @RequestParam(required = false) String password,
            @AuthenticationPrincipal ErflowUserDetails user,
            Model model) {

        if (password == null) {
            return "redirect:/access-error";
        }
        if (profileService.passwordMatches(user.id(), password)) {
            model.addAttribute("nextPage", "/profile/update");
            return "profile/result";
        }
        model.addAttribute("message", "비밀번호가 틀렸습니다.");
        model.addAttribute("nextPage", "/profile?id=" + user.id());
        return "profile/result";
    }

    /**
     * 프로필 수정 화면.
     *
     * <p>레거시는 로그인할 때 세션에 담아 둔 사본을 채워 넣었다. 여기서는 그때마다
     * 다시 읽는다(D-078).
     *
     * @param user 로그인 사용자
     * @param model 뷰 모델
     * @return 수정 템플릿
     */
    @GetMapping("/profile/update")
    public String updateForm(
            @AuthenticationPrincipal ErflowUserDetails user, Model model) {

        model.addAttribute("owner", profileService.get(user.id()));
        return "profile/update";
    }

    /**
     * 프로필 수정 처리.
     *
     * <p>여섯 칸이 <b>하나라도 오지 않으면</b> 잘못된 접근으로 본다. 빈 값은 괜찮다 —
     * 아예 오지 않은 것과 다르다.
     *
     * @param name 이름
     * @param email 이메일
     * @param postalCode 우편번호
     * @param address1 도로명 주소
     * @param address2 상세 주소
     * @param mobilePhone 개인 전화
     * @param user 로그인 사용자
     * @param model 뷰 모델
     * @return 결과 템플릿
     */
    @PostMapping("/profile/update-proc")
    public String update(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String address1,
            @RequestParam(required = false) String address2,
            @RequestParam(required = false) String mobilePhone,
            @AuthenticationPrincipal ErflowUserDetails user,
            Model model) {

        if (name == null || email == null || postalCode == null
                || address1 == null || address2 == null || mobilePhone == null) {
            return "redirect:/access-error";
        }
        // 레거시는 여섯 칸을 다듬어 두고 우편번호와 개인 전화만 다듬지 않은 원본을
        // 다시 꺼내 쓴다. 그래서 그 둘만 빈 글자가 null 이 되지 않는다(D-080).
        ProfileUser updated = new ProfileUser(
                user.id(), blankToNull(name), blankToNull(email), null,
                mobilePhone, postalCode, blankToNull(address1), blankToNull(address2));

        boolean done = profileService.update(updated);
        model.addAttribute("message",
                done ? "프로필을 수정했습니다." : "프로필을 수정하지 못했습니다.");
        model.addAttribute("nextPage", "/profile?id=" + user.id());
        return "profile/result";
    }

    private static String blankToNull(String value) {
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
