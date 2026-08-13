package com.erflow.bound;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 입·출고 매퍼.
 *
 * <p>SQL 은 {@code resources/mapper/bound/BoundMapper.xml} 에 있고, 각 구문에 레거시
 * 출처를 주석으로 달아 뒀다.
 *
 * <p>레거시는 입고(type=0)와 출고(type=1)를 {@code type} 을 SQL 에 상수로 박은 별도
 * 메서드로 나눠 뒀다({@code getInbounds}/{@code getOutbounds}). 여기서는 한 매퍼에
 * {@code type} 파라미터로 합쳤다 — 같은 결과다.
 */
@Mapper
public interface BoundMapper {

    /**
     * 목록을 페이지 단위로 조회한다.
     *
     * @param type 0 입고 / 1 출고
     * @param search 검색 조건
     * @param start 조회 시작 위치
     * @param count 가져올 건수
     * @return 입·출고 목록
     */
    List<BoundRow> findPage(
            @Param("type") int type,
            @Param("search") BoundSearch search,
            @Param("start") int start,
            @Param("count") int count);

    /**
     * 검색 조건에 걸리는 전체 건수.
     *
     * @param type 0 입고 / 1 출고
     * @param search 검색 조건
     * @return 전체 건수
     */
    int countBy(@Param("type") int type, @Param("search") BoundSearch search);

    /**
     * 입·출고 한 건을 넣는다.
     *
     * @param bound 등록할 입·출고
     * @return 반영된 행 수
     */
    int insert(@Param("bound") Bound bound);
}
