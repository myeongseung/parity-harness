<!-- boundUpdate.jsp -->
<%@page import="model.view.ViewBoundBean"%>
<%@page import="controller.PermissionController"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="boundCon" class="controller.BoundController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
final String INBOUND_PROGRAM_CODE = "8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0";
final String OUTBOUND_PROGRAM_CODE = "E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7";

// 로그인 여부 확인
if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String flag = request.getParameter("flag");
String paramId = request.getParameter("id");
boolean isInbound = false;
int boundId = -1;

String productId = "";
String productName = "";
String userId = "";
String userName = "";
String postalCode = "";
String address1 = "";
String address2 = "";
String boundedAt = "";
int count = 0;

// 쿼리 문자열 확인
if (flag != null && !flag.trim().equals("") &&
	paramId != null && !paramId.trim().equals("")) {
	boundId = WebHelper.parseInt(request, "id");
	isInbound = flag.equals("inbound");
	
	ViewBoundBean bean = boundCon.getBoundByType(boundId, isInbound ? 0 : 1);
		
	productId = bean.getProductId();
	productName = bean.getProductName();
	userId = bean.getUserId();
	userName = bean.getUserName();
	postalCode = bean.getPostalCode();
	address1 = bean.getAddress1();
	address2 = bean.getAddress2();
	boundedAt = bean.getBoundedAt();
	count = bean.getCount();
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
String programCode = isInbound ? INBOUND_PROGRAM_CODE : OUTBOUND_PROGRAM_CODE;

// 프로그램 권한이 있는가?
if (!permissionCon.hasProgramPermission(session, programCode)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title> <%=isInbound ? "입고" : "출고" %>수정 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script> <!-- Jquery -->
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css"> <!-- datepicker -->
<script src="https://code.jquery.com/jquery-1.12.4.js"></script> <!-- datepicker -->
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script> <!-- datepicker -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/boundRegister.css">
<script
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/admin/location.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/main/bound.js"></script>
<script src="../js/datepicker.js"></script>
</head>
<body>
	<%@ include file="/indexHeader.jsp"%>
	<div class="content-wrap">
		<%@ include file="/indexSide.jsp"%>
		<!-- 여기가 본문 페이지 -->
		<div class="bound-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > <%=isInbound ? "구매" : "영업" %> > <%=isInbound ? "입고" : "출고" %> 수정</span>
			</div>
			<div class="bound-insert">
				<div class="bound-form">
					<form action="boundUpdateProc.jsp" method="post" name="boundFrm">
						<input type="hidden" name="boundId" value="<%=boundId%>">
						<input type="hidden" name="flag" value="<%=flag%>">
						<div class="form-group d-grid gap-2">
							<label for="input1">제품명:</label> 
							<input type="text" id="productName" name="productName" value="<%=productName%>" placeholder="제품명" readonly>
							<input type="hidden" id="productId" name="productId" value="<%=productId%>"><br>
							<input type="button" class="btn btn-secondary btn-lg"  id="find-product" value="제품 찾기">
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input1"><%=isInbound ? "입고" : "출고" %>자:</label> 
							<input type="text" id="userName" name="userName" value="<%=userName %>" placeholder="<%=isInbound ? "입고" : "출고" %>자" readonly>
							<input type="hidden" id="userId" name="userId" value="<%=userId %>"><br>
							<input type="button" class="btn btn-secondary btn-lg"  id="find-user" value="직원 찾기">
						</div>
						<div class="form-group">
							<label for="input4">창고 주소:</label> <input type="text"
								id="sample6_postcode" placeholder="우편번호" name="postalCode"
								value="<%=postalCode%>"> 
								<input type="text" id="sample6_extraAddress" placeholder="참고항목"> 
								<input type="button" class="btn btn-secondary btn-lg w-100 btn_execDaumPostcode" value="우편 찾기"> 
								<input type="text" id="sample6_address" placeholder="도로명 주소" name="address1" value="<%=address1%>"><br> 
								<input type="text" id="sample6_detailAddress" placeholder="상세주소" name="address2" value="<%=address2%>">
						</div>

						<div class="form-group">
							<label for="input7">수량</label> <input type="text" id="input7"
								name="count" placeholder="수량" value="<%=count%>">
						</div>
						<div class="form-group">
							<label for="input8"><%=isInbound ? "입고" : "출고" %> 날짜</label> 
							<input type="text" id="datepicker" name="boundedAt" placeholder="<%=isInbound ? "입고" : "출고" %>날짜">
						</div>
						<div class="form-group text-center">
							<button type="submit" class="form-Register-btn">제출</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
