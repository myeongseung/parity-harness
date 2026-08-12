package com.erflow.message;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 쪽지 매퍼.
 *
 * <p>SQL 은 {@code resources/mapper/message/MessageMapper.xml} 에 있고, 각 구문에
 * 레거시 출처를 주석으로 달아 뒀다.
 *
 * <p>{@code className} 은 receiver 또는 sender 다 — 화면에서 그 둘로만 검증된다. SQL 이
 * 이 값으로 {@code <class>_id}/{@code <class>_visible} 컬럼을 고르는데, 문자열로 이어
 * 붙이지 않고 분기로 고정해 주입을 막는다.
 */
@Mapper
public interface MessageMapper {

    /**
     * 쪽지함 한 페이지.
     *
     * @param className receiver 또는 sender
     * @param classId 현재 사용자 사번
     * @param search 검색 조건
     * @param start 조회 시작 위치
     * @param count 가져올 건수
     * @return 쪽지 목록
     */
    List<MessageRow> findPage(
            @Param("className") String className,
            @Param("classId") String classId,
            @Param("search") MessageSearch search,
            @Param("start") int start,
            @Param("count") int count);

    /**
     * 검색 조건에 걸리는 전체 건수.
     *
     * @param className receiver 또는 sender
     * @param classId 현재 사용자 사번
     * @param search 검색 조건
     * @return 전체 건수
     */
    int countBy(
            @Param("className") String className,
            @Param("classId") String classId,
            @Param("search") MessageSearch search);
}
