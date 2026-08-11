package com.erflow.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 인증·인가에 쓰는 조회.
 *
 * <p>SQL 은 {@code resources/mapper/auth/AuthMapper.xml} 에 있다.
 */
@Mapper
public interface AuthMapper {

    /**
     * 사번으로 사용자와 권한을 읽는다.
     *
     * @param id 사번
     * @return 사용자. 없으면 {@code null}
     */
    AuthUser findAuthUser(@Param("id") String id);

    /**
     * 요청 경로가 요구하는 권한을 읽는다.
     *
     * <p>레거시는 화면마다 {@code PROGRAM_CODE} 를 박아 두고 그것으로 조회했다.
     * 이관 후에는 {@code screen} 테이블이 그 대응을 담는다.
     *
     * @param route 요청 경로. 쿼리스트링은 뺀다
     * @return 요구 권한. 권한 대상 화면이 아니면 {@code null}
     */
    ScreenAccess findScreenAccess(@Param("route") String route);

    /**
     * 비밀번호를 바꾼다.
     *
     * @param id 사번
     * @param password 새 비밀번호 해시
     * @return 반영된 행 수
     */
    int updatePassword(@Param("id") String id, @Param("password") String password);
}
