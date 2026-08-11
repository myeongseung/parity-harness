<!-- deptRegister.jsp -->
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permission" class="controller.PermissionController" />
<%
if (!permission.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
%>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>부서 생성 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/deptRegister.css">
<script src="../../js/admin/location.js"></script>
<script
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>
<body>
	<div class="container">
		<form action="deptRegisterProc.jsp" method="post">
			<h2>부서 생성</h2>
			<div>
				<label for="deptName">부서 명:</label> 
				<input type="text" id="deptName" name="deptName">
			</div>
			<label for="input4">부서 주소:</label> 
			<input type="text" id="sample6_postcode" placeholder="우편번호" name="postalCode" readonly>
			<input type="text" id="sample6_extraAddress" placeholder="동/읍/면" readonly>
			<button type="button" class="btn btn-primary btn_execDaumPostcode">우편 번호 찾기</button>
			<input type="text" id="sample6_address" placeholder="도로명 주소" name="address1" readonly><br> 
			<input type="text" id="sample6_detailAddress" placeholder="상세주소" name="address2">
			<button type="submit">생성하기</button>
		</form>
	</div>
</body>
</html>
