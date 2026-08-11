<!-- postDeleteProc.jsp -->
<%@page import="helper.WebHelper"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="commentCon" class="controller.CommentController"/>
<jsp:useBean id="fileCon" class="controller.PostFileController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="postCon" class="controller.PostController"/>
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String[] postIds = request.getParameterValues("postId");
String paramBoardId = request.getParameter("boardId");
String message = "게시글을 삭제하지 못했습니다.";
int boardId = -1;

if (paramBoardId != null && !paramBoardId.trim().equals("")) {
	boardId = WebHelper.parseInt(request, "boardId");
} else {
	response.sendRedirect("../../accessError.jsp");
	return;
}
if (postIds != null) {
	boolean result = true;
	
	for (String postId : postIds) {
		int id = Integer.parseInt(postId);
		
		commentCon.deleteAllComments(session, boardId, id);
		fileCon.deleteFiles(id);
		result &= postCon.deletePost(session, boardId, id);
	}
	if (result) {
		message = "게시글을 삭제하였습니다.";
	}
} else {
	message = "삭제할 게시글을 선택해주세요.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'adminBoardList.jsp?boardId=<%=boardId%>';
	</script>
</body>