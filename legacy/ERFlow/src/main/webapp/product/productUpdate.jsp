<!-- companyUpdate.jsp -->
<%@page import="model.ProductBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="productCon" class="controller.ProductController" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
ProductBean bean = null;
String flag = request.getParameter("flag");
String id = request.getParameter("id");

boolean isValid = false;
int value = 0;

if (flag != null && !flag.trim().equals("") &&
	id != null && !id.trim().equals("")) {
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
	bean = productCon.getProduct(id);
	
	isValid = bean != null;
}
if (!isValid) {
	response.sendRedirect("../accessError.jsp");
	return;
}
String productName = bean.getName();
int count = bean.getCount();
int type = bean.getType();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>제품 수정 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/productRegister.css">
<script
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/admin/location.js"></script>
<script src="../js/main/index.js"></script>
</head>
<body>
	<%@ include file="/indexHeader.jsp"%>
	<div class="content-wrap">
		<%@ include file="/indexSide.jsp"%>
		<!-- 여기가 본문 페이지 -->
		<div class="product-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > 제품 > 제품 수정 페이지</span>
			</div>
			<div class="product-insert">
				<div class="product-form">
					<form action="productUpdateProc.jsp" method="post" name="productFrm">
						<div class="form-group d-grid gap-2">
							<label for="productId">제품 ID</label> <input type="text"
								id="productId" name="productId" placeholder="제품 ID"
								value="<%=id%>" readonly> 
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input2">제품 명:</label> 
							<input type="text" id="productName"  name="productName" placeholder="제품명" value="<%=productName %>" >
						</div>
						<div class="form-group d-grid gap-2">
							<label for="count">수량</label> <input type="text"
								id="count" name="count" placeholder="수량"
								value="<%=count%>"> 
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input7">타입</label>
							<input type="text" value="<%=flag%>" readonly>
						</div>
						<div class="form-group text-center">
							<button type="submit" class="form-Register-btn">수정</button>
						</div>
						<input type="hidden" name="type" value="<%=value%>">
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
