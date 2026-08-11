<!-- logoutProc.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="userCon" class="controller.UserController"/>
<%
	userCon.logout(session);
%>
<body>
<script type="text/javascript">
	alert('로그아웃 되었습니다.');
	location.href = 'login.jsp';
</script>
</body>