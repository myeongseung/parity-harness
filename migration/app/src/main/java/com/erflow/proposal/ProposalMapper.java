package com.erflow.proposal;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 결재 매퍼.
 *
 * <p>SQL 은 {@code resources/mapper/proposal/ProposalMapper.xml} 에 있고, 각 구문에
 * 레거시 출처를 주석으로 달아 뒀다.
 */
@Mapper
public interface ProposalMapper {

    /**
     * 결재 리스트 한 페이지.
     *
     * <p>내가 결재선에 든 진행중 결재({@code result = 3})와, 내가 기안한 결재 중 선택한
     * 상태({@code result}) 를 함께 보여준다 — 레거시 인박스 로직 그대로다.
     *
     * @param userId 현재 사용자 사번
     * @param result 상태 필터(3 결재진행중 / 1 승인 / 2 반려)
     * @param start 조회 시작 위치
     * @param count 가져올 건수
     * @return 결재 목록
     */
    List<ProposalRow> findPage(
            @Param("userId") String userId,
            @Param("result") int result,
            @Param("start") int start,
            @Param("count") int count);

    /**
     * 조건에 걸리는 결재 수.
     *
     * @param userId 현재 사용자 사번
     * @param result 상태 필터
     * @return 전체 건수
     */
    int countBy(@Param("userId") String userId, @Param("result") int result);
}
