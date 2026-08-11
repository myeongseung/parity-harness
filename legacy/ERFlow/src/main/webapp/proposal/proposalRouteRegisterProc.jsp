<!-- proposalRouteRegisterProc.jsp -->
<%@page import="model.view.ViewUserBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.view.ViewProposalRouteBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="proposalRouteCon" class="controller.ProposalRouteController" />
<jsp:useBean id="userCon" class="controller.UserController" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"userId", "nickname"
};
String message = "등록에 성공했습니다.";
String nextPage = "proposalRouteList.jsp";

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
String[] userIds = request.getParameterValues("routeId");
if (userIds == null) {
	result = false;
}
Vector<ViewUserBean> users = new Vector<ViewUserBean>();
users.addElement(userCon.getUserView(user.getId()));
for (int i=0;i<userIds.length;i++) {
	ViewUserBean bean = userCon.getUserView(userIds[i]);
	users.addElement(bean);
}
if (result) {
	ViewProposalRouteBean bean = new ViewProposalRouteBean();
	bean.setUserId(parameters.get("userId"));
	bean.setNickname(parameters.get("nickname"));
	bean.setRoute(users);
	result = proposalRouteCon.createProposalRoute(bean);
}
if (!result) {
	message = "등록에 실패했습니다.";
	nextPage = "proposalRouteRegister.jsp";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = '<%=nextPage%>';
	</script>
</body>