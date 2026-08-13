package com.erflow.proposal;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 결재라인 매퍼.
 *
 * <p>SQL 은 {@code resources/mapper/proposal/ProposalRouteMapper.xml} 에 있고, 각
 * 구문에 레거시 출처를 주석으로 달아 뒀다.
 *
 * <p>결재 도메인은 아직 이관 전이다. 찾기 팝업이 쓰는 조회 하나만 있다.
 */
@Mapper
public interface ProposalRouteMapper {

    /**
     * 내가 만든 결재라인.
     *
     * @param userId 사번
     * @return 결재라인 목록. 번호 순이다
     */
    List<ProposalRouteRow> findByUser(@Param("userId") String userId);

    /**
     * 내 결재라인을 검색·페이지 단위로 조회한다.
     *
     * @param userId 사번
     * @param search 검색 조건
     * @param start 조회 시작 위치
     * @param count 가져올 건수
     * @return 결재라인 목록(가공 전)
     */
    List<ProposalRouteListRow> findPage(
            @Param("userId") String userId,
            @Param("search") ProposalRouteSearch search,
            @Param("start") int start,
            @Param("count") int count);

    /**
     * 검색 조건에 걸리는 내 결재라인 수.
     *
     * @param userId 사번
     * @param search 검색 조건
     * @return 전체 건수
     */
    int countBy(@Param("userId") String userId, @Param("search") ProposalRouteSearch search);
}
