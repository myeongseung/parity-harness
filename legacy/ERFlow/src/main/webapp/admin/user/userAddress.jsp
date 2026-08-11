<%@page import="model.view.ViewUserBean"%>
<%@page import="java.util.Vector"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String id = null;
ViewUserBean user = null;

if ((id = request.getParameter("id")) != null) {
	user = adminCon.getUserView(id);
}
if (user == null) {
	response.sendRedirect("../../login.jsp");
}
String name = user.getName();
String jobName = user.getJobName();
String postalCode = user.getPostalCode();
String address1 = user.getAddress1();
String address2 = user.getAddress2();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Document</title>
<link rel="stylesheet" href="../../css/admin/userAddress.css">
</head>
<body>
	<div class="location-group">
		<label for="input4"><%=name%> <%=jobName%>의 주소</label><br>
		<span class="location-span">우편번호 : <%=postalCode%></span><br>
		<span class="location-span">도로명 주소 : <%=address1%></span><br>
		<span class="location-span">상세 주소 : <%=address2%></span>
	</div>
</body>
</html>