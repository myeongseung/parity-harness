<!-- registerProc.jsp -->
<%@page import="java.util.Vector"%>
<%@page import="java.util.Collections"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.MessageBean"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="messageCon" class="controller.MessageController" />
<%
final String[] keys = {
		"receiverId", "content"
	};
final HashMap<String, String> parameters = new HashMap<>();
boolean isValid = true;

UserBean user = WebHelper.getValidUser(session);

//로그인 여부 확인
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
String senderId = user.getId();
String receiverId = parameters.get("receiverId");
String content = parameters.get("content");

String[] receiversArr = receiverId.split(";");
Vector<String> receivers = new Vector<>();

Collections.addAll(receivers, receiversArr);

MessageBean bean = new MessageBean();
bean.setSenderId(senderId);
bean.setContent(content);

String message = "쪽지 전송을 실패했습니다.";
boolean result = messageCon.createMessages(session, receivers, bean);

if (result) {
	message = "쪽지를 전송했습니다.";
}	
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		window.opener.document.location.href = window.opener.document.location.href;
		window.close();
	</script>
</body>