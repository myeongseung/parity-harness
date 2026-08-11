<!-- proposalRouteDeleteProc.jsp -->
<%@page import="java.io.PrintWriter"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="proposalRouteCon" class="controller.ProposalRouteController"/>
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
boolean isValid = false;

String message = "선택한 결재라인을 삭제하지 못했습니다.";
String[] proposalId = request.getParameterValues("proposalId");

if (proposalId != null) {
	isValid = true;
	
	for (String id : proposalId) {
		isValid &= proposalRouteCon.deleteProposalRoute(Integer.parseInt(id));
	}
	if (isValid) {
		message = "선택한 결재라인을 삭제하였습니다.";
	} else {
		message = "결재에 사용중인 결재라인이 있어 일부 결재라인을 삭제할 수 없습니다.";
	}
}
%>
<body>

<script type="text/javascript">
	alert('<%=message%>');
	location.href = 'proposalRouteList.jsp';
</script>
</body>