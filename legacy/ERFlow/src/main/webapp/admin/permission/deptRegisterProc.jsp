<!-- deptReigsterProc.jsp -->
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.HashMap" %>
<jsp:useBean id="deptCon" class="controller.DepartmentController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="dept" class="model.DepartmentBean"/>
<%
final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[]{
	"deptName","postalCode", "address1", "address2"
};
String message = "등록에 성공하였습니다.";
String deptName = null;
boolean accessable = true;
boolean result = false;

if (!permissionCon.isAdmin(session)) {
	message = "등록 권한이 없습니다.";
} else {
	for (String key : keys) {
		String value = request.getParameter(key);

		if (value == null) {
			message = "올바르지 않은 접근입니다.";
			accessable = false;
			break;
		}
		value = value.trim();
		parameters.put(key, value.equals("") ? null : value);
	}
	if (parameters.containsKey("deptName")) {
		deptName = parameters.get("deptName");
	}
	if (accessable && deptName != null && !deptName.trim().equals("") && 
		!deptCon.hasDept(deptName)) {
		dept.setName(parameters.get("deptName"));
		dept.setPostalCode(parameters.get("postalCode"));
		dept.setAddress1(parameters.get("address1"));
		dept.setAddress2(parameters.get("address2"));

		result = deptCon.createDept(session, dept);
	}
	if (accessable && !result) {
		message = "등록에 실패했습니다.";
		result = false;
	}
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		window.opener.document.location.href = window.opener.document.location.href;
		window.close();
	</script>
</body>