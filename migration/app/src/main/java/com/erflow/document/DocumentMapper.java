package com.erflow.document;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 문서 매퍼. 지금은 찾기 팝업이 쓰는 것만 있다. */
@Mapper
public interface DocumentMapper {

    /**
     * 사용자가 만든 문서 목록.
     *
     * @param userId 사번
     * @return 문서 목록. 번호 내림차순
     */
    List<DocumentRow> findByUser(@Param("userId") String userId);

    /**
     * 문서에 걸린 결재 건수.
     *
     * @param documentId 문서번호
     * @return 건수
     */
    int countProposals(@Param("documentId") long documentId);
}
