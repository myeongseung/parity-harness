<!-- documentDeleteProc.jsp -->
<%@page import="model.UserBean"%>
<%@page import="helper.WebHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
final String PROGRAM_CODE = "4BB57A61E4CE88D4416CD611F74308C927913669E7FE78AE4106D01A9ABAB75A";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || 
		!permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String message = "문서를 삭제하지 못했습니다.";

String[] docIds = request.getParameterValues("docId");
boolean result = false;

if (docIds != null) {
	result = true;
	
	for (String docId : docIds) {
		long id = Long.parseLong(docId);
		
		result &= !activityCon.hasProposal(id) && activityCon.deleteDocument(session, id);
	}
	if (result) {
		message = "문서를 삭제하였습니다.";
	} else {
		message = "결재 진행 중인 항목 외의 문서를 삭제하였습니다.";
	}
} else {
	message = "선택한 문서가 없습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'documentList.jsp';
	</script>
</body>