package com.erflow.auth;

import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 요청 경로로 화면 권한을 판정한다.
 *
 * <p>레거시는 화면마다 상단에 같은 코드를 되풀이했다.
 *
 * <pre>
 * final String PROGRAM_CODE = "8A4364846CD2FC49...";
 * if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
 *     response.sendRedirect("../permissionError.jsp");
 *     return;
 * }
 * </pre>
 *
 * <p>39개 JSP 에 이 조각이 박혀 있었다. 이제 {@code screen} 테이블이 경로와 프로그램을
 * 잇고 판정은 여기 한 곳에서 한다. 새 화면을 만들며 검사를 빠뜨리는 실수도 구조적으로
 * 막힌다 — {@code screen} 에 행이 있으면 반드시 검사되고, 없으면 애초에 권한 대상이
 * 아니다.
 */
@Component
public class ScreenAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    private final AuthMapper authMapper;

    /**
     * @param authMapper 인증 조회 매퍼
     */
    public ScreenAuthorizationManager(AuthMapper authMapper) {
        this.authMapper = authMapper;
    }

    /**
     * 접근 가부를 판정한다.
     *
     * @param authentication 인증 정보 공급자
     * @param context 요청 문맥
     * @return 허용 여부
     */
    @Override
    @Transactional(readOnly = true)
    public AuthorizationDecision authorize(
            Supplier<? extends Authentication> authentication,
            RequestAuthorizationContext context) {

        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof ErflowUserDetails user)) {
            return new AuthorizationDecision(false);
        }
        if (user.passwordChangeRequired()) {
            // 비밀번호를 바꾸기 전에는 아무 화면도 볼 수 없다. 레거시도 로그인시키지 않았다.
            return new AuthorizationDecision(false);
        }

        ScreenAccess access = authMapper.findScreenAccess(context.getRequest().getRequestURI());
        if (access == null) {
            // screen 에 없는 경로는 권한 대상이 아니다. 로그인만 되어 있으면 통과한다.
            // 레거시에서 PROGRAM_CODE 가 없던 화면들이 여기 해당한다.
            return new AuthorizationDecision(true);
        }
        return new AuthorizationDecision(Permissions.hasProgramPermission(
                user.deptPermission(), user.jobPermission(),
                access.deptLevel(), access.jobLevel()));
    }
}
