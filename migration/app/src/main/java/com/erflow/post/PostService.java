package com.erflow.post;

import com.erflow.common.Pagination;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 업무.
 *
 * <p>레거시는 페이징 계산과 줄마다의 부가 질의를 {@code postList.jsp} 스크립틀릿에서
 * 했다. 계산을 밖으로 꺼내 시험할 수 있게 한다.
 */
@Service
public class PostService {

    private final PostMapper postMapper;

    /**
     * @param postMapper 게시글 매퍼
     */
    public PostService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    /**
     * 게시글 목록 한 페이지.
     *
     * <p>화면에 찍히는 글번호는 실제 ID 가 아니라
     * {@code totalRecord - (start + i)} 다 — 레거시 그대로다. 전체 건수에서 위치를
     * 빼는 방식이라 검색 결과에서는 번호가 건너뛴다.
     *
     * @param boardId 게시판 번호
     * @param search 검색 조건
     * @param requestedPage 요청된 페이지
     * @return 목록과 페이징
     */
    @Transactional(readOnly = true)
    public PostPage list(int boardId, PostSearch search, int requestedPage) {
        int total = postMapper.countBy(boardId, search);
        Pagination pagination = Pagination.of(total, requestedPage);
        List<PostRow> posts = postMapper.findPage(
                boardId, search, pagination.start(), pagination.numPerPage());

        List<PostListRow> rows = new ArrayList<>();
        for (int i = 0; i < posts.size(); ++i) {
            PostRow post = posts.get(i);
            rows.add(new PostListRow(
                    post,
                    total - (pagination.start() + i),
                    postMapper.countComments(post.id()),
                    postMapper.findFirstAttachmentName(post.id())));
        }
        return new PostPage(rows, pagination);
    }

    /**
     * 게시글 한 건을 읽는다.
     *
     * @param id 글번호
     * @return 게시글. 없으면 {@code null}
     */
    @Transactional(readOnly = true)
    public PostRow get(int id) {
        return postMapper.findById(id);
    }

    /**
     * 게시글 목록 한 페이지.
     *
     * @param rows 게시글 줄
     * @param pagination 페이징
     */
    public record PostPage(List<PostListRow> rows, Pagination pagination) {
    }
}
