<!-- documentRegisterProc.jsp -->
<%@page import="model.DocumentBean"%>
<%@page import="model.UserBean"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
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
String paramDocId = request.getParameter("id");
String paramFlag = request.getParameter("flag");
String paramContent = request.getParameter("content");
String paramSubject = request.getParameter("subject");

String message = "문서를 등록하지 못했습니다.";
String nextPage = "documentRegister.jsp";
boolean result = false;

if (paramDocId != null && !paramDocId.trim().equals("") &&
	paramContent != null && !paramContent.trim().equals("") &&
	paramFlag != null && !paramFlag.trim().equals("")) {
	long docId = -1;
	
	try {
		docId = WebHelper.parseLong(request, "id");
	} catch (NumberFormatException e) {
		
	}
	DocumentBean bean = new DocumentBean(
		docId,
		user.getId(),
		0,
		0,
		paramSubject,
		paramContent,
		0,
		0,
		null,
		null
	);
	
	switch (paramFlag) {
		case "insert":
			result = activityCon.createDocument(session, bean);
			break;
		case "update":
			message = "문서를 수정하지 못했습니다.";
			nextPage += String.format("?flag=%s&id=%d", paramFlag, docId);
			
			if (activityCon.hasProposal(docId)) {
				paramFlag = "renew";
				result = activityCon.createDocument(session, bean);
			} else {
				result = activityCon.updateDocument(session, bean);
			}
			break;
	}
} else {
	response.sendRedirect("../permissionError.jsp");
	return;
}
if (result) {
	switch (paramFlag) {
		case "insert":
			message = "문서를 등록했습니다.";
			break;
		case "update":
			message = "문서를 수정했습니다.";
			break;
		case "renew":
			message = "이미 결재 진행된 문서이므로, 문서를 새로 등록합니다.";
			break;
	}
	nextPage = "documentList.jsp";
}
%>
<body>
<script type="text/javascript">
	alert('<%=message%>');
	location.href = '<%=nextPage%>';
</script>
</body>