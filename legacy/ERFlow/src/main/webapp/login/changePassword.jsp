<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
String id = (String) session.getAttribute("tempId");

if (id == null) {
	response.sendRedirect("login.jsp");
	return;
}
%>
<!DOCTYPE html>
<html>
<head>
<title>Animated Login Form</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/login/login.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Poppins:600&display=swap">
<script src="https://kit.fontawesome.com/a81368914c.js"></script>
<meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
	<img class="wave" src="../images/login/backImage.png">
	<div class="container">
		<div class="img">
			<img src="../images/common/logo.png">
		</div>
		<div class="login-content">
			<form action="ChangePassword" method="post">
				<img
					src="https://raw.githubusercontent.com/sefyudem/Responsive-Login-Form/master/img/avatar.svg">
				<h2 class="title">Change Password</h2>
				<div class="input-div one">
					<div class="i">
						<i class="fas fa-lock"></i>
					</div>
					<div class="div">
						<h5 class="h5-input">Password</h5>
						<input type="password" class="input" name="password">
					</div>
				</div>
				<div class="input-div pass">
					<div class="i">
						<i class="fas fa-lock"></i>
					</div>
					<div class="div">
						<h5 class="h5-input">Confirm Password</h5>
						<input type="password" class="input" name="rePassword">
					</div>
				</div>
				<input type="submit" class="btn" value="send">
			</form>
		</div>
	</div>

	<script src="../js/login/loginSub.js"></script>
	<script>
		document.querySelector("form").addEventListener(
				"submit",
				function(e) {
					const password = document
							.querySelector("input[name='password']").value;
					const rePassword = document
							.querySelector("input[name='rePassword']").value;

					if (password !== rePassword) {
						alert("비밀번호가 일치하지 않습니다.");
						// 화면을 갱신하지 않음
						e.preventDefault();
					}
				});
	</script>
</body>
</html>