package com.erflow.admin.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erflow.auth.TestUsers;
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
 * 사원 리스트와 주소 팝업이 실제 데이터로 도는지 확인한다.
 *
 * <p>관리자 화면이라 <b>누가 볼 수 있는가</b>를 함께 본다. 레거시는 화면마다
 * {@code isAdmin} 을 물었고 우리는 경로로 막는다(D-053) — 막히는지 확인하지 않으면
 * 사원 명부와 주민등록번호가 로그인한 누구에게나 열린다.
 */
@SpringBootTest(properties = "server.port=0")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AdminUserScreenTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeAll
    static void requireLocalConfig() {
        assumeTrue(
                new ClassPathResource("application-local.yml").exists(),
                "application-local.yml 이 없어 건너뛴다");
    }

    @Test
    @DisplayName("사원 리스트가 그려진다")
    void listRenders() throws Exception {
        String html = mockMvc.perform(get("/admin/user/list").with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains("관리자 &gt; 사원 &gt; 사원관리")
                .contains("주민등록번호").contains("내·외국인")
                // 관리자 사이드메뉴가 함께 그려진다
                .contains("사원 관리").contains("권한 관리").contains("게시판 관리");
    }

    @Test
    @DisplayName("관리자가 아니면 사원 리스트를 볼 수 없다")
    void nonAdminCannotSeeUserList() throws Exception {
        // 레거시는 permissionError.jsp 로 보냈고 우리는 같은 화면을 403 으로 내보낸다
        // (다른 화면의 권한 거부와 같은 방식이다).
        mockMvc.perform(get("/admin/user/list").with(user(TestUsers.noPermission())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자가 아니면 주소 팝업도 볼 수 없다")
    void nonAdminCannotSeeAddress() throws Exception {
        mockMvc.perform(get("/admin/user/address").param("id", "admin")
                        .with(user(TestUsers.noPermission())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 발판 화면도 관리자만 볼 수 있다")
    void scaffoldIsAdminOnly() throws Exception {
        mockMvc.perform(get("/admin/board/list").with(user(TestUsers.noPermission())))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/board/list").with(user(TestUsers.admin())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("목록에서 admin 계정은 빠진다")
    void adminAccountIsExcluded() {
        var rows = adminUserService.list(AdminUserSearch.none(), 1).rows();

        assertThat(rows).isNotEmpty();
        assertThat(rows).extracting(AdminUserRow::id).doesNotContain("admin");
    }

    @Test
    @DisplayName("고른 칸으로만 검색한다 — 이름으로 찾으면 사번은 걸리지 않는다")
    void searchesOnlyTheChosenField() {
        List<String> names = jdbc.queryForList(
                "SELECT name FROM user_view WHERE id != 'admin' LIMIT 1", String.class);
        assumeTrue(!names.isEmpty(), "사용자가 없어 건너뛴다");
        String name = names.get(0);

        var byName = adminUserService.list(new AdminUserSearch("name", name), 1).rows();
        var byId = adminUserService.list(new AdminUserSearch("id", name), 1).rows();

        assertThat(byName).extracting(AdminUserRow::name).contains(name);
        // 사번 칸을 이름으로 훑으면 아무것도 나오지 않는다. 칸이 실제로 갈린다는 뜻이다.
        assertThat(byId).isEmpty();
    }

    @Test
    @DisplayName("화면이 주지 않은 칸으로는 검색되지 않는다 — SQL 에 끼어들지 못한다")
    void unknownFieldNeverReachesSql() {
        // 레거시는 이 값을 SQL 에 그대로 이어 붙였다(D-054). 조건이 붙지 않아야 하고,
        // 무엇보다 예외 없이 전체 목록이 나와야 한다.
        var injected = adminUserService.list(
                new AdminUserSearch("id = 'admin' or 1=1 -- ", "x"), 1);
        var all = adminUserService.list(AdminUserSearch.none(), 1);

        assertThat(injected.rows()).hasSameSizeAs(all.rows());
        assertThat(injected.pagination().totalRecord())
                .isEqualTo(all.pagination().totalRecord());
        assertThat(injected.rows()).extracting(AdminUserRow::id).doesNotContain("admin");
    }

    @Test
    @DisplayName("주소 팝업이 그려진다")
    void addressRenders() throws Exception {
        List<String> ids = jdbc.queryForList(
                "SELECT id FROM user_view WHERE id != 'admin' LIMIT 1", String.class);
        assumeTrue(!ids.isEmpty(), "사용자가 없어 건너뛴다");

        String html = mockMvc.perform(get("/admin/user/address").param("id", ids.get(0))
                        .with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains("의 주소").contains("우편번호 :")
                .contains("도로명 주소 :").contains("상세 주소 :");
    }

    @Test
    @DisplayName("없는 사번이면 잘못된 접근으로 보낸다")
    void addressWithoutUserRedirects() throws Exception {
        mockMvc.perform(get("/admin/user/address").param("id", "no-such-user")
                        .with(user(TestUsers.admin())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-error"));
    }
}
