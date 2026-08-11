package com.erflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ERFlow 애플리케이션 진입점.
 *
 * <p>JSP + Servlet(Model 1) 레거시를 Spring Boot 로 이관한 것이다. 레거시 원본은
 * {@code legacy/ERFlow} 에 있으며 그것이 정합성 판정의 정답이다. 화면 동작을 바꾸는
 * 변경은 {@code migration/design/00-decisions.md} 에 근거를 남긴다.
 */
@SpringBootApplication
public class ErflowApplication {

    /**
     * 애플리케이션을 기동한다.
     *
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(ErflowApplication.class, args);
    }
}
