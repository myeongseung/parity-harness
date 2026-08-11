<!-- boardRegister.jsp -->
<%@page import="model.BoardBean"%>
<%@page import="helper.WebHelper"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String boardId = request.getParameter("boardId");
boolean isValid = true;
int id = -1;

if (boardId != null) {
	try {
		id = WebHelper.parseInt(request, "boardId");
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
BoardBean board = adminCon.getBoard(id);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시판 수정</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/common/page.css">
<link rel="stylesheet" href="../../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../../css/admin/boardRegister.css">
<script src="../../js/admin/boardRegister.js"></script>
<script src="../../js/bootjs/bootstrap.js"></script>
</head>
<body>
	<div class="message-wrap">
		<div class="readmessage-body">
			<form name="boardFrm" action="boardUpdateProc.jsp" method="post">
				<input type="hidden" name="id" value="<%=id%>">
				<table class="readmessage-info">
					<tr>
						<td>
							<div class="input-group mb-3">
							  <div class="new-name"><span class="input-group-text">게시판 이름</span></div>
							  <input type="text" name="subject" class="form-control new-name" placeholder="** 게시판" value="<%=board.getSubject()%>">
							</div>
						</td>
					</tr>
				</table>
				<hr>
				<div class="readmessage-footer">
					<div class="readmessage-footer-button">
				    	<button class="btn btn-primary" type="submit">생성</button>
				    	<button class="btn btn-danger cancel-board" type="button">취소</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>