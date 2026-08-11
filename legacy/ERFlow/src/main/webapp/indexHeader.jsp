<!-- indexHeader.jsp -->
<%@page import="model.UserBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminTester" class="controller.PermissionController" scope="session"/>
<%
UserBean headerUser = (UserBean) session.getAttribute("user");

String headerUserId = null;
String headerUserName = null;

if (headerUser != null) {
	headerUserId = headerUser.getId();
	headerUserName = headerUser.getName();
} else {
	response.sendRedirect("permissionError.jsp");
	return;
}
%>
<!-- header -->
<header>
	<nav class="navbar navbar-expand-lg bg-light">
		<div class="container-fluid">
			<a class="navbar-brand" href="/ERFlow/index.jsp"><img
				src="/ERFlow/images/common/logo.png" title="ERFlow"></a>
			<button class="navbar-toggler" type="button"
				data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
				aria-controls="navbarSupportedContent" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class="empty-place"></div>
			<div class="collapse navbar-collapse" id="navbarSupportedContent">
				<ul class="navbar-nav me-auto mb-2 mb-lg-0">

				</ul>


				<ul class="nav nav-pills">
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle" data-bs-toggle="dropdown"
						href="#" role="button" aria-expanded="false"> <%=headerUserId%>
							<%=headerUserName%></a>

						<ul class="dropdown-menu">
							<%
							if (adminTester.isAdmin(session)) {
							%>
							<a class="dropdown-item" href="/ERFlow/admin/admin.jsp">
								<i class="fa-solid fa-gear"></i><span>설정</span></a>
							<%
							}
							%>
							<a class="dropdown-item" href="/ERFlow/message/index.jsp">
								<i class="fa-regular fa-envelope fa-lg"></i><span>쪽지</span></a>
							<a class="dropdown-item" href="/ERFlow/profile.jsp?id=<%=headerUserId%>"> 
								<i class="fa-solid fa-user-tie fa-lg"></i><span>프로필</span></a><hr>
							<a class="dropdown-item" href="/ERFlow/login/logoutProc.jsp"
								title="로그아웃"><span>로그아웃</span></a>
						</ul></li>
						<li class="nav-item">
						<a class="nav-link" href="#">
						<i class="fa-regular fa-bell fa-lg"></i></a>
					</li>
				</ul>
			</div>
		</div>
	</nav>
</header>

<!-- 구분 선 -->
<div class="section2"></div>
