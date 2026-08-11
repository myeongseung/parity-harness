<!-- profileUpdate.jsp -->
<%@page import="java.util.Optional"%>
<%@page import="helper.WebHelper"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("permissionError.jsp");
	return;
}
String userId = user.getId();
String userName = user.getName();
String userSocialNum = Optional.ofNullable(user.getSocialNumber()).orElse("");
String userEmail = Optional.ofNullable(user.getEmail()).orElse("");
String postalCode = Optional.ofNullable(user.getPostalCode()).orElse("");
String address1 = Optional.ofNullable(user.getAddress1()).orElse("");
String address2 = Optional.ofNullable(user.getAddress2()).orElse("");
String mobilePhone = Optional.ofNullable(user.getMobilePhone()).orElse("");

%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>프로필 수정 페이지</title>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer">
</script>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="css/bootcss/bootstrap.css">
<link rel="stylesheet" href="css/common/common.css">
<link rel="stylesheet" href="css/common/page.css">
<link rel="stylesheet" href="css/main/header.css">
<link rel="stylesheet" href="css/main/aside.css">
<link rel="stylesheet" href="css/admin/userRegister.css">
<script src="js/bootjs/bootstrap.js"></script>
<script src="js/admin/location.js"></script>
<script src="js/admin/admin.js"></script>
</head>
<body>
	<!-- 여기까지 -->
	<%@include file="indexHeader.jsp"%>
	<!-- indexHeader -->

	<!--  본문  -->
	<div class="content-wrap">
		<%@include file="indexSide.jsp"%>

		<div class="user-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > <%=userName%>님의 프로필 수정</span>
			</div>
			<div class="user-insert">
				<div class="user-form">
					<form action="profileUpdateProc.jsp" method="post">
						<div class="form-group">
							<label for="input1">사원 번호:</label> <input type="text" id="input1"
								name="id" placeholder="사원 번호" value="<%=userId %>" readonly>
						</div>
						<div class="form-group">
							<label for="input2">이름:</label> <input type="text" id="input2"
								name="name" placeholder="사원 이름" value="<%=userName%>">
						</div>
						<div class="form-group">
							<label for="input8">주민 번호:</label> <input type="text" id="input8"
								name="socialNumber" placeholder="ex)990115-1234567"
								value="<%=userSocialNum%>" readonly>
						</div>
						<div class="form-group">
							<label for="input3">Email</label> <input type="email" id="input3"
								name="email" placeholder="사원 이메일" value="<%=userEmail%>">
						</div>
						<div class="form-group">
							<label for="input4">사원 주소:</label> <input type="text"
								id="sample6_postcode" placeholder="우편번호" name="postalCode"
								value="<%=postalCode %>" readonly> <input type="text"
								id="sample6_extraAddress" placeholder="동/읍/면" readonly>
							<input type="button" class="btn_execDaumPostcode"value="우편 찾기"> 
							<input type="text" id="sample6_address"
								placeholder="도로명 주소" name="address1" value="<%=address1 %>"
								readonly><br> <input type="text"
								id="sample6_detailAddress" placeholder="상세주소" name="address2"
								value="<%=address2%>">
						</div>
						<div class="form-group">
							<label for="input7">개인 번호</label> <input type="text" id=""
								name="mobilePhone" placeholder="휴대전화"
								value="">
						</div>
						<div class="btn-group">
							<button class="userRegister-btn" type="submit">사원 수정</button>
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
