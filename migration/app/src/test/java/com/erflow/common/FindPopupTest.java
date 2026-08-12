package com.erflow.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erflow.auth.TestUsers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 코드 찾기 팝업.
 *
 * <p>검색 전과 «검색어를 비운 채 누른 것»이 다르다는 점을 주로 본다. 레거시가
 * 그렇게 갈라 놓았고, 눈으로는 잘 드러나지 않는 차이다.
 */
@SpringBootTest(properties = "server.port=0")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class FindPopupTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void requireLocalConfig() {
        assumeTrue(
                new ClassPathResource("application-local.yml").exists(),
                "application-local.yml 이 없어 건너뛴다");
    }

    /**
     * 팝업을 연다. 검색어는 파라미터로 넘긴다 — 주소에 한글을 그대로 박으면
     * MockMvc 가 ISO-8859-1 로 풀어 글자가 깨진다. 실제 Tomcat 은 UTF-8 로 푼다.
     *
     * @param path 경로
     * @param search 검색어. {@code null} 이면 파라미터를 아예 보내지 않는다
     * @return 받은 HTML
     */
    private String open(String path, String search) throws Exception {
        var request = get(path).with(user(TestUsers.admin()));
        if (search != null) {
            request = request.param("search", search);
        }
        return mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("창을 처음 열면 도움말이 나온다")
    void firstOpenShowsTips() throws Exception {
        String html = open("/find/bank", null);

        assertThat(html).contains("tip").contains("산업은행");
        assertThat(html).doesNotContain("a-bank-info");
    }

    @Test
    @DisplayName("검색어를 비운 채 누르면 전체 목록이 나온다")
    void emptySearchShowsEverything() throws Exception {
        // 레거시는 search 파라미터가 없을 때와 빈 문자열일 때를 구별한다.
        String html = open("/find/bank", "");

        assertThat(html).contains("a-bank-info");
        assertThat(html).doesNotContain("<h3>tip</h3>");
    }

    @Test
    @DisplayName("은행 이름으로 찾는다")
    void findsBankByName() throws Exception {
        String html = open("/find/bank", "기업은행");

        assertThat(html).contains("기업은행");
    }

    @Test
    @DisplayName("은행 코드로도 찾는다")
    void findsBankByCode() throws Exception {
        // CodeTable.matching 은 이름만 보지만 팝업은 코드도 훑어야 한다.
        String html = open("/find/bank", "003");

        assertThat(html).contains("003");
        assertThat(html).contains("a-bank-info");
    }

    @Test
    @DisplayName("업종 팝업은 값을 data 속성으로 넘긴다")
    void workPopupUsesDataAttributes() throws Exception {
        String html = open("/find/work", "어업");

        // 은행 팝업은 onclick, 업종 팝업은 data-key/data-value 다. 레거시 그대로다.
        assertThat(html).contains("a-work-info").contains("data-key=");
    }

    @Test
    @DisplayName("찾는 것이 없으면 목록만 비고 화면은 뜬다")
    void noMatchStillRenders() throws Exception {
        String html = open("/find/work", "없는업종이름12345");

        assertThat(html).doesNotContain("a-work-info");
        assertThat(html).contains("업체 코드 찾기 서비스 제공");
    }
}
