<!-- passwordCheckProc.jsp -->
<%@page import="model.UserBean"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="userCon" class="controller.UserController" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("permissionError.jsp");
	return;
}
String message = "비밀번호가 틀렸습니다.";
String nextPage = "profile.jsp?id=" + user.getId();
String password = request.getParameter("password");
boolean result = false;

if (password != null) {
	result = userCon.login(user.getId(), password);
} else {
	response.sendRedirect("accessError.jsp");
	return;
}

if (result) {
	nextPage = "profileUpdate.jsp";
}
%>
<body>
	<script>
		if (<%=!result%>) {
			alert('<%=message%>');
		}
		location.href = '<%=nextPage%>';
	</script>
</body>