<!-- proposalRegisterProc.jsp -->
<%@page import="model.view.ViewProposalBean"%>
<%@page import="model.ProposalBean"%>
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
<jsp:useBean id="proposalCon" class="controller.ProposalController" />
<jsp:useBean id="userCon" class="controller.UserController" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"userId", "documentId", "routeId", "route"
};
String message = "등록에 성공했습니다.";
String nextPage = "proposalList.jsp";

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

if (result) {
	ProposalBean bean = new ProposalBean();
	bean.setUserId(parameters.get("userId"));
	bean.setDocumentId(Integer.parseInt(parameters.get("documentId")));
	bean.setRouteId(Integer.parseInt(parameters.get("routeId")));
	bean.setStep(0);
	result = proposalCon.createProposal(bean);
	if (result) {
		Vector<ViewProposalBean> vlist = proposalCon.getProposalViews(bean.getDocumentId());
		nextPage = "proposalDocument.jsp?proposalId="+vlist.get(0).getId(); 
	}
}
if (!result) {
	message = "등록에 실패했습니다.";
	nextPage = "proposalRegister.jsp";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = '<%=nextPage%>';
	</script>
</body>