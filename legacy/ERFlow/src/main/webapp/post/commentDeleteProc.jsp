<!-- commentDeleteProc.jsp -->
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" scope="page" />
<%
final String[] keys = { "boardId", "postId", "id" };
final HashMap<String, String> parameters = new HashMap<>();

boolean isValid = true;
int id = -1;

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
String boardId = parameters.get("boardId");
String postId = parameters.get("postId");

if (parameters.containsKey("id")) {
	id = Integer.parseInt(parameters.get("id"));
}
boolean flag = false;
String msg = "댓글을 삭제하지 못했습니다.";
if (id != -1) {
	flag = activityCon.deleteComment(session, id);
}
if (flag) {
	msg = "댓글을 삭제했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=msg%>');
		location.href = 'postView.jsp?boardId=<%=boardId%>&id=<%=postId%>';
	</script>
</body>