package com.erflow.admin.permission;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 권한 관리 업무 — 직급·부서.
 *
 * <p>레거시는 이 계산을 {@code *Proc.jsp} 스크립틀릿에서 했다. 체크된 번호를 받아 그
 * 번호의 비트를 찾아 {@code |} 로 합치는 일이다. 화면에서 걷어내 여기로 모은다.
 *
 * <p><b>비트 계산은 전부 여기(Java)에서 한다.</b> 매퍼에는 계산이 끝난 값 하나만 넘긴다 —
 * MariaDB 의 비트 연산은 부호가 없어 관리자 비트가 걸리면 뒤집힌다.
 */
@Service
public class AdminPermissionService {

    private final AdminPermissionMapper mapper;

    /**
     * @param mapper 권한 관리 매퍼
     */
    public AdminPermissionService(AdminPermissionMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 직급·부서 목록 화면 한 벌.
     *
     * @param jobKeyword 직급명 검색어
     * @param deptKeyword 부서명 검색어
     * @return 직급 목록과 부서 목록
     */
    @Transactional(readOnly = true)
    public JobDeptList list(String jobKeyword, String deptKeyword) {
        return new JobDeptList(
                mapper.findJobPermissions(jobKeyword), mapper.findDeptPermissions(deptKeyword));
    }

    /**
     * 부서 수정 화면 한 벌.
     *
     * <p>체크박스는 <b>자기 자신을 뺀</b> 나머지 부서다. 이미 가진 비트에 체크가 켜진다.
     *
     * @param deptId 부서 번호
     * @return 화면 한 벌. 부서가 없으면 {@code null}
     */
    @Transactional(readOnly = true)
    public DeptForm deptForm(int deptId) {
        DeptRow dept = mapper.findDept(deptId);
        if (dept == null) {
            return null;
        }
        return new DeptForm(dept, choices(mapper.findDeptPermissions(null), deptId));
    }

    /**
     * 직급 수정 화면 한 벌.
     *
     * @param jobId 직급 번호
     * @return 화면 한 벌. 직급이 없으면 {@code null}
     */
    @Transactional(readOnly = true)
    public JobForm jobForm(int jobId) {
        String name = mapper.findJobName(jobId);
        if (name == null) {
            return null;
        }
        return new JobForm(jobId, name, choices(mapper.findJobPermissions(null), jobId));
    }

    /**
     * 부서를 만든다.
     *
     * <p>부서를 넣고 <b>빈 비트 하나를 찾아</b> 권한 행을 만든다. 레거시는 이 둘을 따로
     * 커밋해서, 사이에서 끊기면 권한 행 없는 부서가 남았다. 한 트랜잭션으로 묶는다(D-061).
     *
     * @param name 부서명
     * @param postalCode 우편번호
     * @param address1 도로명 주소
     * @param address2 상세 주소
     * @return 만들었으면 {@code true}. 이름이 비었거나 이미 있으면 {@code false}
     */
    @Transactional
    public boolean createDept(String name, String postalCode, String address1, String address2) {
        if (name == null || name.trim().isEmpty() || hasDept(name)) {
            return false;
        }
        mapper.insertDept(new DeptRow(0, name, null, postalCode, address1, address2));
        int deptId = mapper.lastInsertId();
        return mapper.insertDeptPermission(deptId, Levels.next(mapper.findDeptLevels())) == 1;
    }

    /**
     * 직급을 만든다.
     *
     * @param name 직급명
     * @return 만들었으면 {@code true}. 이미 있으면 {@code false}
     */
    @Transactional
    public boolean createJob(String name) {
        if (name == null || hasJob(name)) {
            return false;
        }
        mapper.insertJob(name);
        int jobId = mapper.lastInsertId();
        return mapper.insertJobPermission(jobId, Levels.next(mapper.findJobLevels())) == 1;
    }

    /**
     * 부서 이름·주소와 권한을 고친다.
     *
     * <p>권한은 <b>자기 비트 + 체크된 부서들의 비트</b> 다. 자기 비트는 화면에 체크박스가
     * 없지만 언제나 켜진다.
     *
     * <p>이름이 이미 있으면 아무것도 하지 않는다 — 자기 이름을 그대로 둔 경우는 예외다.
     *
     * @param deptId 부서 번호
     * @param name 부서명
     * @param postalCode 우편번호
     * @param address1 도로명 주소
     * @param address2 상세 주소
     * @param checked 체크된 부서 번호들
     * @return 고쳤으면 {@code true}
     */
    @Transactional
    public boolean updateDept(
            int deptId, String name, String postalCode, String address1, String address2,
            List<Integer> checked) {

        DeptRow previous = mapper.findDept(deptId);
        if (previous == null || (hasDept(name) && !previous.name().equals(name))) {
            return false;
        }
        List<PermissionRow> all = mapper.findDeptPermissions(null);
        // 부서장은 화면에 없다. 읽은 값을 그대로 되돌려 놓지 않으면 지워진다.
        boolean updated = mapper.updateDept(new DeptRow(
                deptId, name, previous.managerId(), postalCode, address1, address2)) == 1;
        return updated
                && mapper.updateDeptPermission(deptId, permissionOf(all, deptId, checked)) == 1;
    }

    /**
     * 직급 이름과 권한을 고친다.
     *
     * @param jobId 직급 번호
     * @param name 직급명
     * @param checked 체크된 직급 번호들
     * @return 고쳤으면 {@code true}
     */
    @Transactional
    public boolean updateJob(int jobId, String name, List<Integer> checked) {
        String previous = mapper.findJobName(jobId);
        if (previous == null || (hasJob(name) && !previous.equals(name))) {
            return false;
        }
        List<PermissionRow> all = mapper.findJobPermissions(null);
        boolean updated = mapper.updateJob(jobId, name) == 1;
        return updated
                && mapper.updateJobPermission(jobId, permissionOf(all, jobId, checked)) == 1;
    }

    /**
     * 부서들을 지운다.
     *
     * <p>권한 행을 먼저 지우고 부서를 지운다. <b>다른 부서·프로그램에 남은 그 비트는
     * 걷어내지 않는다</b> — 레거시가 그렇다(D-062).
     *
     * @param deptIds 지울 부서 번호들
     * @return 전부 지웠으면 {@code true}
     */
    @Transactional
    public boolean deleteDepts(List<Integer> deptIds) {
        boolean result = deptIds != null && !deptIds.isEmpty();
        for (int deptId : deptIds == null ? List.<Integer>of() : deptIds) {
            mapper.deleteDeptPermission(deptId);
            result &= mapper.deleteDept(deptId) == 1;
        }
        return result;
    }

    /**
     * 직급들을 지운다.
     *
     * @param jobIds 지울 직급 번호들
     * @return 전부 지웠으면 {@code true}
     */
    @Transactional
    public boolean deleteJobs(List<Integer> jobIds) {
        boolean result = jobIds != null && !jobIds.isEmpty();
        for (int jobId : jobIds == null ? List.<Integer>of() : jobIds) {
            mapper.deleteJobPermission(jobId);
            result &= mapper.deleteJob(jobId) == 1;
        }
        return result;
    }

    /**
     * 체크된 번호들을 비트마스크로 만든다.
     *
     * <p>시작값은 <b>자기 비트</b>다. 그래서 체크를 다 지워도 자기 자신은 남는다.
     */
    private static long permissionOf(List<PermissionRow> all, int selfId, List<Integer> checked) {
        List<Long> levels = new ArrayList<>();
        long self = 0L;
        for (PermissionRow row : all) {
            if (row.classId() == selfId) {
                self = row.level();
            } else if (checked != null && checked.contains(row.classId())) {
                levels.add(row.level());
            }
        }
        return Levels.combine(self, levels);
    }

    /** 자기 자신을 뺀 나머지를 체크박스로 만든다. 이미 가진 비트는 켜 둔다. */
    private static List<PermissionChoice> choices(List<PermissionRow> all, int selfId) {
        long permission = 0L;
        for (PermissionRow row : all) {
            if (row.classId() == selfId) {
                permission = row.permission();
                break;
            }
        }
        List<PermissionChoice> choices = new ArrayList<>();
        for (PermissionRow row : all) {
            if (row.classId() != selfId) {
                choices.add(new PermissionChoice(
                        row.classId(), row.name(), Levels.has(permission, row.level())));
            }
        }
        return choices;
    }

    /**
     * 같은 이름이 있는가.
     *
     * <p>레거시는 {@code count == 1} 로 본다. 같은 이름이 둘이면 «없다» 가 되어 셋째를
     * 만들 수 있다. 그대로 옮긴다.
     */
    private boolean hasDept(String name) {
        return mapper.countDeptByName(name) == 1;
    }

    private boolean hasJob(String name) {
        return mapper.countJobByName(name) == 1;
    }

    /**
     * 직급·부서 목록 화면 한 벌.
     *
     * @param jobs 직급 목록
     * @param depts 부서 목록
     */
    public record JobDeptList(List<PermissionRow> jobs, List<PermissionRow> depts) {
    }

    /**
     * 부서 수정 화면 한 벌.
     *
     * @param dept 고칠 부서
     * @param choices 다른 부서 체크박스
     */
    public record DeptForm(DeptRow dept, List<PermissionChoice> choices) {
    }

    /**
     * 직급 수정 화면 한 벌.
     *
     * @param id 직급 번호
     * @param name 직급명
     * @param choices 다른 직급 체크박스
     */
    public record JobForm(int id, String name, List<PermissionChoice> choices) {
    }
}
