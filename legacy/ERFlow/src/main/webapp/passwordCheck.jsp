<!-- passwordCheck.jsp -->
<%@page import="model.UserBean"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>비밀번호 입력</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="css/bootcss/bootstrap.css">
<link rel="stylesheet" href="css/permissionError.css">
</head>
<body>
	<form action="passwordCheckProc.jsp" method="post">
		<div class="error-container">
			<div class="error-title">비밀번호를 재입력 해주세요.</div>
			<input class="form-control form-control-lg" type="password"
			placeholder="password" name="password">
		<button class="error-button"><a href="profile.jsp?id=<%=user.getId()%>">이전페이지</a></button>
		</div>
	</form>
</body>
</html>

