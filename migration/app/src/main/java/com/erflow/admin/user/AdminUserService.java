package com.erflow.admin.user;

import com.erflow.common.Pagination;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사원 관리 업무.
 *
 * <p>레거시는 페이징 계산을 {@code admin/user/userList.jsp} 스크립틀릿에, DB 접근을
 * {@code UserServiceImpl} 에 두었다. 화면에서 로직을 걷어내 여기로 모은다.
 */
@Service
public class AdminUserService {

    private final AdminUserMapper adminUserMapper;

    /**
     * @param adminUserMapper 사원 관리 매퍼
     */
    public AdminUserService(AdminUserMapper adminUserMapper) {
        this.adminUserMapper = adminUserMapper;
    }

    /**
     * 사원 리스트 한 페이지.
     *
     * @param search 검색 조건
     * @param requestedPage 요청된 페이지
     * @return 목록과 페이징
     */
    @Transactional(readOnly = true)
    public UserPage list(AdminUserSearch search, int requestedPage) {
        int total = adminUserMapper.countBy(search);
        Pagination pagination = Pagination.of(total, requestedPage);
        List<AdminUserRow> rows = adminUserMapper.findPage(
                search, pagination.start(), pagination.numPerPage());
        return new UserPage(rows, pagination);
    }

    /**
     * 한 사원의 주소.
     *
     * @param id 사번
     * @return 주소. 없으면 {@code null}
     */
    @Transactional(readOnly = true)
    public AdminUserAddress address(String id) {
        return adminUserMapper.findAddress(id);
    }

    /**
     * 사원 리스트 한 페이지.
     *
     * @param rows 이 페이지의 사원 목록
     * @param pagination 페이징 정보
     */
    public record UserPage(List<AdminUserRow> rows, Pagination pagination) {
    }
}
