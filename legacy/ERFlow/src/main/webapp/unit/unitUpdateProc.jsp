<!-- unitUpdateProc.jsp -->
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="model.UnitBean"%>
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="unitCon" class="controller.UnitController" />
<jsp:useBean id="unit" class="model.UnitBean" />
<%
final String PROGRAM_CODE = "8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2";

if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] { "userID", "documentID", "equipmentName", "status" };
String message = "설비를 수정하지 못했습니다.";

String id = request.getParameter("id");

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
	unit.setId(id);
	unit.setChargerId(parameters.get("userID"));
	unit.setDocumentId(Integer.parseInt(parameters.get("documentID")));
	unit.setName(parameters.get("equipmentName"));
	unit.setStatus(Integer.parseInt(parameters.get("status")));

	result = unitCon.updateUnit(session, unit);
}

if (result) {
	message = "설비를 수정하였습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'unitList.jsp'; 
	</script>
</body>