<!-- profileUpdateProc.jsp -->
<%@page import="java.util.HashMap"%>
<%@page import="model.UserBean"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="userCon" class="controller.UserController"/>
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("permissionError.jsp");
	return;
}

final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"name", "email", "postalCode", "address1", "address2", "mobilePhone"
};

String message = "프로필을 수정하지 못했습니다.";
boolean isValid = true;

for (String key : keys) {
	String value = request.getParameter(key);
	
	if (value == null) {
		isValid = false;
		break;
	}
	value = value.trim();
	parameters.put(key, value.equals("") ? null : value);
}
boolean result = false;

if (isValid) {
	user.setName(parameters.get("name"));
	user.setEmail(parameters.get("email"));
	user.setPostalCode(request.getParameter("postalCode"));
	user.setAddress1(parameters.get("address1"));
	user.setAddress2(parameters.get("address2"));
	user.setMobilePhone(request.getParameter("mobilePhone"));
	
	result = userCon.updateUser(session, user);
} else {
	response.sendRedirect("accessError.jsp");
	return;
}
if (result) {
	session.removeAttribute("user");	
	session.setAttribute("user", user);
	message = "프로필을 수정했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'profile.jsp?id=<%=user.getId()%>';
	</script>
</body>