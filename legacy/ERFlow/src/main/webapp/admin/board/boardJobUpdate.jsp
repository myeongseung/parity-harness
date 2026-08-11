<!-- boardJobUpdate.jsp -->
<%@page import="model.BoardBean"%>
<%@page import="model.view.ViewPermissionBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.JobBean"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String paramBoardId = request.getParameter("id");
String flag = request.getParameter("flag");
boolean isValid = true;
long permission = 0L;
int boardId = -1;

if (flag != null && !flag.trim().equals("") &&
	paramBoardId != null && !paramBoardId.trim().equals("")) {
	try {
		boardId = WebHelper.parseInt(request, "id");
		
		BoardBean board = adminCon.getBoard(boardId);
		
		switch (flag) {
		case "read":
			permission = board.getJobReadPermissionLevel();
			break;
		case "write":
			permission = board.getJobWritePermissionLevel();
			break;
		}
	} catch (NumberFormatException e) {
		isValid = false;
	}
} else {
	isValid = false;
}
if (!isValid) {
	response.sendRedirect("../../accessError.jsp");
	return;
}
%>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>직급 수정 페이지</title>
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/jobRegister.css">
</head>
<body>

	<div class="container">
		<form action="boardJobUpdateProc.jsp" method="post">
			<input type="hidden" name="boardId" value="<%=boardId%>">
			<input type="hidden" name="flag" value="<%=flag%>">
			<h2>직급 수정</h2>

			<div class="checkbox-container">
				<label>직급 권한:</label><br>
				<%
				Vector<ViewPermissionBean> jobs = permissionCon.getJobPermissions(null, null);
				
				for (int i = 0; i < jobs.size(); ++i) {
					ViewPermissionBean job = jobs.get(i);
					
					String beanName = job.getClassName();
					int beanId = job.getClassId();
					long level = job.getLevel();
				%>
					<div class="input-group">
						<input type="checkbox" id="permission<%=i + 1%>" name="permissions"
						value="<%=beanId%>" <%=((permission & level) != 0 ? "checked" : "")%>>
							<label for="permission<%=i + 1%>"><%=beanName%></label>
					</div>
				<%
				}
				%>
				<button type="submit">수정하기</button>
		</form>

	</div>

</body>
</html>