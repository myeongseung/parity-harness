<!-- jotDeleteProc.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String[] ids = request.getParameterValues("jobId");
String message = "직급을 삭제하지 못했습니다.";
boolean result = ids != null;

if (result) {
	for (String id : ids) {
		int jobId = Integer.parseInt(id);
		
		result &= adminCon.deleteJob(session, jobId);
	}
} else {
	message = "선택한 직급이 없습니다.";
}
if (result) {
	message = "직급을 삭제했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'jobDeptList.jsp';
	</script>
</body>