package com.erflow.proposal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erflow.auth.TestUsers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
 * 결재라인 목록이 실제 데이터로 그려지는지 확인한다.
 *
 * <p>결재경로(사번을 ; 로 이은 문자열)를 사람마다 «[부서/직급] 이름(사번)» 로 풀어
 * 보여주는지, 내 결재라인만 나오는지를 주로 본다.
 */
@SpringBootTest(properties = "server.port=0")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ProposalRouteScreenTest {

    private static final Path OUT = Path.of("build", "rendered");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProposalRouteService proposalRouteService;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeAll
    static void requireLocalConfig() {
        assumeTrue(
                new ClassPathResource("application-local.yml").exists(),
                "application-local.yml 이 없어 건너뛴다");
    }

    @Test
    @DisplayName("결재라인 목록이 그려진다")
    void routeListRenders() throws Exception {
        String html = mockMvc.perform(get("/proposal/route-list").with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Files.createDirectories(OUT);
        Files.writeString(OUT.resolve("proposal-route-list.html"), html);

        assertThat(html).contains("결재라인 관리").contains("결재관리번호").contains("결재경로");
    }

    @Test
    @DisplayName("결재경로가 사람마다 [부서/직급] 이름(사번) 으로 풀린다")
    void routeChainIsResolved() {
        // 결재라인을 하나 가진 사용자를 찾는다.
        List<String> owners = jdbc.queryForList(
                "SELECT DISTINCT user_id FROM proposal_route_view LIMIT 5", String.class);
        assumeTrue(!owners.isEmpty(), "결재라인 데이터가 없어 건너뛴다");

        for (String owner : owners) {
            var items = proposalRouteService.list(
                    owner, ProposalRouteSearch.none(), 1).items();
            if (items.isEmpty()) {
                continue;
            }
            // 첫 결재자는 만든 사람 자신이라 반드시 풀린다 — [부서/직급] 이름(사번) 형식.
            assertThat(items.get(0).routeChain()).contains("[").contains("]").contains("(");
            return;
        }
    }

    @Test
    @DisplayName("남의 결재라인은 보이지 않는다")
    void showsOnlyMine() {
        // 결재라인이 없는 사용자에게는 아무것도 나오지 않는다.
        var page = proposalRouteService.list(
                "no-such-user", ProposalRouteSearch.none(), 1);

        assertThat(page.items()).isEmpty();
    }
}
