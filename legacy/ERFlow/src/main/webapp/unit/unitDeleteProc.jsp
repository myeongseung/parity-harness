<!-- unitDeleteProc.jsp -->
<!-- 
	@author 장진원
	@version 1.0.2
	@see
 -->
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Vector"%>
<%@page import="model.UserBean"%>
<%@page import="model.view.ViewUnitBean"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
final String PROGRAM_CODE = "8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2";

if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String message = "설비를 삭제하지 못했습니다.";
String nextPage = "unitList.jsp";
String[] unitId = request.getParameterValues("unitId");
boolean flag = true;

// POST 값 전달 확인
if (unitId != null) {
	for (String id : unitId) {
		flag &= activityCon.deleteUnit(session, id);
	}
} else {
	message = "삭제할 설비를 선택해주세요.";
}
if (flag) {
	message = "설비를 삭제하였습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = '<%=nextPage%>';
	</script>
</body>