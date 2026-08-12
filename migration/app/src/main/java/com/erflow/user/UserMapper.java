package com.erflow.user;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 사용자 찾기 팝업이 쓰는 조회.
 *
 * <p>SQL 은 {@code resources/mapper/user/UserMapper.xml} 에 있고, 각 구문에 레거시
 * 출처를 주석으로 달아 뒀다.
 *
 * <p>부서·직급 목록은 사용자 테이블이 아니라 {@code dept_tbl} / {@code job_tbl} 에서
 * 온다. 지금은 이 팝업만 쓰므로 여기 둔다 — 부서·직급 관리 화면을 이관하면 그때
 * 그쪽으로 옮긴다.
 */
@Mapper
public interface UserMapper {

    /**
     * 콤보에 채울 부서명 목록.
     *
     * @return 부서명. DB 가 주는 순서 그대로다
     */
    List<String> findDepartmentNames();

    /**
     * 콤보에 채울 직급명 목록.
     *
     * @return 직급명. DB 가 주는 순서 그대로다
     */
    List<String> findJobNames();

    /**
     * 조건에 걸리는 사용자.
     *
     * @param search 검색 조건
     * @return 사용자 목록. {@code admin} 은 빠진다
     */
    List<UserRow> findUserViews(@Param("search") UserSearch search);
}
