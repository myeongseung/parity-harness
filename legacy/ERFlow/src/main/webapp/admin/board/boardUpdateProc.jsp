<!-- boardUpdateProc.jsp -->
<%@page import="model.BoardBean"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
	if (!permissionCon.isAdmin(session)) {
		response.sendRedirect("../../permissionError.jsp");
		return;
	}
	String message = "게시판을 수정하지 못했습니다.";
	String paramId = request.getParameter("id");
	String subject = request.getParameter("subject");
	boolean result = false;
	
	if (paramId != null && !paramId.trim().equals("") &&
		subject != null && !subject.trim().equals("")) {
		try {
			int boardId = WebHelper.parseInt(request, "id");
			
			BoardBean board = adminCon.getBoard(boardId);
			
			result = (board.getSubject().equals(subject) || !adminCon.hasBoardName(subject)) &&
					adminCon.updateBoard(session, request);	
		} catch (NumberFormatException e) {
			
		}
	} else {
		response.sendRedirect("../../accessError.jsp");
		return;
	}
	if (result) {
		message = "게시판을 수정하였습니다.";
	}
%>
<script>
	alert('<%=message%>');
	window.opener.document.location.href = window.opener.document.location.href; 
	window.close();
</script>