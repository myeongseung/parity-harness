<!-- documentFormRegisterProc.jsp -->
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="adminCon" class="controller.AdminController" />
<%
if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String message = "문서 양식을 등록하지 못했습니다.";

String flag = request.getParameter("flag");
String content = request.getParameter("content");
String subject = request.getParameter("subject");
boolean result = false;

if (flag != null && !flag.trim().equals("") &&
	content != null && !content.trim().equals("") &&
	subject != null && !subject.trim().equals("")) {
	
	switch (flag) {
		case "insert":
			result = adminCon.createTemplate(session, request);
			break;
		case "update":
			message = "문서 양식을 수정하지 못했습니다.";
			result = adminCon.updateTemplate(session, request);
			break;
	}
} else {
	response.sendRedirect("../../accessError.jsp");
	return;
}
if (result) {
	switch (flag) {
		case "insert":
			message = "문서 양식을 등록했습니다.";
			break;
		case "update":
			message = "문서 양식을 수정했습니다.";
	}
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'documentFormList.jsp';
	</script>
</body>