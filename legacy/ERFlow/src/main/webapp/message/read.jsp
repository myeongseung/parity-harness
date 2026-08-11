<%@page import="org.apache.commons.io.serialization.ClassNameMatcher"%>
<%@page import="model.view.ViewMessageBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="model.MessageBean"%>
<%@page import="model.view.ViewMessageBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="messageCon" class="controller.MessageController"/>
<%
UserBean user = WebHelper.getValidUser(session);

int messageId = -1;

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
if (request.getParameter("messageId") != null) {
	messageId = WebHelper.parseInt(request, "messageId");
}
if (messageId == -1) {
	response.sendRedirect("../accessError.jsp");
	return;
}
messageCon.readMessage(session, messageId);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
 <meta charset="UTF-8">
 <title>쪽지 읽기</title>
	<script src="https://code.jquery.com/jquery-latest.min.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
		integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
		crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/index.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/admin/admin.js"></script>

<link rel="stylesheet" href="../css/message/index.css">
<link rel="stylesheet" href="../css/message/read.css">
</head>
<body>
<form method ="" action="" name="readFrm">
<div class="message-wrap">
	<div class="readmessage-body">
		<%
		ViewMessageBean messageView = messageCon.getMessageView(session, messageId);
		
		int id = messageView.getId();
		String senderId = messageView.getSenderId();
		String senderName = messageView.getSenderName();
		String senderDept = messageView.getSenderDeptName();
		String senderJob = messageView.getSenderJobName();
		String content = messageView.getContent();
		String messageDate = messageView.getCreatedAt();
		String name = "[" + senderDept + "] " + senderName + " " + senderJob;
		%>
		<input type="hidden" name="messageId" value="<%=id%>">
		<input type="hidden" name="senderId" value="<%=senderId%>">
		<table class="readmessage-info">
			<tr>
				<th>보낸사람</th>
				<td class="readmessage-info-sender"><%=name%></td>
			</tr>
			<tr>
				<th>받은시각</th>
				<td class="readmessage-info-receivetime"><%=messageDate%></td>
			</tr>
		</table>
		<hr>
		<div class="readmessage-content"><%=content%></div>
		<hr>
		<div class="readmessage-footer">
			<div class="readmessage-footer-button">
		    	<button class="btn btn-primary reply-inner-message" type="submit">답장</button>
		    	<button class="btn btn-danger delete-message" type="submit">삭제</button>
			</div>
		</div>
	</div>
</div>
</form>
</body>
<script src="../js/message/index.js"></script>
</html>