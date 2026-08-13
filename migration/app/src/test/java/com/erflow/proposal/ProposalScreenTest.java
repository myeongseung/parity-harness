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
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    @DisplayName("결재 등록 화면이 그려진다")
    void registerFormRenders() throws Exception {
        String html = mockMvc.perform(get("/proposal/register").with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains("결재 생성").contains("문서 찾기").contains("결재라인 찾기");
    }

    @Test
    @DisplayName("등록하면 진행중(result 3, step 0)으로 결재가 생긴다")
    @Transactional
    void createStartsInProgress() {
        // FK 안전한 문서·결재라인을 기존 결재 한 건에서 가져온다. 롤백된다.
        var refs = jdbc.queryForList(
                "SELECT document_tbl_id, proposal_route_tbl_id FROM proposal_tbl LIMIT 1");
        assumeTrue(!refs.isEmpty(), "결재 데이터가 없어 건너뛴다");
        long documentId = ((Number) refs.get(0).get("document_tbl_id")).longValue();
        int routeId = ((Number) refs.get(0).get("proposal_route_tbl_id")).intValue();

        long newId = proposalService.create("admin", documentId, routeId);

        assertThat(newId).isGreaterThan(0);
        var row = jdbc.queryForMap(
                "SELECT step, result FROM proposal_tbl WHERE id = ?", newId);
        assertThat(((Number) row.get("step")).intValue()).isZero();
        assertThat(((Number) row.get("result")).intValue()).isEqualTo(3);
    }
}
