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
}
