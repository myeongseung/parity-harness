<!-- boardRegisterProc.jsp -->
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
	if (!permissionCon.isAdmin(session)) {
		response.sendRedirect("../../permissionError.jsp");
		return;
	}
	String message = "게시판을 등록하지 못했습니다.";
	String subject = request.getParameter("subject");
	boolean result = false;
	
	if (subject != null) {
		result = !adminCon.hasBoardName(subject) && adminCon.createBoard(session, request);
	} else {
		response.sendRedirect("../../accessError.jsp");
		return;
	}
	if (result) {
		message = "게시판을 등록하였습니다.";
	}
%>
<script>
	alert('<%=message%>');
	window.opener.document.location.href = window.opener.document.location.href; 
	window.close();
</script>