package com.erflow.admin.user;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 사원 관리 매퍼.
 *
 * <p>SQL 은 {@code resources/mapper/admin/AdminUserMapper.xml} 에 있고, 각 구문에 레거시
 * 출처를 주석으로 달아 뒀다.
 *
 * <p>사용자 찾기 팝업({@code com.erflow.user.UserMapper})과 같은 {@code user_view} 를
 * 읽지만 보는 칸이 다르다. 팝업은 부서·직급·이름·사번만, 여기는 주민등록번호까지 본다.
 */
@Mapper
public interface AdminUserMapper {

    /**
     * 사원 리스트 한 페이지.
     *
     * @param search 검색 조건
     * @param start 조회 시작 위치
     * @param count 가져올 건수
     * @return 사원 목록. 부서·직급 이름순이며 {@code admin} 은 빠진다
     */
    List<AdminUserRow> findPage(
            @Param("search") AdminUserSearch search,
            @Param("start") int start,
            @Param("count") int count);

    /**
     * 조건에 걸리는 사원 수.
     *
     * @param search 검색 조건
     * @return 전체 건수
     */
    int countBy(@Param("search") AdminUserSearch search);

    /**
     * 한 사원의 주소.
     *
     * @param id 사번
     * @return 주소. 없으면 {@code null}
     */
    AdminUserAddress findAddress(@Param("id") String id);
}
