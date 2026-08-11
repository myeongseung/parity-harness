<!-- programJobUpdate.jsp -->
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="model.view.ViewPermissionBean"%>
<%@page import="java.util.Vector"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.ProgramBean"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
String programId = request.getParameter("id");
String programName = null;
long permission = 0L;
int id = -1;

if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
if (programId != null && !programId.trim().equals("")) {
	id = WebHelper.parseInt(request, "id");
	ProgramBean program = adminCon.getProgram(session, id);
	
	programName = program.getProgramName();
	permission = program.getJobLevel();
} else {
	response.sendRedirect("../../accessError.jsp");
	return;
}
%>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>프로그램 직급 수정 페이지</title>
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/jobRegister.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="../../js/admin/permissions.js"></script>
</head>
<body>
	<div class="container">
		<form action="programJobUpdateProc.jsp">
			<input type="hidden" id="programId" name="programId" value="<%=id%>">
			<h2>프로그램 직급 권한 수정</h2>

			<label for="jobName">프로그램명:</label> 
			<input type="text" id="jobName" name="jobName" value="<%=programName%>" disabled>

			<div class="checkbox-container">
				<label>프로그램 직급 권한:</label><br>
				<div class="input-group">
					<input type="checkbox" id="chkAll">
					<label for="chkAll">전체 선택</label>
				</div>
				<br>
				<%
				Vector<ViewPermissionBean> jobs = permissionCon.getJobPermissions(null, null);
				
				for (int i = 0; i < jobs.size(); ++i) {
					ViewPermissionBean job = jobs.get(i);
					
					String beanName = job.getClassName();
					int beanId = job.getClassId();
					long level = job.getLevel();
				%>
				<!-- 프로그램 직급 권한 띄우기 -->
					<div class="input-group">
						<input type="checkbox" id="permission<%=i + 1%>" name="permissions" value="<%=beanId%>" <%=((permission & level) != 0 ? "checked" : "")%>>
						<label for="permission<%=i + 1%>"><%=beanName%></label>
					</div>
				<%
				}
				%>
			</div>
			<button type="submit">수정하기</button>
		</form>
	</div>
</body>
</html>