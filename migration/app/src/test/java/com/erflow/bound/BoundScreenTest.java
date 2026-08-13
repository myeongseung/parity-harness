package com.erflow.bound;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 입·출고 목록이 실제 데이터로 그려지는지 확인한다.
 *
 * <p>입고(type=0)와 출고(type=1)가 서로 다른 목록을 보여주는지, 검색이 대상 칸만
 * 훑는지를 주로 본다.
 */
@SpringBootTest(properties = "server.port=0")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class BoundScreenTest {

    private static final Path OUT = Path.of("build", "rendered");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoundService boundService;

    @BeforeAll
    static void requireLocalConfig() {
        assumeTrue(
                new ClassPathResource("application-local.yml").exists(),
                "application-local.yml 이 없어 건너뛴다");
    }

    private String render(String path, String fileName) throws Exception {
        String html = mockMvc.perform(get(path).with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Files.createDirectories(OUT);
        Files.writeString(OUT.resolve(fileName), html);
        return html;
    }

    @Test
    @DisplayName("입고 목록이 그려지고 화면 글자가 입고다")
    void inboundListRenders() throws Exception {
        String html = render("/bound/inbound", "bound-inbound.html");

        assertThat(html).contains("입고 제품 관리").contains("입고 시간").contains("입고자");
        assertThat(html).doesNotContain("출고 시간");
    }

    @Test
    @DisplayName("출고 목록은 화면 글자가 출고다")
    void outboundListRenders() throws Exception {
        String html = render("/bound/outbound", "bound-outbound.html");

        assertThat(html).contains("출고 제품 관리").contains("출고 시간").contains("출고자");
    }

    @Test
    @DisplayName("입고와 출고는 서로 다른 목록이다")
    void inboundAndOutboundAreDifferentSets() {
        List<BoundRow> in = boundService.list(BoundService.INBOUND, BoundSearch.none(), 1).rows();
        List<BoundRow> out = boundService.list(BoundService.OUTBOUND, BoundSearch.none(), 1).rows();
        assumeTrue(!in.isEmpty() || !out.isEmpty(), "입·출고 데이터가 없어 건너뛴다");

        List<Integer> outIds = out.stream().map(BoundRow::id).toList();
        assertThat(in).noneMatch(row -> outIds.contains(row.id()));
    }

    @Test
    @DisplayName("제품명으로 걸러낸다")
    void filtersByProductName() {
        var all = boundService.list(BoundService.INBOUND, BoundSearch.none(), 1).rows();
        assumeTrue(!all.isEmpty(), "입고 데이터가 없어 건너뛴다");
        String name = all.get(0).productName();

        var filtered = boundService.list(
                BoundService.INBOUND, new BoundSearch("productName", name), 1).rows();

        assertThat(filtered).isNotEmpty()
                .allSatisfy(row -> assertThat(row.productName()).contains(name));
    }

    @Test
    @DisplayName("화이트리스트 밖 keyfield 는 전체 조회로 떨어진다")
    void unknownKeyfieldFallsBackToAll() {
        int all = boundService.list(BoundService.INBOUND, BoundSearch.none(), 1)
                .pagination().totalRecord();
        int junk = boundService.list(
                BoundService.INBOUND, new BoundSearch("id2; DROP TABLE bound_tbl --", "x"), 1)
                .pagination().totalRecord();

        assertThat(junk).isEqualTo(all);
    }
}
