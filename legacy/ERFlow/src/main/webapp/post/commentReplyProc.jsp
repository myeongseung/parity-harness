<!-- commentReplyProc.jsp -->
<%@page import="model.UserBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.CommentBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="controller.ActivityController"%>
<jsp:useBean id="bean" class="model.CommentBean" />
<jsp:useBean id="postCon" class="controller.PostController" />
<jsp:useBean id="fileCon" class="controller.PostFileController" />
<jsp:useBean id="commentCon" class="controller.CommentController"/>
<%
final String[] keys = {
		"boardId", "postId", "comment", "refId"
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
int refId = Integer.parseInt(parameters.get("refId"));
String commentText = parameters.get("comment");

CommentBean comment = new CommentBean();
comment.setPostId(postId);
comment.setRefId(refId);
comment.setUserId(userId);
comment.setComment(commentText);

String message = "댓글 등록을 실패하였습니다.";
boolean result = commentCon.createReply(session, comment);

if (result) {
	message = "댓글을 등록하였습니다.";
}
%>
<script type="text/javascript">
	alert('<%=message%>');
	location.href = 'postView.jsp?boardId=<%=boardId%>&id=<%=postId%>';
</script>