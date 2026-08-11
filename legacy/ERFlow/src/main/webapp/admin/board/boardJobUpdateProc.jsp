<!-- boardJobUpdateProc.jsp -->
<%@page import="model.BoardBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.view.ViewPermissionBean"%>
<%@page import="java.util.Vector"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" scope="page"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController" scope="page"/>
<%
String[] permissions = request.getParameterValues("permissions");
String paramFlag = request.getParameter("flag");
String paramBoardId = request.getParameter("boardId");

if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
boolean isValid = true;
int boardId = -1;

if (paramFlag != null && !paramFlag.trim().equals("") &&
	paramBoardId != null && !paramBoardId.trim().equals("")) {
	try {
		boardId = WebHelper.parseInt(request, "boardId");
	} catch (NumberFormatException e) {
		isValid = false;
	}
} else {
	isValid = false;
}
if (!isValid) {
	response.sendRedirect("../../accessError.jsp");
	return;
}
Vector<ViewPermissionBean> vlist = permissionCon.getJobPermissions(null, null);
BoardBean board = adminCon.getBoard(boardId);
String message = "직급 권한 정보를 수정하지 못했습니다.";
boolean flag = false;
long result = Long.MIN_VALUE;
long others = Long.MIN_VALUE;

// 권한을 합성하자.
if (permissions != null) {
	for (String permission : permissions) {
		int current = Integer.parseInt(permission);
		
		for (ViewPermissionBean bean : vlist) {
			if (bean.getClassId() == current) {
				result |= bean.getLevel();
			}
		}
	}
}
switch (paramFlag) {
	case "read":
		others = board.getJobReadPermissionLevel();
		flag = permissionCon.changeBoardReadPermission(session, board, others, result);
		break;
	case "write":
		others = board.getJobWritePermissionLevel();
		flag = permissionCon.changeBoardWritePermission(session, board, others, result);
		break;
}
if (flag) {
	message = "직급 권한 정보를 수정하였습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'adminBoardList.jsp?boardId=<%=boardId%>';
	</script>
</body>