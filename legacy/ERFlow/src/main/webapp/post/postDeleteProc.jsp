<!-- postDeleteProc.jsp -->
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="postCon" class="controller.PostController" />
<jsp:useBean id="fileCon" class="controller.PostFileController" />
<jsp:useBean id="commentCon" class="controller.CommentController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
String paramBoardId = request.getParameter("boardId");
String paramPostId = request.getParameter("postId");
int boardId = -1;
int postId = -1;

// 프로그램을 실행하기 위해 필요한 매개변수를 검사한다.
if (paramBoardId != null && !paramBoardId.trim().equals("") &&
	paramPostId != null && !paramPostId.trim().equals("")) {
	// 정상적으로 실행되면 Integer로 변경
	boardId = WebHelper.parseInt(request, "boardId");
	postId = WebHelper.parseInt(request, "postId");
} else {
	// 실패했을 경우, 잘못된 접근.
	response.sendRedirect("../accessError.jsp");
	return;
}
boolean flag = false;
String message = "게시물을 삭제하지 못했습니다.";

if (!WebHelper.isLogin(session) || !permissionCon.hasBoardWritePermission(session, boardId)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

if (boardId != -1 && postId != -1) {
	// 먼저 게시글에 작성된 모든 댓글을 제거한다.
	commentCon.deleteAllComments(session, boardId, postId);
	// 게시글에 연결된 첨부파일을 삭제한다.
	fileCon.deleteFiles(postId);
	// 그리고 게시글을 삭제한다.
	flag = postCon.deletePost(session, boardId, postId);
}
if (flag) {
	message = "게시물을 삭제하였습니다.";
}
%>
<script type="text/javascript">
	alert('<%=message%>');
	location.href = 'postList.jsp?boardId=<%=boardId%>';
</script>