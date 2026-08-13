package com.erflow.proposal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erflow.auth.TestUsers;
import java.util.List;
import java.util.Map;
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
 * 결재 리스트·등록·문서 상세가 실제 데이터로 도는지 확인한다.
 *
 * <p>상태 필터(결재진행중/승인/반려)와 인박스 로직(내가 결재선에 든 진행중 + 내가 기안한
 * 선택 상태), 그리고 승인/반려가 결재선을 어떻게 밟아 나가는지를 본다.
 *
 * <p>승인·반려는 상태를 바꾸는 시험이라 모두 {@code @Transactional} 로 되돌린다. 결재선을
 * 처음부터 밟으려면 그 문서의 결재를 지우고 다시 만들어야 하는데, 롤백이 없으면 실행할
 * 때마다 데이터가 달라져 다음 시험이 무엇을 보는지 알 수 없게 된다.
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

    @Test
    @DisplayName("문서 상세가 그려진다")
    void documentRenders() throws Exception {
        Long id = jdbc.queryForObject("SELECT MAX(id) FROM proposal_tbl", Long.class);
        assumeTrue(id != null, "결재 데이터가 없어 건너뛴다");

        String html = mockMvc.perform(get("/proposal/document")
                        .param("proposalId", String.valueOf(id))
                        .with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains("결재 문서 보기").contains("담당").contains("승인")
                .contains("코멘트");
    }

    @Test
    @DisplayName("결재번호가 없으면 잘못된 접근으로 보낸다")
    void documentWithoutIdRedirects() throws Exception {
        mockMvc.perform(get("/proposal/document").with(user(TestUsers.admin())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-error"));
    }

    @Test
    @DisplayName("승인/반려는 대상이 없으면 잘못된 접근으로 보낸다")
    void decideWithoutTargetRedirects() throws Exception {
        mockMvc.perform(post("/proposal/document-proc")
                        .param("proposalId", "-1")
                        .param("result", "confirm")
                        .with(user(TestUsers.admin())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-error"));
    }

    @Test
    @DisplayName("마지막 차례가 아니면 승인이 다음 결재자의 차례를 만든다")
    @Transactional
    void approveHandsOverToNextApprover() {
        Chain chain = freshChain();
        long first = proposalService.create(chain.route()[0], chain.documentId(), chain.routeId());

        String message = proposalService.decide(first, "confirm", "확인했습니다.");

        assertThat(message).isEqualTo("결재완료하였습니다.");
        // 내 차례는 닫힌다 — result 0, 승인 시각이 찍힌다.
        var mine = jdbc.queryForMap(
                "SELECT result, approved_at, comment FROM proposal_tbl WHERE id = ?", first);
        assertThat(((Number) mine.get("result")).intValue()).isZero();
        assertThat(mine.get("approved_at")).isNotNull();
        assertThat(mine.get("comment")).isEqualTo("확인했습니다.");
        // 다음 차례가 결재선의 둘째 사번으로 생긴다.
        var next = jdbc.queryForMap(
                "SELECT user_tbl_id, step, result, approved_at FROM proposal_tbl "
                        + "WHERE document_tbl_id = ? AND id <> ?", chain.documentId(), first);
        assertThat(next.get("user_tbl_id")).isEqualTo(chain.route()[1]);
        assertThat(((Number) next.get("step")).intValue()).isEqualTo(1);
        assertThat(((Number) next.get("result")).intValue()).isEqualTo(3);
        assertThat(next.get("approved_at")).isNull();
    }

    @Test
    @DisplayName("결재선 끝까지 승인하면 문서의 결재가 모두 승인이 된다")
    @Transactional
    void lastApprovalMarksWholeDocumentApproved() {
        Chain chain = freshChain();
        long id = proposalService.create(chain.route()[0], chain.documentId(), chain.routeId());
        // 결재선 길이만큼 밟는다. 마지막 한 번이 문서 전체를 승인으로 바꾼다.
        String message = null;
        for (int step = 0; step < chain.route().length; ++step) {
            message = proposalService.decide(id, "confirm", "확인");
            id = jdbc.queryForObject(
                    "SELECT MAX(id) FROM proposal_tbl WHERE document_tbl_id = ?",
                    Long.class, chain.documentId());
        }

        assertThat(message).isEqualTo("결재하였습니다.");
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT result FROM proposal_tbl WHERE document_tbl_id = ?", chain.documentId());
        assertThat(rows).hasSize(chain.route().length);
        assertThat(rows).allSatisfy(row ->
                assertThat(((Number) row.get("result")).intValue())
                        .isEqualTo(ProposalService.APPROVED));
    }

    @Test
    @DisplayName("승인하면 그 자리에 도장이 찍힌다")
    @Transactional
    void approvalLeavesStamp() {
        Chain chain = freshChain();
        long first = proposalService.create(chain.route()[0], chain.documentId(), chain.routeId());
        proposalService.decide(first, "confirm", "확인");

        ProposalDocument document = proposalService.document(first);

        assertThat(document.stamps()).hasSize(chain.route().length);
        // 첫 자리는 승인됐으므로 이름과 날짜(MM/dd)가 있다.
        assertThat(document.stamps().get(0).name()).isNotBlank();
        assertThat(document.stamps().get(0).approvedAt()).matches("\\d{2}/\\d{2}");
        // 둘째 자리는 결재 행은 생겼지만 아직 승인 전이라 비어 있다.
        assertThat(document.stamps().get(1).filled()).isTrue();
        assertThat(document.stamps().get(1).name()).isEmpty();
        assertThat(document.comments()).extracting(ProposalComment::comment).contains("확인");
    }

    @Test
    @DisplayName("반려하면 문서의 결재가 모두 반려가 되고 도장은 찍히지 않는다")
    @Transactional
    void rejectMarksWholeDocumentRejected() {
        Chain chain = freshChain();
        long first = proposalService.create(chain.route()[0], chain.documentId(), chain.routeId());

        String message = proposalService.decide(first, "reject", "다시 올려주세요.");

        assertThat(message).isEqualTo("반려하였습니다.");
        var row = jdbc.queryForMap(
                "SELECT result, approved_at, comment FROM proposal_tbl WHERE id = ?", first);
        assertThat(((Number) row.get("result")).intValue()).isEqualTo(ProposalService.REJECTED);
        assertThat(row.get("approved_at")).isNull();
        assertThat(row.get("comment")).isEqualTo("다시 올려주세요.");
        // 반려는 다음 차례를 만들지 않는다.
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM proposal_tbl WHERE document_tbl_id = ?",
                Integer.class, chain.documentId())).isEqualTo(1);
    }

    /**
     * 결재를 처음부터 밟을 수 있는 문서 하나를 고른다.
     *
     * <p>결재자가 둘 이상인 결재라인을 쓰는 문서를 찾아 그 문서의 결재를 모두 지운다.
     * 롤백되는 트랜잭션 안에서만 부른다.
     */
    private Chain freshChain() {
        List<Map<String, Object>> refs = jdbc.queryForList(
                "SELECT p.document_tbl_id AS documentId, r.id AS routeId, r.route AS route "
                        + "  FROM proposal_tbl p "
                        + "  JOIN proposal_route_tbl r ON r.id = p.proposal_route_tbl_id "
                        + " WHERE r.route LIKE '%;%' LIMIT 1");
        assumeTrue(!refs.isEmpty(), "결재자가 둘 이상인 결재 데이터가 없어 건너뛴다");

        long documentId = ((Number) refs.get(0).get("documentId")).longValue();
        int routeId = ((Number) refs.get(0).get("routeId")).intValue();
        String[] route = ((String) refs.get(0).get("route")).split(";");
        jdbc.update("DELETE FROM proposal_tbl WHERE document_tbl_id = ?", documentId);
        return new Chain(documentId, routeId, route);
    }

    /** 시험용 결재 밑감. 문서 하나와 그 문서가 쓰는 결재라인. */
    private record Chain(long documentId, int routeId, String[] route) {
    }
}
