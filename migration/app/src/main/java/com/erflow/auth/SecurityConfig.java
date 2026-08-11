package com.erflow.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 보안 설정.
 *
 * <h2>레거시와 달라지는 것</h2>
 *
 * <p>레거시에는 CSRF 방어가 없었다. 이관하면서 켠다. 그 결과 <b>모든 form 에
 * {@code _csrf} hidden 입력이 생긴다</b> — 정합성 게이트가 이것을 "레거시에 없던 요소"
 * 로 잡으며, 그게 맞다. 사유를 적어 allowlist 에 등록해 통과시킨다.
 *
 * <p>세션 고정 공격 방어도 프레임워크 기본값을 쓴다. 레거시는 로그인 전후로 세션을
 * 바꾸지 않았다.
 *
 * <h2>레거시를 따르는 것</h2>
 *
 * <p>비밀번호 해시({@link ErflowPasswordEncoder}), 부서·직급 비트마스크
 * ({@link Permissions}), 화면별 권한({@link ScreenAuthorizationManager}) 은 모두
 * 레거시 규칙 그대로다.
 */
@Configuration
public class SecurityConfig {

    /**
     * 레거시 해시를 그대로 쓰는 인코더.
     *
     * @return 비밀번호 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new ErflowPasswordEncoder();
    }

    /**
     * 필터 체인.
     *
     * @param http 보안 설정 빌더
     * @param screenAuthorization 화면 권한 판정
     * @return 필터 체인
     * @throws Exception 설정 실패 시
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, ScreenAuthorizationManager screenAuthorization) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico")
                            .permitAll()
                        .requestMatchers("/login", "/login/password-error", "/login/find-password")
                            .permitAll()
                        // 비밀번호 변경은 '변경 필요' 상태에서만 들어간다
                        .requestMatchers("/login/change-password")
                            .hasAuthority(ErflowUserDetails.ROLE_PASSWORD_CHANGE)
                        .requestMatchers("/login/password-ok", "/permission-error", "/access-error")
                            .permitAll()
                        .anyRequest().access(screenAuthorization))
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("id")
                        .passwordParameter("password")
                        .successHandler(new LoginSuccessHandler())
                        // 레거시는 실패 시 passwordError.html 로 보냈다
                        .failureUrl("/login/password-error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/login/logout-proc")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true))
                // 미인가는 레거시와 같은 안내 화면으로 보낸다
                .exceptionHandling(ex -> ex.accessDeniedPage("/permission-error"));

        return http.build();
    }
}
