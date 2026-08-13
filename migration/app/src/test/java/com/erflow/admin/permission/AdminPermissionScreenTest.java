package com.erflow.admin.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erflow.auth.Permissions;
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
import org.springframework.transaction.annotation.Transactional;

/**
 * 직급·부서 권한 화면이 실제 데이터로 도는지 확인한다.
 *
 * <p>여기서 보는 것은 <b>저장되는 숫자</b>다. 체크박스는 레거시와 신규 양쪽에 다 있어
 * 게이트가 통과시키지만, 그 체크가 어떤 비트마스크가 되는지는 게이트가 못 본다.
 * 틀리면 «없던 권한이 생기는» 쪽으로 틀린다.
 *
 * <p>상태를 바꾸는 시험은 전부 {@code @Transactional} 로 되돌린다.
 */
@SpringBootTest(properties = "server.port=0")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AdminPermissionScreenTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminPermissionService permissionService;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeAll
    static void requireLocalConfig() {
        assumeTrue(
                new ClassPathResource("application-local.yml").exists(),
                "application-local.yml 이 없어 건너뛴다");
    }

    @Test
    @DisplayName("직급·부서 리스트가 그려진다")
    void listRenders() throws Exception {
        String html = mockMvc.perform(
                        get("/admin/permission/job-dept-list").with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains("직급 리스트").contains("부서 리스트")
                .contains("직급 권한 및 정보").contains("부서 권한 및 정보");
    }

    @Test
    @DisplayName("관리자와 최고관리자는 목록에 나오지 않는다")
    void adminRowsAreHidden() {
        var page = permissionService.list(null, null);

        assertThat(page.jobs()).extracting(PermissionRow::classId).doesNotContain(-1);
        assertThat(page.depts()).extracting(PermissionRow::classId).doesNotContain(-1);
        assertThat(page.jobs()).isNotEmpty();
        assertThat(page.depts()).isNotEmpty();
    }

    @Test
    @DisplayName("검색어로 좁혀진다")
    void keywordNarrowsTheList() {
        String name = permissionService.list(null, null).depts().get(0).name();

        var narrowed = permissionService.list(null, name);

        assertThat(narrowed.depts()).extracting(PermissionRow::name).contains(name);
        assertThat(narrowed.depts().size())
                .isLessThanOrEqualTo(permissionService.list(null, null).depts().size());
    }

    @Test
    @DisplayName("수정 화면은 자기 자신을 체크박스로 내놓지 않는다")
    void updateFormExcludesItself() {
        int deptId = permissionService.list(null, null).depts().get(0).classId();

        var form = permissionService.deptForm(deptId);

        assertThat(form.choices()).extracting(PermissionChoice::classId).doesNotContain(deptId);
        assertThat(form.dept().id()).isEqualTo(deptId);
    }

    @Test
    @DisplayName("이미 가진 권한에는 체크가 켜져 있다")
    void updateFormChecksWhatIsAlreadyGranted() {
        // 사원(직급)은 대리 비트를 겸하고 있다. 그 체크가 켜져 있어야 한다.
        var jobs = permissionService.list(null, null).jobs();
        PermissionRow granted = jobs.stream()
                .filter(row -> row.permission() != row.level())
                .findFirst().orElse(null);
        assumeTrue(granted != null, "겸직 권한을 가진 직급이 없어 건너뛴다");

        var form = permissionService.jobForm(granted.classId());
        List<PermissionChoice> checked = form.choices().stream()
                .filter(PermissionChoice::checked).toList();

        assertThat(checked).isNotEmpty();
        for (PermissionChoice choice : checked) {
            long level = levelOf(jobs, choice.classId());
            assertThat(granted.permission() & level).isNotZero();
        }
    }

    @Test
    @DisplayName("수정하면 «자기 비트 + 체크한 비트» 가 저장된다")
    @Transactional
    void updateStoresSelfPlusChecked() {
        var depts = permissionService.list(null, null).depts();
        assumeTrue(depts.size() >= 3, "부서가 셋 미만이라 건너뛴다");
        PermissionRow self = depts.get(0);
        PermissionRow other = depts.get(1);

        boolean updated = permissionService.updateDept(
                self.classId(), self.name(), null, null, null, List.of(other.classId()));

        assertThat(updated).isTrue();
        long stored = storedDeptPermission(self.classId());
        assertThat(stored).isEqualTo(self.level() | other.level());
    }

    @Test
    @DisplayName("체크를 다 지워도 자기 비트는 남는다")
    @Transactional
    void updateKeepsOwnBit() {
        PermissionRow self = permissionService.list(null, null).depts().get(0);

        permissionService.updateDept(self.classId(), self.name(), null, null, null, List.of());

        assertThat(storedDeptPermission(self.classId())).isEqualTo(self.level());
    }

    @Test
    @DisplayName("부서를 고치면 주소가 지워진다 — 레거시 그대로다")
    @Transactional
    void updateWipesAddress() {
        PermissionRow self = permissionService.list(null, null).depts().get(0);
        jdbc.update("UPDATE dept_tbl SET postal_code = '48058', address1 = '부산', "
                + "address2 = '3층' WHERE id = ?", self.classId());

        // 화면이 주소칸을 채워 주지 않으므로 빈 값이 그대로 올라온다(D-059).
        permissionService.updateDept(self.classId(), self.name(), null, null, null, List.of());

        var row = jdbc.queryForMap(
                "SELECT postal_code, address1, address2 FROM dept_tbl WHERE id = ?",
                self.classId());
        assertThat(row.get("postal_code")).isNull();
        assertThat(row.get("address1")).isNull();
        assertThat(row.get("address2")).isNull();
    }

    @Test
    @DisplayName("부서장은 수정해도 지워지지 않는다")
    @Transactional
    void updateKeepsManager() {
        PermissionRow self = permissionService.list(null, null).depts().get(0);
        String manager = jdbc.queryForObject(
                "SELECT id FROM user_tbl WHERE id != 'admin' LIMIT 1", String.class);
        jdbc.update("UPDATE dept_tbl SET user_tbl_manager_id = ? WHERE id = ?",
                manager, self.classId());

        permissionService.updateDept(self.classId(), self.name(), null, null, null, List.of());

        assertThat(jdbc.queryForObject(
                "SELECT user_tbl_manager_id FROM dept_tbl WHERE id = ?",
                String.class, self.classId())).isEqualTo(manager);
    }

    @Test
    @DisplayName("새 부서는 비어 있는 비트를 하나 받는다")
    @Transactional
    void createGivesAFreeBit() {
        long before = orOfDeptLevels();

        assertThat(permissionService.createDept("시험부서", "48058", "부산", "3층")).isTrue();

        var row = jdbc.queryForMap(
                "SELECT p.level, p.permission FROM dept_tbl d "
                        + "JOIN permission_dept_tbl p ON p.dept_tbl_id = d.id "
                        + "WHERE d.name = '시험부서'");
        long level = ((Number) row.get("level")).longValue();
        // 아무도 안 쓰던 자리이고, 비트가 정확히 하나이며, 관리자 비트가 아니다.
        assertThat(before & level).isZero();
        assertThat(Long.bitCount(level)).isEqualTo(1);
        assertThat(level & Permissions.ADMIN_BIT).isZero();
        // 새 부서는 자기 비트만 갖는다.
        assertThat(((Number) row.get("permission")).longValue()).isEqualTo(level);
    }

    @Test
    @DisplayName("같은 이름의 부서는 다시 만들 수 없다")
    @Transactional
    void createRejectsDuplicateName() {
        String existing = permissionService.list(null, null).depts().get(0).name();

        assertThat(permissionService.createDept(existing, null, null, null)).isFalse();
    }

    @Test
    @DisplayName("부서를 지우면 부서와 권한 행이 함께 사라진다")
    @Transactional
    void deleteRemovesBothRows() {
        permissionService.createDept("지울부서", null, null, null);
        int deptId = jdbc.queryForObject(
                "SELECT id FROM dept_tbl WHERE name = '지울부서'", Integer.class);

        assertThat(permissionService.deleteDepts(List.of(deptId))).isTrue();

        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM dept_tbl WHERE id = ?", Integer.class, deptId)).isZero();
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM permission_dept_tbl WHERE dept_tbl_id = ?",
                Integer.class, deptId)).isZero();
    }

    @Test
    @DisplayName("권한을 바꾸면 그 부서 사람의 화면 접근이 실제로 달라진다")
    @Transactional
    void permissionChangeReachesTheScreen() {
        // 권한 판정이 이 값을 읽는지 끝에서 끝까지 확인한다.
        var depts = permissionService.list(null, null).depts();
        PermissionRow self = depts.get(0);
        PermissionRow other = depts.get(1);

        permissionService.updateDept(
                self.classId(), self.name(), null, null, null, List.of(other.classId()));

        long stored = storedDeptPermission(self.classId());
        // 로그인 조회가 읽는 값과 같아야 한다.
        assertThat(Permissions.hasProgramPermission(stored, -1L, other.level(), -1L)).isTrue();
        assertThat(Permissions.hasProgramPermission(self.level(), -1L, other.level(), -1L))
                .isFalse();
    }

    @Test
    @DisplayName("수정 화면은 번호가 없으면 잘못된 접근으로 보낸다")
    void updateFormWithoutIdRedirects() throws Exception {
        mockMvc.perform(get("/admin/permission/dept-update").with(user(TestUsers.admin())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-error"));
        mockMvc.perform(get("/admin/permission/job-update").with(user(TestUsers.admin())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-error"));
    }

    @Test
    @DisplayName("관리자가 아니면 권한 화면을 볼 수 없다")
    void nonAdminIsBlocked() throws Exception {
        mockMvc.perform(get("/admin/permission/job-dept-list")
                        .with(user(TestUsers.noPermission())))
                .andExpect(status().isForbidden());
    }

    private long storedDeptPermission(int deptId) {
        return jdbc.queryForObject(
                "SELECT permission FROM permission_dept_tbl WHERE dept_tbl_id = ?",
                Long.class, deptId);
    }

    private long orOfDeptLevels() {
        long all = 0L;
        for (Long level : jdbc.queryForList(
                "SELECT level FROM permission_dept_tbl", Long.class)) {
            all |= level;
        }
        return all;
    }

    private static long levelOf(List<PermissionRow> rows, int classId) {
        return rows.stream().filter(row -> row.classId() == classId)
                .findFirst().orElseThrow().level();
    }
}
