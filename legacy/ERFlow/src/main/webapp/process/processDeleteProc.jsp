<!-- proposalRouteUpdateProc.jsp -->
<%@page import="model.ProcessBean"%>
<%@page import="model.view.ViewUserBean"%>
<%@page import="java.util.Vector"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissonCon" class="controller.PermissionController" />
<jsp:useBean id="processCon" class="controller.ProcessController" />
<jsp:useBean id="userCon" class="controller.UserController" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

boolean result = true;

String message = "삭제에 성공했습니다.";
String[] processIds = request.getParameterValues("processId");
String nextPage = "processList.jsp";

if (processIds == null) {
	result = false;
}

if (result) {
	for (String id : processIds) {
		result &= processCon.deleteProcess(id);
	}
} else {
	message = "삭제에 실패했습니다.";
}

if (!result) {
	message = "선택한 내역을 삭제하지 못했습니다.";
}
%>
<body>
	<script type="text/javascript">
	alert('<%=message%>');
	location.href = '<%=nextPage%>';
	</script>
</body>