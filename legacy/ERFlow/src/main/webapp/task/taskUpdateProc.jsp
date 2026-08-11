<!-- companyUpdateProc.jsp -->
<%@page import="model.TaskHistoryBean"%>
<%@page import="java.util.Vector"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="task" class="model.TaskBean" />
<jsp:useBean id="taskCon" class="controller.TaskController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
//UserBean user = WebHelper.getValidUser(session);

//if (user == null) {
//	response.sendRedirect("../permissionError.jsp");
//	return;
//}

final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"taskId","userId", "documentId",  "taskAt", "status"
};
String message = "수정에 성공했습니다.";
String type = request.getParameter("flag");
String nextPage = type + "Task.jsp";

boolean result = true;

for (String key : keys) {
	String value = request.getParameter(key);

	if (value == null) {
		result = false;
		break;
	}
	value = value.trim();
	parameters.put(key, value.equals("") ? null : value);
}

String[] productIds = request.getParameterValues("productId");
String[] counts = request.getParameterValues("count");

if (productIds == null) {
	result = false;
}

if (result) {
	task.setId(Integer.parseInt(parameters.get("taskId")));
	task.setUserId(parameters.get("userId"));
	task.setDocumentId(Integer.parseInt(parameters.get("documentId")));
	task.setTaskAt(parameters.get("taskAt"));
	task.setStatus(Integer.parseInt(parameters.get("status")));

	result = taskCon.updateTask(task);
	taskCon.deleteTaskHistory(task.getId());
	Vector<TaskHistoryBean> vlist = new Vector<>();
	for (int i=0;i<productIds.length;i++) {
		TaskHistoryBean bean = new TaskHistoryBean();
		bean.setTaskId(task.getId());
		bean.setProductId(productIds[i]);
		bean.setCount(Integer.parseInt(counts[i]));
		taskCon.createTaskHistory(bean);
	}
}

if (!result) {
	message = "수정에 실패했습니다.";
}

%>
<body>
	<script type="text/javascript">
	alert('<%=message%>');
	location.href = '<%=nextPage%>';
	</script>
</body>