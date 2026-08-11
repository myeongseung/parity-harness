<!-- jobRegisterProc.jsp -->
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="jobCon" class="controller.JobController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="job" class="model.JobBean"/>
<%
String message = "등록에 실패하였습니다.";
String jobName = request.getParameter("jobName");
boolean result = false;

if (!permissionCon.isAdmin(session)) {
	message = "권한이 없습니다.";
} else if (jobName == null) {
	message = "잘못된 접근입니다.";
} else if (!jobCon.hasJob(jobName)) {
	job.setName(jobName);
	result = jobCon.createJob(session, job);
}
if (result) {
	message = "등록에 성공했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		window.opener.document.location.href = window.opener.document.location.href;
		window.close();
	</script>
</body>