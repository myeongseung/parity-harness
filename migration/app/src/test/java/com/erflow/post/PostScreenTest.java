package com.erflow.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erflow.auth.TestUsers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 게시판 화면이 실제 데이터로 그려지는지 확인한다.
 *
 * <p>연결된 DB 를 상대로 한다. {@code application-local.yml} 이 없는 CI 에서는
 * 스스로 건너뛴다.
 */
@SpringBootTest(properties = "server.port=0")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class PostScreenTest {

    private static final Path OUT = Path.of("build", "rendered");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostFileService postFileService;

    @BeforeAll
    static void requireLocalConfig() {
        assumeTrue(
                new ClassPathResource("application-local.yml").exists(),
                "application-local.yml 이 없어 건너뛴다");
    }

    private String render(String path, String fileName) throws Exception {
        String html = mockMvc.perform(get(path).with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Files.createDirectories(OUT);
        Files.writeString(OUT.resolve(fileName), html);
        return html;
    }

    @Test
    @DisplayName("게시판 목록이 글 수와 최신글을 함께 그린다")
    void boardListShowsCountAndRecent() throws Exception {
        List<BoardRow> rows = boardService.list(null, 1).rows();
        assumeTrue(!rows.isEmpty(), "게시판 데이터가 없어 건너뛴다");

        String html = render("/post/board-list", "post-board-list.html");

        for (BoardRow row : rows) {
            assertThat(html).contains(row.subject());
        }
        assertThat(html).contains("게시글 수").contains("최신글 제목").contains("선택");
    }

    @Test
    @DisplayName("게시글 목록이 레거시 정렬 그대로 나온다")
    void postListKeepsLegacyOrder() throws Exception {
        List<BoardRow> boards = boardService.list(null, 1).rows();
        BoardRow board = boards.stream().filter(b -> b.postCount() > 0).findFirst()
                .orElse(null);
        assumeTrue(board != null, "글이 있는 게시판이 없어 건너뛴다");

        String html = render("/post/list?boardId=" + board.id(), "post-list.html");

        List<PostListRow> rows = postService.list(board.id(), PostSearch.none(), 1).rows();
        int previous = Integer.MAX_VALUE;
        for (PostListRow row : rows) {
            assertThat(html).contains(row.post().subject());
            // 레거시 order by depth desc — depth 는 스레드 그룹 번호다.
            assertThat(row.post().depth()).isLessThanOrEqualTo(previous);
            previous = row.post().depth();
        }
    }

    @Test
    @DisplayName("화면에 찍히는 글번호는 실제 ID 가 아니라 역순 일련번호다")
    void displayNumberIsNotTheId() {
        List<BoardRow> boards = boardService.list(null, 1).rows();
        BoardRow board = boards.stream().filter(b -> b.postCount() > 1).findFirst()
                .orElse(null);
        assumeTrue(board != null, "글이 둘 이상인 게시판이 없어 건너뛴다");

        List<PostListRow> rows = postService.list(board.id(), PostSearch.none(), 1).rows();

        assertThat(rows.get(0).displayNumber()).isEqualTo(board.postCount());
        assertThat(rows.get(1).displayNumber()).isEqualTo(board.postCount() - 1);
    }

    @Test
    @DisplayName("읽기 권한이 없으면 게시글 목록을 볼 수 없다")
    void listDeniesWithoutBoardPermission() throws Exception {
        List<BoardRow> boards = boardService.list(null, 1).rows();
        assumeTrue(!boards.isEmpty(), "게시판 데이터가 없어 건너뛴다");

        mockMvc.perform(get("/post/list?boardId=" + boards.get(0).id())
                        .with(user(TestUsers.noPermission())))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("게시판 번호가 없으면 접근 오류로 보낸다")
    void listWithoutBoardIdGoesToAccessError() throws Exception {
        mockMvc.perform(get("/post/list").with(user(TestUsers.admin())))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("게시글 보기가 댓글과 첨부를 함께 그린다")
    void viewShowsCommentsAndAttachments() throws Exception {
        PostListRow target = anyPost();
        assumeTrue(target != null, "글이 있는 게시판이 없어 건너뛴다");

        int boardId = target.post().boardId();
        int postId = target.post().id();
        String html = render("/post/view?boardId=" + boardId + "&id=" + postId,
                "post-view.html");

        assertThat(html).contains(target.post().subject());
        assertThat(html).contains("작성자:").contains("작성일:")
                .contains("댓글수:").contains("조회수:");
        for (CommentRow comment : commentService.list(postId)) {
            assertThat(html).contains(comment.comment());
        }
        for (PostAttachment file : postFileService.list(postId)) {
            assertThat(html).contains(file.displayName());
        }
    }

    @Test
    @DisplayName("댓글이 없으면 없다고 말한다")
    void viewSaysWhenThereAreNoComments() throws Exception {
        PostListRow empty = postsOf(anyBoardWithPosts()).stream()
                .filter(row -> row.commentCount() == 0).findFirst().orElse(null);
        assumeTrue(empty != null, "댓글 없는 글이 없어 건너뛴다");

        String html = render("/post/view?boardId=" + empty.post().boardId()
                + "&id=" + empty.post().id(), "post-view-no-comment.html");

        assertThat(html).contains("등록된 댓글이 없습니다.");
    }

    @Test
    @DisplayName("쓰기 권한이 있어야 글쓰기 화면이 열린다")
    void registerNeedsWritePermission() throws Exception {
        int boardId = anyBoardWithPosts();

        mockMvc.perform(get("/post/register?boardId=" + boardId)
                        .with(user(TestUsers.noPermission())))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("글쓰기 화면은 게시판 이름을 자유게시판으로 고정해 그린다")
    void registerHardcodesBoardName() throws Exception {
        // 레거시 결함을 그대로 옮긴 것이다. D-032 참조.
        String html = render("/post/register?boardId=" + anyBoardWithPosts(),
                "post-register.html");

        assertThat(html).contains("value=\"자유게시판\"");
    }

    @Test
    @DisplayName("답변 화면은 실제 게시판 이름을 그린다")
    void replyShowsRealBoardName() throws Exception {
        int boardId = anyBoardWithPosts();
        Board board = boardService.get(boardId);

        String html = render("/post/reply?boardId=" + boardId + "&postId="
                + anyPost().post().id(), "post-reply.html");

        assertThat(html).contains(board.subject());
    }

    @Test
    @DisplayName("글수정 화면이 기존 제목과 본문을 채워 그린다")
    void updatePrefillsExistingValues() throws Exception {
        PostListRow target = anyPost();
        assumeTrue(target != null, "글이 있는 게시판이 없어 건너뛴다");

        String html = render("/post/update?boardId=" + target.post().boardId()
                + "&id=" + target.post().id(), "post-update.html");

        assertThat(html).contains(target.post().subject());
    }

    private int anyBoardWithPosts() {
        return boardService.list(null, 1).rows().stream()
                .filter(b -> b.postCount() > 0)
                .map(BoardRow::id)
                .findFirst()
                .orElse(-1);
    }

    private List<PostListRow> postsOf(int boardId) {
        return boardId == -1 ? List.of()
                : postService.list(boardId, PostSearch.none(), 1).rows();
    }

    private PostListRow anyPost() {
        List<PostListRow> rows = postsOf(anyBoardWithPosts());
        return rows.isEmpty() ? null : rows.get(0);
    }
}
