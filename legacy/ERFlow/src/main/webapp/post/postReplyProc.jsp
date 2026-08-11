<!-- postReplyProc.jsp -->
<%@page import="model.view.ViewPostBean"%>
<%@page import="model.PostBean"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="postCon" class="controller.PostController" />
<jsp:useBean id="fileCon" class="controller.PostFileController" />
<%
final String[] keys = {
	"boardId", "id", "subject", "content"
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
int boardId = Integer.parseInt(parameters.get("boardId"));
int postId = Integer.parseInt(parameters.get("id"));
String content = parameters.get("content");
String subject = parameters.get("subject");

ViewPostBean parentPost = postCon.getPostView(session, boardId, postId);

// 답변글을 등록하기위해 bean에 값 설정
PostBean bean = new PostBean();
bean.setBoardId(boardId);
bean.setUserId(userId);
bean.setSubject(subject);
bean.setContent(content);
bean.setRefId(postId);
bean.setPos(parentPost.getPos());
bean.setDepth(parentPost.getDepth());

String message = "답변 등록을 실패하였습니다.";
boolean result = postCon.replyPost(bean);

if (result) {
	message = "답변을 등록하였습니다.";
}
%>
<script type="text/javascript">
	alert('<%=message%>');
	location.href = 'postList.jsp?boardId=<%=boardId%>';
</script>