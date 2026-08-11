<!-- commentRegisterProc.jsp -->
<%@page import="model.UserBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.CommentBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="controller.ActivityController"%>
<jsp:useBean id="bean" class="model.CommentBean"/>
<jsp:useBean id="commentCon" class="controller.CommentController"/>
<jsp:useBean id="fileCon" class="controller.PostFileController" />
<jsp:useBean id="postCon" class="controller.PostController" />
<%
final String[] keys = { "boardId", "comment", "postId" };
final HashMap<String, String> parameters = new HashMap<>();
boolean isValid = true;
int postId = -1;

for (String key : keys) {
	String value = request.getParameter(key);
	
	if (value == null) {
		isValid = false;
		return;
	}
	parameters.put(key, value);
}
if (isValid) {
	postId = WebHelper.parseInt(request, "postId");
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
UserBean user = WebHelper.getValidUser(session);

//로그인 여부 확인
if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
boolean flag = false;
String message = "댓글 작성을 실패했습니다.";

if (postId != -1){
	CommentBean comment = new CommentBean(
		-1,
		postId,
		-1,
		user.getId(),
		parameters.get("comment"),
		0,
		null
	);
	flag = commentCon.createComment(session, comment);
}
if (flag) {
	message = "댓글을 작성하였습니다.";
}
%>
<script type="text/javascript">
	alert('<%=message%>');
	location.href = 'postView.jsp?boardId=<%=parameters.get("boardId")%>&id=<%=postId%>';
</script>