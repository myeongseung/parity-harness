<!-- unitRegisterProc.jsp -->
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="model.UnitBean"%>
<%@page import="controller.UnitController"%>
<%@page import="java.util.HashMap"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="unit" class="model.UnitBean"/>
<%
final String PROGRAM_CODE = "8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2";

if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"equipmentId", "userId", "documentId", "equipmentName", "manufactureDate"
};
String message = "설비를 등록하지 못했습니다.";

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
	long docId = -1;
	
	try {
		docId = WebHelper.parseInt(request, "documentId");
	} catch (NumberFormatException e) {
		
	}
	unit.setId(parameters.get("equipmentId"));
	unit.setChargerId(parameters.get("userId"));
	
	if (docId != -1) {
		unit.setDocumentId(docId);
	}
	unit.setName(parameters.get("equipmentName"));
	unit.setCreateAt(parameters.get("manufactureDate"));

	result = activityCon.createUnit(session, unit);
}
if (result) {
	message = "설비를 등록했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		window.opener.document.location.href = window.opener.document.location.href;
		window.close();
	</script>
</body>