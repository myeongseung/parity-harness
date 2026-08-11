<!-- commentUpdateProc.jsp -->
<%@page import="java.util.HashMap"%>
<%@page import="model.CommentBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="commentCon" class="controller.CommentController"/>
<%
final String[] keys = {
		"boardId", "postId", "id", "comment",
	};
final HashMap<String, String> parameters = new HashMap<>();
boolean isValid = true;

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
for (String key : keys) {
	String value = request.getParameter(key);	
	if (value == null) {
		isValid = false;
		break;
	}
	parameters.put(key, value);
}
if (!isValid) {
	response.sendRedirect("../accessError.jsp");
	return;
}
String userId = user.getId();
String boardId = parameters.get("boardId");
int postId = Integer.parseInt(parameters.get("postId"));
int commentId = Integer.parseInt(parameters.get("id"));
String commentText = parameters.get("comment");

CommentBean comment = commentCon.getComment(commentId);

String message = "";

comment.setId(commentId);
comment.setComment(commentText);
comment.setUserId(userId);

boolean result = commentCon.updateComment(session, comment);

if (result) {
	message = "댓글을 수정했습니다.";
} else {
	message = "댓글을 수정하지 못했습니다.";
}
%>
<script type="text/javascript">
	alert('<%=message%>');
	location.href = 'postView.jsp?boardId=<%=boardId%>&id=<%=postId%>';
</script>