<!-- proposalRouteUpdateProc.jsp -->
<%@page import="model.ProcessBean"%>
<%@page import="model.view.ViewUserBean"%>
<%@page import="java.util.Vector"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<jsp:useBean id="permissonCon" class="controller.PermissionController" />
<jsp:useBean id="processCon" class="controller.ProcessController" />
<jsp:useBean id="userCon" class="controller.UserController" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String id = request.getParameter("id");
String name = request.getParameter("processName");
String message = "수정에 성공했습니다.";

boolean result = true;

if (id == null || name == null) {
	result = false;
}

if (result) {
	ProcessBean bean = processCon.getProcess(id);
	if (bean == null) {
		result = false;
	}
	bean.setName(name);
	result = processCon.updateProcess(bean);
}
if (!result) {
	message = "수정에 실패했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		opener.location.reload();
		window.close();
	</script>
</body>