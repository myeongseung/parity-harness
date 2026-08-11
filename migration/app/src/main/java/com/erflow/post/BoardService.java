package com.erflow.post;

import com.erflow.common.Pagination;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시판 업무.
 *
 * <p>레거시는 게시판 목록 화면 스크립틀릿에서 게시판마다 글 수와 최신글을 다시
 * 질의했다. 질의 횟수를 줄이면 결과가 같아도 순서나 경계가 달라질 수 있어, 호출을
 * 서비스로 옮기기만 하고 질의 구조는 그대로 둔다.
 */
@Service
public class BoardService {

    private final BoardMapper boardMapper;
    private final PostMapper postMapper;

    /**
     * @param boardMapper 게시판 매퍼
     * @param postMapper 게시글 매퍼
     */
    public BoardService(BoardMapper boardMapper, PostMapper postMapper) {
        this.boardMapper = boardMapper;
        this.postMapper = postMapper;
    }

    /**
     * 게시판 목록 한 페이지.
     *
     * <h2>레거시의 페이징이 동작하지 않는다</h2>
     *
     * <p>{@code getBoards} 에 {@code LIMIT} 이 없고, 화면은 {@code boardList.get(i)}
     * 를 <b>0번부터</b> 15건 자른다. {@code start} 를 계산해 두고 쓰지 않는다. 즉
     * 2페이지를 눌러도 1페이지와 같은 목록이 나온다. 게시판이 4개뿐이라 페이지
     * 번호 자체가 하나만 그려져 드러나지 않는다.
     *
     * <p>고치지 않는다. 고치면 게시판이 15개를 넘는 순간 레거시와 다른 화면이 된다.
     * {@code migration/design/00-decisions.md} 의 D-025 참조.
     *
     * @param keyword 검색어
     * @param requestedPage 요청된 페이지
     * @return 목록과 페이징
     */
    @Transactional(readOnly = true)
    public BoardPage list(String keyword, int requestedPage) {
        int total = boardMapper.countBy(keyword);
        Pagination pagination = Pagination.of(total, requestedPage);

        List<Board> boards = boardMapper.findAll(keyword);
        List<BoardRow> rows = new ArrayList<>();
        // 레거시 그대로 0번부터 자른다. pagination.start() 를 쓰지 않는 것이 의도다.
        for (int i = 0; i < pagination.numPerPage() && i < boards.size(); ++i) {
            Board board = boards.get(i);
            int postCount = postMapper.countBy(board.id(), PostSearch.none());
            List<PostRow> recent =
                    postMapper.findPage(board.id(), PostSearch.none(), 0, 1);
            PostRow head = recent.isEmpty() ? null : recent.get(0);
            rows.add(new BoardRow(
                    board.id(),
                    board.subject(),
                    postCount,
                    head == null ? null : head.id(),
                    head == null ? null : head.subject(),
                    head == null ? null : head.createdAt()));
        }
        return new BoardPage(rows, pagination);
    }

    /**
     * 게시판 한 건을 읽는다.
     *
     * @param id 게시판 번호
     * @return 게시판. 없으면 {@code null}
     */
    @Transactional(readOnly = true)
    public Board get(int id) {
        return boardMapper.findById(id);
    }

    /**
     * 게시판 목록 한 페이지.
     *
     * @param rows 게시판 줄
     * @param pagination 페이징
     */
    public record BoardPage(List<BoardRow> rows, Pagination pagination) {
    }
}
