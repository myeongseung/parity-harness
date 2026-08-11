<!-- proposalRouteRegister.jsp -->
<%@page import="java.net.URLDecoder"%>
<%@page import="java.util.Base64"%>
<%@page import="controller.UserController"%>
<%@page import="model.view.ViewUserBean"%>
<%@page import="java.util.Vector"%>
<%@page import="controller.PermissionController"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="model.UserBean"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<%
final String PROGRAM_CODE = "53A5157A4BAB5F1B75921C9B42888D7E1539466CD3DD258C1E318F29B62EC586";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || 
		!permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String documentId = "";
String documentName = "";
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>결재 생성</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/proposal/proposalRegister.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/proposal/proposalRegister.js"></script>
<script src="../js/main/index.js"></script>

</head>
<body>
	<%@include file="/indexHeader.jsp"%>
	<div class="content-wrap">
		<%@include file="/indexSide.jsp"%>
		<div class="proposal-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > 전자결재 > 결재 생성</span>
			</div>
			<div class="proposal-insert">
				<div class="proposal-form">
					<form action="proposalRegisterProc.jsp" method="post" name="proposalFrm">
						<input type="hidden" id="userId" name="userId" value="<%=user.getId()%>">
						<div class="input-group mb-3">
						  <div class="input-group-prepend">
						    <span class="input-group-text" id="basic-addon1">결재 문서:</span>
						  </div>
						  <input type="text" class="form-control" id="documentId" name="documentId" 
						  value="<%=documentId %>" placeholder="결재 문서 ID" readonly>
						  <input type="button" class="btn btn-secondary btn-lg" id="find-document" value="문서 찾기">
						</div>
						<div class="input-group mb-3">
							<div class="input-group-prepend">
						    <span class="input-group-text" id="basic-addon1">결재 문서 명:</span>
						  </div>
						  <input type="text" class="form-control" id="documentName" name="documentName" 
						  value="<%=documentName %>" placeholder="결재 문서 명" readonly>
						</div>
						<hr>
						<div class="input-group mb-3">
						  <div class="input-group-prepend">
						    <span class="input-group-text" id="basic-addon1">결재 ID:</span>
						  </div>
						  <input type="text" class="form-control" id="routeId" name="routeId"
						  value="" placeholder="결재라인 번호" readonly>
						  <input type="button" class="btn btn-secondary btn-lg" id="find-route" value="결재라인 찾기">
						</div>
						<div class="input-group mb-3">
							<div class="input-group-prepend">
						    <span class="input-group-text" id="basic-addon1">결재 명:</span>
						  </div>
						  <input type="text" class="form-control" id="nickname" name="nickname"
						  value="" placeholder="결재 명" readonly>
						</div>
						<div class="input-group mb-3">
							<div class="input-group-prepend">
						    <span class="input-group-text" id="basic-addon1">결재 라인:</span>
						  </div>
							<input type="text" class="form-control" id="route" name="route"
						  value="" placeholder="결재라인" readonly>
						</div>
						<div class="btn-group">
							<button class="proposalRegister-btn" type="submit">결재 등록</button>
						</div>
					</form>

				</div>
			</div>
		</div>
	</div>
</body>
</html>