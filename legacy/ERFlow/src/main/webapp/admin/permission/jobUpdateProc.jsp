<!-- jobUpdateProc.jsp -->
<%@page import="model.JobBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.view.ViewPermissionBean"%>
<%@page import="java.util.Vector"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" scope="page" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" scope="page" />
<%
	String[] permissions = request.getParameterValues("permissions");
	String jobId = request.getParameter("jobId");
	String jobName = request.getParameter("jobName");
	
	if (!permissionCon.isAdmin(session)) {
		response.sendRedirect("../../permissionError.jsp");
		return;
	}
	if (jobId == null || jobName == null) {
		response.sendRedirect("../../accessError.jsp");
		return;
	}
	Vector<ViewPermissionBean> vlist = permissionCon.getJobPermissions(null, null);
	String message = "직급 정보를 수정하지 못했습니다.";
	boolean flag = false;
	long result = 0L;
	int id = WebHelper.parseInt(request, "jobId");
	
	for (ViewPermissionBean bean : vlist) {
		if (bean.getClassId() == id) {
			result |= bean.getLevel();
			break;
		}
	}
	// 권한을 합성하자.
	if (permissions != null) {
		for (String permission : permissions) {
			int current = Integer.parseInt(permission);
			
			for (ViewPermissionBean bean : vlist) {
				if (bean.getClassId() == current) {
					result |= bean.getLevel();
				}
			}
		}
	}
	// 이름과 권한을 여기서 수정한다.
	JobBean prevJob = adminCon.getJob(id);
	
	if (!adminCon.hasJob(jobName) ||
			prevJob.getName().equals(jobName)) {
		prevJob.setName(jobName);
		// 먼저 직급 이름을 수정하고,
		flag = adminCon.updateJob(session, prevJob);
		// 권한을 수정한다.
		flag &= permissionCon.changeJobPermission(session, prevJob, result);
	}	
	if (flag) {
		message = "직급 정보를 수정했습니다.";
	}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'jobDeptList.jsp';
	</script>
</body>