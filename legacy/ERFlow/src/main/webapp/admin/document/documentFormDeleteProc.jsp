<!-- documentFormDeleteProc.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="adminCon" class="controller.AdminController" scope="page" />
<%
if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String[] paramIds = request.getParameterValues("templateId");
String message = "문서 양식을 삭제하지 못했습니다.";

if (paramIds != null) {
	boolean result = true;
	
	for (String id : paramIds) {
		int templateId = Integer.parseInt(id);
		
		result &= adminCon.deleteTemplate(session, templateId);
	}
	if (result) {
		message = "문서 양식을 삭제하였습니다.";
	}
} else {
	message = "삭제할 양식을 선택하십시오.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'documentFormList.jsp';
	</script>
</body>