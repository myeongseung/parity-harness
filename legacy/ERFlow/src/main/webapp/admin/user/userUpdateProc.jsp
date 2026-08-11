<!-- userUpdateProc.jsp -->
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="admin" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="user" class="model.UserBean" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}

final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[]{"id", "name", "email", "postalCode", "address1", "address2", "job", "dept",
		"extensionPhone"};

String message = "수정에 성공했습니다.";
String nextPage = "userList.jsp";
boolean result = true;

if (permissionCon.isAdmin(session)) {
	for (String key : keys) {
		String value = request.getParameter(key);
		
		if (value == null) {
	result = false;
	break;
		}
		value = value.trim();
		parameters.put(key, value.equals("") ? null : value);
	}
	if (result) {
		user.setId(parameters.get("id"));
		user.setName(parameters.get("name"));
		user.setEmail(parameters.get("email"));
		user.setPostalCode(request.getParameter("postalCode"));
		user.setAddress1(parameters.get("address1"));
		user.setAddress2(parameters.get("address2"));
		user.setJobId(WebHelper.parseInt(request, "job"));
		user.setDeptId(WebHelper.parseInt(request, "dept"));
		user.setExtensionPhone(request.getParameter("extensionPhone"));

		result = admin.updateUser(session, user);
	}
} else {
	message = "필요한 권한 취득에 실패했습니다.";
	nextPage = "../../permissionError.jsp";
}
if (!result) {
	message = "등록에 실패했습니다.";
	nextPage = "userUpdate.jsp";
}
%>
<body>
	<script type="text/javascript">
	alert('<%=message%>');
	location.href = '<%=nextPage%>';
	</script>
</body>
