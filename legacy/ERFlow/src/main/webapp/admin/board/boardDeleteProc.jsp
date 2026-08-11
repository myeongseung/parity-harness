<!-- boardDeleteProc.jsp -->
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="model.PostBean"%>
<%@page import="java.util.Vector"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="boardCon" class="controller.BoardController"/>
<jsp:useBean id="commentCon" class="controller.CommentController"/>
<jsp:useBean id="fileCon" class="controller.PostFileController"/>
<jsp:useBean id="postCon" class="controller.PostController"/>
<%
if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String[] boardIds = request.getParameterValues("boardId");
String message = "게시판을 삭제하지 못했습니다.";

if (boardIds != null) {
	boolean result = true;
	
	for (String boardId : boardIds) {
		int id = Integer.parseInt(boardId);
		Vector<PostBean> posts = postCon.getPosts(id);
		
		for (PostBean post : posts) {
			int postId = post.getId();
			
			commentCon.deleteAllComments(session, id, postId);
			fileCon.deleteFiles(postId);
			result &= postCon.deletePost(session, id, postId);
		}
		result &= boardCon.deleteBoard(session, id);
	}
	if (result) {
		message = "게시판을 삭제하였습니다.";
	}
} else {
	message = "삭제할 게시판을 선택해주세요.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'adminBoardList.jsp';
	</script>
</body>