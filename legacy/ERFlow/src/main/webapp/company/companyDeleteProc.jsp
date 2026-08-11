<!-- companyDeleteProc.jsp -->
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.io.PrintWriter"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="companyCon" class="controller.CompanyController"/>
<%
UserBean user = WebHelper.getValidUser(session);

if (user == null) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

boolean isValid = true;

String message = "삭제에 성공했습니다.";
String paramFlag = request.getParameter("flag");
String[] companyId = request.getParameterValues("companyId");

if (companyId == null || paramFlag == null || paramFlag.trim().equals("")) {
	isValid = false;
}
if (isValid) {
	for (String id : companyId) {
		isValid &= companyCon.deleteCompany(session, id);
	}
} else {
	message = "삭제에 실패했습니다.";
}
%>
<body>

<script type="text/javascript">
	alert('<%=message%>');
	location.href = 'companyList.jsp?flag=<%=paramFlag%>';
</script>
</body>