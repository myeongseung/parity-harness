<!-- boardDeptUpdate.jsp -->
<%@page import="model.BoardBean"%>
<%@page import="model.DepartmentBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page import="model.view.ViewPermissionBean"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String paramBoardId = request.getParameter("id");
String flag = request.getParameter("flag");
boolean isValid = true;
long permission = 0L;
int boardId = -1;

if (flag != null && !flag.trim().equals("") &&
	paramBoardId != null && !paramBoardId.trim().equals("")) {
	try {
		boardId = WebHelper.parseInt(request, "id");
		
		BoardBean board = adminCon.getBoard(boardId);
		
		switch (flag) {
		case "read":
			permission = board.getDeptReadPermissionLevel();
			break;
		case "write":
			permission = board.getDeptWritePermissionLevel();
			break;
		}
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
%>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>부서 수정 페이지</title>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/deptRegister.css">
<script src="../../js/admin/location.js"></script>
<script
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>
<body>
	<div class="container">
		<form action="boardDeptUpdateProc.jsp" method="post">
			<input type="hidden" name="boardId" value="<%=boardId%>">
			<input type="hidden" name="flag" value="<%=flag%>">
			<h2>부서 수정</h2>
			
			<div class="checkbox-container">
				<label>부서 권한:</label><br>
				<%
				Vector<ViewPermissionBean> depts = permissionCon.getDeptPermissions(null, null);
				
				for (int i = 0; i < depts.size(); ++i) {
					ViewPermissionBean dept = depts.get(i);
					
					String beanName = dept.getClassName();
					int beanId = dept.getClassId();
					long level = dept.getLevel();
				%>
					<div class="input-group">
						<input type="checkbox" id="permission<%=i + 1%>" name="permissions"
						value="<%=beanId%>" <%=((permission & level) != 0 ? "checked" : "")%>>
							<label for="permission<%=i + 1%>"><%=beanName%></label>
					</div>
				<%
				}
				%>
				<button type="submit">수정하기</button>
			</div>
		</form>
	</div>
</body>
</html>