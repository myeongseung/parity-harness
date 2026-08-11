<!-- readDeleteProc.jsp -->
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="messageCon" class="controller.MessageController"/>
<%
if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String message = "선택된 쪽지가 없습니다.";
String[] messageIds = request.getParameterValues("messageId");
boolean result = true;

if (messageIds != null) {
	for (String messageId : messageIds) {
		int id = Integer.parseInt(messageId);
		result &= messageCon.deleteMessage(session, id);
	}
} else {
	result = false;
}
if (result) {
	message = "쪽지가 삭제되었습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		window.opener.document.location.href = window.opener.document.location.href;
		window.close();
	</script>
</body>