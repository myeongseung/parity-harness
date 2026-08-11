<!-- proposalDocumentProc.jsp -->
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
	 "proposalId",  "result"
};
String message = "결재하지 못했습니다.";

boolean isValid = true;

for (String key : keys) {
	String value = request.getParameter(key);
	
	if (value == null) {
		isValid = false;
		break;
	}
	value = value.trim();
	parameters.put(key, value.equals("") ? null : value); 
}
long proposalId = -1L;

try {
	proposalId = WebHelper.parseLong(request, "proposalId");
} catch (NumberFormatException e) {
	isValid = false;
}
ProposalBean bean = proposalCon.getProposal(proposalId);

if (!isValid || bean == null) {
	response.sendRedirect("../accessError.jsp");
	return;
}

int button = parameters.get("result").equals("confirm") ? 1 : 2;

bean.setComment(request.getParameter("comment"));
bean.setResult(button);

switch (button) {
	case 1:
		if (!proposalCon.isFinalStep(proposalCon.getProposal(proposalId))) {
			ViewProposalBean route = proposalCon.getProposalView(proposalId);
			proposalCon.confirmProposal(bean);
			int nextStep = bean.getStep() + 1;
			String nextUser = route.getRoute().split(";")[nextStep];
			bean.setUserId(nextUser);
			bean.setStep(nextStep);
			proposalCon.createProposal(bean);
			message = "결재완료하였습니다.";
		} else if (proposalCon.isFinalStep(proposalCon.getProposal(proposalId))){
			proposalCon.confirmProposal(bean);
			proposalCon.confirmProposals(bean);
			message = "결재하였습니다.";
		}
		break;
	case 2:
		proposalCon.rejectProposal(bean);
		proposalCon.rejectProposals(bean);
		message = "반려하였습니다.";
		break;
}

%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'proposalList.jsp';
	</script>
</body>