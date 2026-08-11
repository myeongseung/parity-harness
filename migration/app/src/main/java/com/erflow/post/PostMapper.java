package com.erflow.post;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 게시글 매퍼. 레거시 {@code post_view} 를 그대로 쓴다. */
@Mapper
public interface PostMapper {

    /**
     * 게시글 한 페이지.
     *
     * @param boardId 게시판 번호
     * @param search 검색 조건
     * @param start 조회 시작 위치
     * @param count 가져올 건수
     * @return 게시글 목록
     */
    List<PostRow> findPage(
            @Param("boardId") int boardId,
            @Param("search") PostSearch search,
            @Param("start") int start,
            @Param("count") int count);

    /**
     * 게시글 수.
     *
     * @param boardId 게시판 번호
     * @param search 검색 조건
     * @return 건수
     */
    int countBy(@Param("boardId") int boardId, @Param("search") PostSearch search);

    /**
     * 게시글 한 건.
     *
     * @param id 글번호
     * @return 게시글. 없으면 {@code null}
     */
    PostRow findById(@Param("id") int id);

    /**
     * 게시글별 댓글 수.
     *
     * @param postId 글번호
     * @return 댓글 수
     */
    int countComments(@Param("postId") int postId);

    /**
     * 게시글에 붙은 첨부파일의 원본 이름. 목록의 첨부 아이콘 표시에만 쓴다.
     *
     * @param postId 글번호
     * @return 첨부 원본 이름. 없으면 {@code null}
     */
    String findFirstAttachmentName(@Param("postId") int postId);
}
