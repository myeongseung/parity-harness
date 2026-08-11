<!-- deptDeleteProc.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String[] ids = request.getParameterValues("deptId");
String message = "부서를 삭제하지 못했습니다.";
boolean result = ids != null;

if (result) {
	for (String id : ids) {
		int deptId = Integer.parseInt(id);
		
		result &= adminCon.deleteDept(session, deptId);
	}
} else {
	message = "선택한 부서가 없습니다.";
}
if (result) {
	message = "부서를 삭제했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'jobDeptList.jsp';
	</script>
</body>