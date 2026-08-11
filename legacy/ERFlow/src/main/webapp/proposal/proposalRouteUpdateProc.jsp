<!-- proposalRouteUpdateProc.jsp -->
<%@page import="model.view.ViewUserBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.view.ViewProposalRouteBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<jsp:useBean id="permissonCon" class="controller.PermissionController" />
<jsp:useBean id="proposalRouteCon" class="controller.ProposalRouteController" />
<jsp:useBean id="userCon" class="controller.UserController" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String nickname = request.getParameter("nickname");
int proposalId = WebHelper.parseInt(request, "proposalId");
String message = "수정에 성공했습니다.";
String nextPage = "proposalRouteList.jsp";

boolean result = true;

String[] userIds = request.getParameterValues("routeId");

if (userIds == null || nickname == null) {
	result = false;
}
Vector<ViewUserBean> users = new Vector<ViewUserBean>();

for (int i=0;i<userIds.length;i++) {
	ViewUserBean bean = userCon.getUserView(userIds[i]);
	users.addElement(bean);
}
if (result) {
	ViewProposalRouteBean bean = new ViewProposalRouteBean();
	bean.setId(proposalId);
	bean.setNickname(nickname);
	bean.setRoute(users);
	result = proposalRouteCon.updateProposalRoute(bean);
}
if (!result) {
	message = "수정에 실패했습니다.";
	nextPage = "proposalRouteList.jsp";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = '<%=nextPage%>';
	</script>
</body>