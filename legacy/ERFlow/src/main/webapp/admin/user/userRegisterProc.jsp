<!-- userRegisterProc.jsp -->
<%@page import="helper.WebHelper"%>
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="admin" class="controller.AdminController"/>
<jsp:useBean id="permission" class="controller.PermissionController"/>
<jsp:useBean id="user" class="model.UserBean"/>
<%
final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"id", "name", "socialNumber", "email","job", "dept"	
};
String message = "등록에 성공했습니다.";
String nextPage = "userList.jsp";
boolean result = true;

if (permission.isAdmin(session)) {
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
		user.setSocialNumber(request.getParameter("socialNumber"));
		user.setEmail(parameters.get("email"));
		user.setPostalCode(request.getParameter("postalCode"));
		user.setAddress2(parameters.get("address1"));
		user.setAddress2(parameters.get("address2"));
		user.setJobId(WebHelper.parseInt(request, "job"));
		user.setDeptId(WebHelper.parseInt(request, "dept"));
		user.setExtensionPhone(request.getParameter("extensionPhone"));
		
		result = admin.register(session, user);
	}
} else {
	message = "필요한 권한 취득에 실패했습니다.";
	nextPage = "../../permissionError.jsp";
}
if (!result) {
	message = "등록에 실패했습니다.";
	nextPage = "userRegister.jsp";
}
%>
<body>
<script type="text/javascript">
	alert('<%=message%>');
	location.href = '<%=nextPage%>';
</script>
</body>