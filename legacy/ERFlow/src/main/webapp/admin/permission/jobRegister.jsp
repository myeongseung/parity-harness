<!-- jobRegister.jsp -->
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
%>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>직급 생성 페이지</title>
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/jobRegister.css">
</head>
<body>

	<div class="container">
		<form action="jobRegisterProc.jsp" method="post">
			<h2>직급 생성</h2>

			<label for="jobName">직급명:</label> 
			<input type="text" id="jobName" name="jobName">
			
			<button type="submit">생성하기</button>
		</form>

	</div>

</body>
</html>
