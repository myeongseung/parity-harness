<%@page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>ERFlow Help Center</title>
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
			<form action="../mail/SendMail" method="post">
				<img
					src="https://raw.githubusercontent.com/sefyudem/Responsive-Login-Form/master/img/avatar.svg">
				<h2 class="title">Forgot your password?</h2>
				<div class="input-div one">
					<div class="i">
						<i class="fas fa-user"></i>
					</div>
					<div class="div">
						<h5 class="h5-input">Username</h5>
						<input type="text" class="input" name="id">
					</div>
				</div>
				<div class="input-div pass">
					<div class="i">
						<i class="fa-regular fa-envelope fa-lg"></i>
					</div>
					<div class="div">
						<h5 class="h5-input">Email</h5>
						<input type="email" class="input" name="email">
					</div>
				</div>
				<input type="submit" class="btn" value="send">
			</form>
		</div>
	</div>
	<script src="../js/login/loginSub.js"></script>
</body>
</html>