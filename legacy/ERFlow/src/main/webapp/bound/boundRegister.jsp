<!-- boundRegister.jsp -->
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.BoundBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="boundCon" class="controller.BoundController" />
<%
final String INBOUND_PROGRAM_CODE = "8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0";
final String OUTBOUND_PROGRAM_CODE = "E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7";

// 권한 확인 코드
if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, INBOUND_PROGRAM_CODE) || !permissionCon.hasProgramPermission(session, OUTBOUND_PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String flag = request.getParameter("flag");
boolean isInbound = false;


if (flag != null && !flag.trim().equals("")) {
	isInbound = flag.equals("inbound");
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
//프로그램 권한이 있는가?
String programCode = isInbound ? INBOUND_PROGRAM_CODE : OUTBOUND_PROGRAM_CODE;
		
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
<title><%=isInbound ? "입고" : "출고" %> 등록 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script> <!-- Jquery -->
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="https://code.jquery.com/jquery-1.12.4.js"></script> <!-- datepicker -->
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script> <!-- datepicker -->
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/jquery-ui.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/boundRegister.css">
<script src="../js/admin/admin.js"></script>
<script src="../js/admin/location.js"></script>
<script src="../js/main/bound.js"></script>
<script src="../js/datepicker.js"></script>
<script src="../js/bootjs/bootstrap.js"></script>
<script
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>

<body>
	<%@ include file="../indexHeader.jsp"%>
	<div class="content-wrap">
		<%@ include file="../indexSide.jsp"%>
		<!-- 여기가 본문 페이지 -->
		<div class="bound-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > <%=isInbound ? "구매" : "영업" %> > <%=isInbound ? "입고" : "출고" %> 등록</span>
			</div>
			<div class="bound-insert">
				<div class="bound-form">
					<form action="boundRegisterProc.jsp" method="post" name="boundFrm">
					<input type="hidden" name="flag" value = <%=flag%>>
						<div class="form-group d-grid gap-2">
							<label for="input1">제품명:</label> 
							<input type="text" id="productName" name="productName" placeholder="제품명" readonly>
							<input type="hidden" id="productId" name="productId"><br>
							<input type="button" class="btn btn-secondary btn-lg"  id="find-product" value="제품 찾기">
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input2"><%=isInbound ? "입고" : "출고" %>자:</label> 
							<input type="text" id="userId"  name="userId" placeholder="<%=isInbound ? "입고" : "출고" %>자 사번" readonly>
							<input type="button" class="btn btn-secondary btn-lg" id="find-each-user" value="직원 찾기">
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input4">창고 주소:</label> <input type="text"
								id="sample6_postcode" placeholder="우편번호" name="postalCode"
								readonly> <input type="text" id="sample6_extraAddress"
								placeholder="동/읍/면" readonly> 
								<input type="button" value="우편번호 찾기" class="btn btn-secondary btn-lg btn_execDaumPostcode"> 
								<input type="text" id="sample6_address" placeholder="도로명 주소"
								name="address1"><br> <input type="text"
								id="sample6_detailAddress" placeholder="상세주소" name="address2">
						</div>
						<div class="form-group">
							<label for="input7">수량</label> <input type="number" id="input7"
								name="count" placeholder="수량">
						</div>
						<div class="form-group">
							<label for="input8"><%=isInbound ? "입고" : "출고" %> 날짜</label>
							<input type="text" id="datepicker" name="boundedAt" placeholder="<%=isInbound ? "입고" : "출고"%>날짜">
						</div>
						<div class="btn-group">
							<button class="boundRegister-btn" type="submit"><%=isInbound ? "입고" : "출고"%> 등록</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	</div>
	</div>
</body>
</html>