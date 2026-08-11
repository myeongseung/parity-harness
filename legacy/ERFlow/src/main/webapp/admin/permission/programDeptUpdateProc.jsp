<!-- programDeptUpdateProc.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="model.ProgramBean"%>
<%@page import="model.view.ViewPermissionBean"%>
<%@page import="java.util.Vector"%>
<%@page import="helper.WebHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
String programId = request.getParameter("programId");
String permissions[] = request.getParameterValues("permissions");

if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
Vector<ViewPermissionBean> vlist = permissionCon.getDeptPermissions(null, null);
String message = "부서 권한을 수정하지 못했습니다.";
String programCode = null; 
long result = Long.MIN_VALUE;

if (programId != null && !programId.trim().equals("")) {
	int id = WebHelper.parseInt(request, "programId");
	ProgramBean bean = adminCon.getProgram(session, id);
	
	programCode = bean.getProgramId();
} else {
	response.sendRedirect("../../accessError.jsp");
	return;
}
// 권한 조합하기
if (permissions != null) {
	for (String permission : permissions) {
		int current = Integer.parseInt(permission);
		
		for (ViewPermissionBean bean : vlist) {
			if (bean.getClassId() == current) {
				result |= bean.getLevel();
			}
		}
	}
}
boolean flag = permissionCon.changeProgramDeptPermission(session, programCode, result);

if (flag) {
	message = "부서 권한 정보를 수정했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'programList.jsp';
	</script>
</body>