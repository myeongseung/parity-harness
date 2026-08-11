<!-- boundDeleteProc.jsp -->
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="boundCon" class="controller.BoundController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
final String INBOUND_PROGRAM_CODE = "8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0";
final String OUTBOUND_PROGRAM_CODE = "E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7";

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
boolean isValid = true;

String[] companyId = request.getParameterValues("boundId");
String flag = request.getParameter("flag");
String message = "선택한 내역을 삭제하였습니다.";
String nextPage = flag + ".jsp";
boolean isInbound = false;

if (companyId == null || flag == null || flag.trim().equals("")) {
	isValid = false;
}
if (isValid) {
	isInbound = flag.equals("inbound");
	
	String programCode = isInbound ? INBOUND_PROGRAM_CODE : OUTBOUND_PROGRAM_CODE;

	//프로그램 권한이 있는가?
	if (!permissionCon.hasProgramPermission(session, programCode)) {
		response.sendRedirect("../permissionError.jsp");
		return;
	}
	for (String paramId : companyId) {
		int id = Integer.parseInt(paramId);
		
		if (isInbound) {
	isValid &= boundCon.deleteBound(id, 0);
		} else {
	isValid &= boundCon.deleteBound(id, 1);
		}
	}
}
if (!isValid) {
	message = "선택한 내역을 삭제하지 못했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = '<%=nextPage%>';
	</script>
</body>