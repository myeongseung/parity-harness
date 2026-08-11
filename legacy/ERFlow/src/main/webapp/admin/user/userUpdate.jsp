<!-- userUpdate.jsp -->
<%@page import="java.util.Optional"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="model.DepartmentBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.JobBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="bean" class="model.UserBean" />
<jsp:useBean id="adminCon" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}

String userId = request.getParameter("id");

if (userId == null) {
	return;
}

bean = adminCon.getUser(userId);

if(bean == null){
	return;
}

bean.setId(userId);
String userName = bean.getName();
String socialNumber = Optional.ofNullable(bean.getSocialNumber()).orElse("");
String email = Optional.ofNullable(bean.getEmail()).orElse("");
String postalCode = Optional.ofNullable(bean.getPostalCode()).orElse("");
String address1 = Optional.ofNullable(bean.getAddress1()).orElse("");
String address2 = Optional.ofNullable(bean.getAddress2()).orElse("");
int jobId = bean.getJobId();
int deptId = bean.getDeptId();
String extensionPhone = Optional.ofNullable(bean.getExtensionPhone()).orElse("");
String mobilePhone = Optional.ofNullable(bean.getMobilePhone()).orElse("");

%>
<!DOCTYPE html>
<html lang="ko">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>사원 수정 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="../../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/admin.css">
<link rel="stylesheet" href="../../css/admin/adminSidebar.css">
<link rel="stylesheet" href="../../css/admin/adminHeader.css">
<link rel="stylesheet" href="../../css/admin/userRegister.css">
<script src="../../js/admin/admin.js"></script>
<script src="../../js/admin/location.js"></script>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>

<body>
	<%@ include file="../adminSide.jsp"%>
	<%@ include file="../adminHeader.jsp"%>
	<!-- 여기가 본문 페이지 -->
	<div class="user-insert-wrap">
		<div class="right-align">
			<span class="menu-name">관리자 > 사원 > 사원수정</span>
		</div>
		<div class="user-insert">
			<div class="user-form">
				<form action="userUpdateProc.jsp" method="post">
					<div class="form-group">
						<label for="input1">사원 번호:</label> <input type="text" id="input1"
							name="id" placeholder="사원 번호" value="<%=bean.getId()%>" readonly>
					</div>
					<div class="form-group">
						<label for="input2">이름:</label> <input type="text" id="input2"
							name="name" placeholder="사원 이름" value="<%=userName%>">
					</div>
					<div class="form-group">
						<label for="input8">주민 번호:</label> <input type="text" id="input8"
							name="socialNumber" placeholder="ex)990115-1234567" value="<%=socialNumber%>" readonly>
					</div>
					<div class="form-group">
						<label for="input3">Email</label> <input type="email" id="input3"
							name="email" placeholder="사원 이메일" value="<%=email %>">
					</div>
					<div class="form-group">
						<label for="input4">사원 주소:</label> <input type="text"
							id="sample6_postcode" placeholder="우편번호" name="postalCode" value="<%=postalCode %>" readonly>
						<input type="text" id="sample6_extraAddress" placeholder="동/읍/면" readonly>
						<input type="button" class="btn_execDaumPostcode" value="우편 찾기"> 
						<input type="text" id="sample6_address" placeholder="도로명 주소" name="address1" value="<%=address1 %>" readonly><br> 
						<input type="text" id="sample6_detailAddress" placeholder="상세주소" name="address2" value="<%=address2 %>">

					</div>
					<div class="form-group">
						<label for="job">직급</label> <select name="job" id="job">
							<%
							Vector<JobBean> jobs = adminCon.getJobs(null);
							Collections.sort(jobs, new Comparator() {
								@Override
								public int compare(Object o1, Object o2) {
									int value = -1;
									JobBean j1 = (JobBean)o1;
									JobBean j2 = (JobBean)o2;
									
									if (j1.getName().compareTo(j2.getName()) > 0) {
										value = 1;
									} else if (j1.getName().compareTo(j2.getName()) == 0) {
										value = 0;
									}
									return value;
								}
							});
								
							for (int i = 0; i < jobs.size(); ++i) {
								JobBean job = jobs.get(i);
								
								int beanJobId = job.getId();
								String beanJobName = job.getName();
							%>
							<option value="<%=beanJobId%>" <%=jobId == beanJobId ? "selected" : ""%>><%=beanJobName%></option>
							<%
							}
							%>
						</select>
					</div>
					<div class="form-group">
						<label for="dept">부서</label> <select name="dept" id="dept">
							<%
							Vector<DepartmentBean> depts = adminCon.getDepts(null);
							
								Collections.sort(depts, new Comparator() {
									@Override
									public int compare(Object o1, Object o2) {
										int value = -1;
										DepartmentBean j1 = (DepartmentBean)o1;
										DepartmentBean j2 = (DepartmentBean)o2;
										
										if (j1.getName().compareTo(j2.getName()) > 0) {
											value = 1;
										} else if (j1.getName().compareTo(j2.getName()) == 0) {
											value = 0;
										}
										return value;
									}
								});
								
							for (int i = 0; i < depts.size(); ++i) {
								DepartmentBean dept = depts.get(i);
								
								int beanDeptId = dept.getId();
								String beanDeptName = dept.getName();
							%>
							<option value="<%=beanDeptId%>" <%=deptId == beanDeptId ? "selected" : ""%>><%=beanDeptName%></option>
							<%
							}
							%>
						</select>
					</div>
					<div class="form-group">
						<label for="input7">내선 번호</label> <input type="text" id="input7"
							name="extensionPhone" placeholder="사원 내선 번호" value="<%=extensionPhone%>">
					</div>
					<div class="form-group">
						<label for="input8">휴대 전화</label> <input type="text" id="input8"
							name="mobilePhone" placeholder="휴대 전화" value="<%=mobilePhone%>">
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