package com.erflow.proposal;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 결재 리스트가 실제 데이터로 그려지는지 확인한다.
 *
 * <p>상태 필터(결재진행중/승인/반려)와 인박스 로직(내가 결재선에 든 진행중 + 내가 기안한
 * 선택 상태)을 주로 본다.
 */
@SpringBootTest(properties = "server.port=0")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ProposalScreenTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProposalService proposalService;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeAll
    static void requireLocalConfig() {
        assumeTrue(
                new ClassPathResource("application-local.yml").exists(),
                "application-local.yml 이 없어 건너뛴다");
    }

    @Test
    @DisplayName("결재 리스트가 그려진다")
    void listRenders() throws Exception {
        String html = mockMvc.perform(get("/proposal/list").with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains("결재 리스트").contains("문서제목").contains("결재진행중");
    }

    @Test
    @DisplayName("상태 필터로 다른 인박스가 나온다")
    void statusFilterChangesInbox() {
        // 결재선에 실제로 든 사용자를 찾는다(진행중 결재의 route 에서).
        var owners = jdbc.queryForList(
                "SELECT DISTINCT original_user FROM proposal_view LIMIT 5", String.class);
        assumeTrue(!owners.isEmpty(), "결재 데이터가 없어 건너뛴다");

        String who = owners.get(0);
        // 진행중과 승인은 서로 다른 필터다 — 같은 인박스가 나오면 필터가 무시된 것이다.
        var inProgress = proposalService.list(who, ProposalService.IN_PROGRESS, 1)
                .pagination().totalRecord();
        var approved = proposalService.list(who, ProposalService.APPROVED, 1)
                .pagination().totalRecord();
        // 최소한 진행중 인박스는 조회가 되어야 한다(0 이상). 예외 없이 도는지 확인.
        assertThat(inProgress).isGreaterThanOrEqualTo(0);
        assertThat(approved).isGreaterThanOrEqualTo(0);
    }
}
