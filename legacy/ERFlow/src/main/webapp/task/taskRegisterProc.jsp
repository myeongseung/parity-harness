<!-- .jsp -->
<%@page import="java.util.Vector"%>
<%@page import="model.TaskHistoryBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<jsp:useBean id="permissonCon" class="controller.PermissionController" />
<jsp:useBean id="taskCon" class="controller.TaskController" />
<jsp:useBean id="task" class="model.TaskBean" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"userId", "companyId", "documentId",  "taskAt", "status"
};
String message = "등록에 성공했습니다.";
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
	parameters.put(key, value);
}

String[] productIds = request.getParameterValues("productId");
String[] counts = request.getParameterValues("count");

if (productIds == null) {
	result = false;
}
if (result) {
	task.setUserId(parameters.get("userId"));
	task.setCompanyId(Integer.parseInt(parameters.get("companyId")));
	task.setDocumentId(Integer.parseInt(parameters.get("documentId")));
	task.setTaskAt(parameters.get("taskAt"));
	task.setType(type.equals("sell") ? 0 : 1);
	task.setStatus(Integer.parseInt(parameters.get("status")));
	
	Vector<TaskHistoryBean> vlist = new Vector<>();
	
	for (int i=0;i<productIds.length;i++) {
		TaskHistoryBean bean = new TaskHistoryBean();
		bean.setProductId(productIds[i]);
		bean.setCount(Integer.parseInt(counts[i]));
		vlist.addElement(bean);
	}
	result = taskCon.createTask(task, vlist);
}
if (!result) {
	message = "등록에 실패했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = '<%=nextPage%>'; 
	</script>
</body>