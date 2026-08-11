<!-- companyDeleteProc.jsp -->
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.io.PrintWriter"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="taskCon" class="controller.TaskController"/>
<%
boolean isValid = true;

String message = "삭제에 성공했습니다.";
String[] taskId = request.getParameterValues("taskId");
String flag = request.getParameter("flag");
String nextPage = flag + "Task.jsp";

if (taskId == null) {
	isValid = false;
}
if (isValid) {
	for (String id : taskId) {
		isValid &= taskCon.deleteTask(Integer.parseInt(id), flag.equals("sell") ? 0 : 1);
	}
} else {
	message = "삭제에 실패했습니다.";
}
%>
<body>

<script type="text/javascript">
	alert('<%=message%>');
	location.href = '<%=nextPage%>';
</script>
</body>