<!-- jobUpdate.jsp -->
<%@page import="model.view.ViewPermissionBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.JobBean"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
String message = "등록에 실패하였습니다.";
String nextPage = "jobDeptList.jsp";
String jobName = null;
boolean result = false;
long permission = 0L;
int paramJobId = -1;
int jobId = -1;

if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
if (request.getParameter("jobId") != null) {
	paramJobId = WebHelper.parseInt(request, "jobId");
} else {
	response.sendRedirect("../../accessError.jsp");
	return;
}
if (paramJobId != -1) {
	JobBean job = adminCon.getJob(paramJobId);
	jobId = job.getId();
	jobName = job.getName();
}
%>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>직급 수정 페이지</title>
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/jobRegister.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="../../js/admin/permissions.js"></script>
</head>
<body>
	<div class="container">
		<form action="jobUpdateProc.jsp" method="post">
			<h2>직급 수정</h2>

			<label for="jobName">기존 직급 명칭: <%=jobName%></label> 
			<input type="hidden" id="jobId" name="jobId" value="<%=jobId%>">
			<input type="text" id="jobName" name="jobName" value="<%=jobName%>">

			<div class="checkbox-container">
				<label>직급 권한:</label><br>
				<div class="input-group">
					<input type="checkbox" id="chkAll">
					<label for="chkAll">전체 선택</label>
				</div>
				<br>
				<%
				Vector<ViewPermissionBean> jobs = permissionCon.getJobPermissions(null, null);
				
				for (ViewPermissionBean bean : jobs) {
					if (jobId == bean.getClassId()) {
						permission = bean.getPermission();
						break;
					}
				}
				
				for (int i = 0; i < jobs.size(); ++i) {
					ViewPermissionBean job = jobs.get(i);
					
					String beanName = job.getClassName();
					int beanId = job.getClassId();
					long level = job.getLevel();
					
					if (jobId != beanId) {
				%>
					<div class="input-group">
						<input type="checkbox" id="permission<%=i + 1%>" name="permissions"
						value="<%=beanId%>" <%=((permission & level) != 0 ? "checked" : "")%>>
							<label for="permission<%=i + 1%>"><%=beanName%></label>
					</div>
				<%
					}
				}
				%>
				<button type="submit">수정하기</button>
			</div>
		</form>
	</div>
</body>
</html>
