<!-- unitUpdate.jsp -->
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="model.UnitBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="unitCon" class="controller.UnitController" />
<%
final String PROGRAM_CODE = "8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2";

if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
UnitBean bean = null;
String id = request.getParameter("id");
boolean isValid = true;

if ((isValid = id != null)) {
	bean = unitCon.getUnit(session, id);
	isValid = bean != null;
}
if (!isValid) {
	response.sendRedirect("../accessError.jsp");
	return;
}
String userID = bean.getChargerId();
long documentId = bean.getDocumentId();
String name = bean.getName();
int status = bean.getStatus();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>설비 업데이트 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/main/unitRegister.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/admin/admin.js"></script>
<script src="../js/main/unitRegister.js"></script>
</head>
<body>
	<div class="unit-register-form">
		<form action="unitUpdateProc.jsp" method="post">
			<input type="hidden" name="id" value="<%=bean.getId()%>">
			<div class="form-group">
				<label for="userID">관리자 사번</label> 
				<input type="text" id="userID" name="userID" value="<%=userID%>">
				<button type="button" class="btn btn-light find-user">관리자 선택</button>
			</div>
			<div class="form-group">
				<label for="documentID">문서번호</label> <input type="text"
					id="documentID" name="documentID" class="documentId"
					value="<%=documentId%>" readonly>
				<button type="button" class="btn btn-light find-document">문서 선택</button>
			</div>
			<div class="form-group">
				<label for="equipmentName">장비명</label> <input type="text"
					id="equipmentName" name="equipmentName" value="<%=name%>">
			</div>
			<div class="form-group">
				<select class="form-select form-select-lg mb-3" aria-label=".form-select-sm example" name="status">
					<option value="0" <%=status == 0 ? "selected" : ""%>>멈춤</option>
					<option value="1" <%=status == 1 ? "selected" : ""%>>가동중</option>
				</select>
			</div>
			<button type="submit">제출</button>
		</form>
	</div>
</body>
</html>