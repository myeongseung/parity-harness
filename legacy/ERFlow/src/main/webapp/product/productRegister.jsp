<!-- userRegister.jsp -->
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.Vector"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String flag = request.getParameter("flag");

int value = 0;

if (flag != null && !flag.trim().equals("")) {
	switch (flag) {
		case "ingredient":
			flag = "원재료";
			value = 0;
			break;
		case "processed":
			flag = "가공품";
			value = 1;
			break;
		case "producted":
			flag = "완제품";
			value = 2;
			break;
	}
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
%>
<!DOCTYPE html>
<html lang="ko">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>제품 등록 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/productRegister.css">
<script src="../js/main/index.js"></script>
<script src="../js/admin/location.js"></script>
<script src="../js/main/product.js"></script>
<script src="../js/bootjs/bootstrap.js"></script>
<script
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>

<body>
	<%@ include file="../indexHeader.jsp"%>
	<div class="content-wrap">
		<%@ include file="../indexSide.jsp"%>
		<!-- 여기가 본문 페이지 -->
		<div class="product-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > <%=value == 2 ? "영업" : "구매"%> > <%=flag%> 등록</span>
			</div>
			<div class="product-insert">
				<div class="product-form">
					<form action="productRegisterProc.jsp" method="post" name="productFrm">
						<div class="form-group d-grid gap-2">
							<label for="input2">제품 ID</label> 
							<input type="text" id="productId" name="productId" placeholder="제품 ID" >
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input1">제품명:</label> 
							<input type="text" id="productName" name="productName" placeholder="제품명">
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input1">수량:</label> 
							<input type="text" id="count" name="count" placeholder="제품 수량">
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input7">타입</label>
							<input type="text" value="<%=flag%>" readonly>
						</div>
						<div class="btn-group">
							<button class="productRegister-btn" type="submit">제품 등록</button>
						</div>
						<input type="hidden" name="type" value="<%=value%>">
					</form>
				</div>
			</div>
		</div>
	</div>
	</div>
	</div>
</body>
</html>