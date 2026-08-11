<!-- processRegister.jsp -->
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
final String PROGRAM_CODE = "F03BEAC83BFF5F1D13D55F26C10040BED42E0C5BBAC8EC850574D03C13AE8CFF";

if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>공정 생성</title>
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
<link rel="stylesheet" href="../css/process/processRegister.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/process/processRegister.js"></script>
<script src="../js/main/index.js"></script>

</head>
<body>
	<%@include file="/indexHeader.jsp"%>
	<div class="content-wrap">
		<%@include file="/indexSide.jsp"%>
		<div class="proposal-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > 생산관리 > 공정 등록</span>
			</div>
			<div class="proposal-insert">
				<div class="proposal-form">
					<form action="processRegisterProc.jsp" method="post" name="processFrm">
						<div class="form-group d-grid gap-2">
							<label for="input1">공정ID:</label> 
							<input type="text" class="processId" name="processId" value="" placeholder="공정ID">
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input2">공정명</label> 
							<input type="text" class="processName" name="processName" value="" placeholder="공정명">
						</div>
						<div class="form-group d-grid gap-2">
							<input type="button" class="btn btn-secondary btn-lg" id="process-register-button" value="공정 추가">
							<table id="myTable" class="table table-striped">
								<tr>
									<th>공정ID</th>
									<th>공정명</th>
									<th>우선순위</th>
									<th></th>
								</tr>
							</table>
						</div>
						<div class="btn-group">
							<button class="proposalRegister-btn" id="registerBtn" type="button">공정 등록</button>
						</div>
					</form>
					<form name="readFrm">
						<input type="hidden" name="receiver"> 
						<input type="hidden" name="processId"> 
						<input type="hidden" name="nickname">
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>