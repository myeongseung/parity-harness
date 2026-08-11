<!-- userRegister.jsp -->
<%@page import="model.DepartmentBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.JobBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
%>
<!DOCTYPE html>
<html lang="ko">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>관리자 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<link rel="stylesheet" href="../../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/admin.css">
<link rel="stylesheet" href="../../css/admin/adminSidebar.css">
<link rel="stylesheet" href="../../css/admin/adminHeader.css">
<link rel="stylesheet" href="../../css/admin/userRegister.css">
<script src="../../js/admin/admin.js"></script>
<script src="../../js/admin/location.js"></script>
</head>

<body>
	<%@ include file="../adminSide.jsp"%>
	<%@ include file="../adminHeader.jsp"%>
	<!-- 여기가 본문 페이지 -->
	<div class="user-insert-wrap">
		<div class="right-align">
			<span class="menu-name">관리자 > 사원 > 사원추가</span>
		</div>
		<div class="user-insert">
			<div class="user-form">
				<form action="userRegisterProc.jsp" method="post">
					<div class="form-group">
						<label for="input1">사원 번호:</label> <input type="text" id="input1"
							name="id" placeholder="사원 번호">
					</div>
					<div class="form-group">
						<label for="input2">이름:</label> <input type="text" id="input2"
							name="name" placeholder="사원 이름">
					</div>
					<div class="form-group">
						<label for="input8">주민 번호:</label> <input type="text" id="input8"
							name="socialNumber" placeholder="ex)990115-1234567">
					</div>
					<div class="form-group">
						<label for="input3">Email</label> <input type="email" id="input3"
							name="email" placeholder="사원 이메일">
					</div>
					<div class="form-group">
						<label for="input4">사원 주소:</label> <input type="text"
							id="sample6_postcode" placeholder="우편번호" name="postalCode" readonly>
						<input type="text" id="sample6_extraAddress" placeholder="동/읍/면" readonly>
						<input type="button" class="btn_execDaumPostcode" value="우편 찾기"> 
						<input type="text" id="sample6_address" placeholder="도로명 주소" name="address1"><br> 
						<input type="text" id="sample6_detailAddress" placeholder="상세주소" name="address2">

					</div>
					<script>
						$(document).ready(function() {
							$("#btn_execDaumPostcode").on("click", function() {
								sample6_execDaumPostcode();
							});
						});
					</script>
					<div class="form-group">
						<label for="job">직급</label> <select name="job" id="job">
							<%
							Vector<JobBean> jobs = adminCon.getJobs(null);
							
							for (int i = 0; i < jobs.size(); ++i) {
								JobBean job = jobs.get(i);
								
								int id = job.getId();
								String name = job.getName();
							%>
							<option value="<%=id%>"><%=name%></option>
							<%
							}
							%>
						</select>
					</div>
					<div class="form-group">
						<label for="dept">부서</label> <select name="dept" id="dept">
							<%
							Vector<DepartmentBean> depts = adminCon.getDepts(null);
							
							for (int i = 0; i < depts.size(); ++i) {
								DepartmentBean dept = depts.get(i);
								
								int id = dept.getId();
								String name = dept.getName();
							%>
							<option value="<%=id%>"><%=name%></option>
							<%
							}
							%>
						</select>
					</div>
					<div class="form-group">
						<label for="input7">내선 번호</label> <input type="text" id="input7"
							name="extensionPhone" placeholder="사원 내선 번호">
					</div>
					<div class="form-group">
						<label for="input8">휴대 전화</label> <input type="text" id="input8"
							name="mobilePhone" placeholder="휴대 전화">
					</div>
					<div class="btn-group">
						<button class="userRegister-btn" type="submit">사원 생성</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>