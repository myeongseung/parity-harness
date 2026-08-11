<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
UserBean user = WebHelper.getValidUser(session); 

if (user != null) {
	if (permissionCon.isAdmin(session)) {
		response.sendRedirect("../admin/admin.jsp");
	} else {
		response.sendRedirect("../index.jsp");
	}
}
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>ERFlow Login</title>
  <link rel="stylesheet" href="../css/login/login.css">
  <script src="https://code.jquery.com/jquery-latest.min.js"></script>
  <link href="https://fonts.googleapis.com/css?family=Poppins:600&display=swap" rel="stylesheet">
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
        <form action="Login" method="post">
            <img src="https://raw.githubusercontent.com/sefyudem/Responsive-Login-Form/master/img/avatar.svg">
            <h2 class="title">Welcome</h2>
                <div class="input-div one">
                    <div class="i"> 
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="div">
                        <h5 class="h5-input">Username</h5>
                        <input type="text" name="id" class="input">
                    </div>
                </div>
                <div class="input-div pass">
                    <div class="i"> 
                        <i class="fas fa-lock"></i>
                    </div>
                    <div class="div">
                        <h5 class="h5-input">Password</h5>
                        <input type="password" name="password" class="input">
                    </div>
                </div>
                <a href="findPassword.jsp">Forgot Password?</a>
                <input type="submit" class="btn" value="Login">
            </form>
        </div>
    </div>
    <script src="../js/login/loginSub.js"></script>
</body>
</html>