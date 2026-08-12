package com.erflow.task;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 수·발주 매퍼.
 *
 * <p>SQL 은 {@code resources/mapper/task/TaskMapper.xml} 에 있고, 각 구문에 레거시
 * 출처를 주석으로 달아 뒀다.
 *
 * <p>{@code type} 으로 발주(1)와 수주(0)가 갈린다. 목록·카운트 모두 같은 {@code type}
 * 을 받는다 — 게시판에서 목록과 카운트가 서로 다른 파라미터를 봐서 생긴 결함(D-025)이
 * 여기엔 없다. 레거시가 인덱스를 맞춰 두었다.
 */
@Mapper
public interface TaskMapper {

    /**
     * 목록을 페이지 단위로 조회한다.
     *
     * @param type 0 수주 / 1 발주
     * @param search 검색 조건
     * @param start 조회 시작 위치
     * @param count 가져올 건수
     * @return 수·발주 목록
     */
    List<TaskRow> findPage(
            @Param("type") int type,
            @Param("search") TaskSearch search,
            @Param("start") int start,
            @Param("count") int count);

    /**
     * 검색 조건에 걸리는 전체 건수를 센다.
     *
     * @param type 0 수주 / 1 발주
     * @param search 검색 조건
     * @return 전체 건수
     */
    int countBy(@Param("type") int type, @Param("search") TaskSearch search);
}
