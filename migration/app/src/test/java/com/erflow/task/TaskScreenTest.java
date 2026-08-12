package com.erflow.task;

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
 * 수·발주 목록이 실제 데이터로 그려지는지 확인한다.
 *
 * <p>연결된 MariaDB 를 상대로 돈다. 발주(type=1)와 수주(type=0)가 서로 다른 목록을
 * 보여주는지, 검색이 대상 칸만 훑는지를 주로 본다.
 */
@SpringBootTest(properties = "server.port=0")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class TaskScreenTest {

    private static final Path OUT = Path.of("build", "rendered");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

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
    @DisplayName("발주 목록이 그려지고 화면 글자가 발주다")
    void purchaseListRenders() throws Exception {
        String html = render("/task/purchase-task", "task-purchase.html");

        assertThat(html).contains("발주 관리").contains("의뢰 시각");
        assertThat(html).doesNotContain("수주 시각");
    }

    @Test
    @DisplayName("수주 목록이 그려지고 화면 글자가 수주다")
    void sellListRenders() throws Exception {
        String html = render("/task/sell-task", "task-sell.html");

        assertThat(html).contains("수주 관리").contains("수주 시각");
    }

    @Test
    @DisplayName("발주와 수주는 서로 다른 목록이다")
    void purchaseAndSellAreDifferentSets() {
        List<TaskRow> purchase = taskService.list(
                TaskService.PURCHASE, TaskSearch.none(), 1).rows();
        List<TaskRow> sell = taskService.list(
                TaskService.SELL, TaskSearch.none(), 1).rows();

        // 같은 화면 골격을 쓰지만 type 으로 갈린다. 한쪽 id 가 다른 쪽에 섞이면
        // type 필터가 빠진 것이다.
        assertThat(purchase).isNotEmpty();
        List<Integer> sellIds = sell.stream().map(TaskRow::id).toList();
        assertThat(purchase).noneMatch(row -> sellIds.contains(row.id()));
    }

    @Test
    @DisplayName("담당직원명으로 걸러낸다")
    void filtersByUserName() {
        var all = taskService.list(TaskService.PURCHASE, TaskSearch.none(), 1).rows();
        assumeTrue(!all.isEmpty(), "발주 데이터가 없어 건너뛴다");
        String name = all.get(0).userName();

        var filtered = taskService.list(
                TaskService.PURCHASE, new TaskSearch("userName", name), 1).rows();

        assertThat(filtered).isNotEmpty()
                .allSatisfy(row -> assertThat(row.userName()).contains(name));
    }

    @Test
    @DisplayName("검색 대상이 아닌 화이트리스트 밖 keyfield 는 전체 조회로 떨어진다")
    void unknownKeyfieldFallsBackToAll() {
        var all = taskService.list(TaskService.PURCHASE, TaskSearch.none(), 1)
                .pagination().totalRecord();
        var junk = taskService.list(
                TaskService.PURCHASE, new TaskSearch("id; DROP TABLE task_tbl --", "x"), 1)
                .pagination().totalRecord();

        // 화이트리스트 밖이면 조건을 만들지 않는다 — 전체 건수가 같아야 한다.
        assertThat(junk).isEqualTo(all);
    }

    @Test
    @DisplayName("상태 검색어가 숫자가 아니면 목록이 빈다")
    void nonNumericStatusYieldsEmpty() {
        // 레거시는 Integer.parseInt 예외를 삼켜 빈 목록을 냈다(TaskSearch.isBrokenStatus).
        var page = taskService.list(
                TaskService.PURCHASE, new TaskSearch("status", "진행중"), 1);

        assertThat(page.rows()).isEmpty();
        assertThat(page.pagination().totalRecord()).isZero();
    }
}
