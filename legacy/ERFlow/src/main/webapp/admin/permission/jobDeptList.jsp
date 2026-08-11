<!-- jobDeptList.jsp -->
<%@page import="model.view.ViewPermissionBean"%>
<%@page import="java.util.Vector"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" scope="page" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" scope="page" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String deptKeyword = "";
String jobKeyword = "";

if (request.getParameter("deptKeyword") != null) {
	deptKeyword = request.getParameter("deptKeyword");
}
if (request.getParameter("jobKeyword") != null) {
	jobKeyword = request.getParameter("jobKeyword");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head></head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>직급·부서 리스트</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="../../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/admin.css">
<link rel="stylesheet" href="../../css/admin/adminSidebar.css">
<link rel="stylesheet" href="../../css/admin/adminHeader.css">
<link rel="stylesheet" href="../../css/admin/jobDeptList.css">
<script src="../../js/bootjs/bootstrap.js"></script>
<script src="../../js/admin/admin.js"></script>
<script src="../../js/admin/jobDeptList.js"></script>
</head>

<body>
	<%@ include file="../adminSide.jsp"%>
	<%@ include file="../adminHeader.jsp"%>

	<!-- 여기가 본문 페이지 -->
	<div class="all-wrap">
		<div class="right-align">
			<span class="menu-name">관리자 > 권한 관리 > 직급·부서 리스트</span>
		</div>
		<div class="main-wrap">
			<form name="readFrm">
				<input type="hidden" name="deptKeyword" value="<%=deptKeyword%>">
				<input type="hidden" name="jobKeyword" value="<%=jobKeyword%>">
			</form>
			<div class="job-list scrollable-table">
				<span>직급 리스트</span>
				<form action="jobDeleteProc.jsp" method="post" name="jobFrm">
					<div class="search-delete-container">
						<div class="search-delete-content">
							<div style="display: flex">
								<button type="submit" name="deptFlag" class="btn btn-danger">삭제</button>
								<button type="button" class="btn btn-primary open-job-btn">추가</button>
							</div>
							
							<div class="search-container">
								<input type="text" name="jobKeyword" placeholder="직급 검색..." class="form-control">
								<button type="button" name="jobms" value="search" class="btn btn-primary ml-2">검색</button>
							</div>
						</div>
					</div>
					<table class="table table-striped">
						<thead>
							<tr>
								<th><input type="checkbox" id="chkAllJob"></th>
								<th scope="col">직급 번호</th>
								<th scope="col">직급 명칭</th>
								<th scope="col">직급 권한 및 정보</th>
							</tr>
						</thead>
						<tbody>
						<%
						Vector<ViewPermissionBean> jobs = adminCon.getJobPermissions("name", jobKeyword);
						
						for (int i = 0; i < jobs.size(); ++i) {
							ViewPermissionBean job = jobs.get(i);
							
							int beanNum = job.getId();
							int beanId = job.getClassId();
							String beanName = job.getClassName();
						%>
							<tr>
								<td><input type="checkbox" name="jobId" value="<%=beanId%>"></td>
								<td><%=beanNum%></td>
								<td><%=beanName%></td>
								<td><a href="jobUpdate.jsp?jobId=<%=beanId%>">수정</a></td>
							</tr>
						<%
						}
						%>
						</tbody>
						<tfoot>
							<td colspan="3"></td>
							<td></td>
						</tfoot>
					</table>
				</form>
			</div>

			<div class="dept-list scrollable-table">
				<span>부서 리스트</span>
				<form action="deptDeleteProc.jsp" method="post" name="deptFrm">
					<div class="search-delete-container">
						<div class="search-delete-content">
						<div style="display: flex">
							<button type="submit" class="btn btn-danger">삭제</button>
							<button type="button" class="btn btn-primary open-dept-btn">추가</button>
						</div>
							<div class="search-container">
								<input type="text" name="deptKeyword" placeholder="부서 검색..." class="form-control">
								<input type="button" name="deptms" value="search" onclick="searchDept()"
									class="btn btn-primary ml-2">
							</div>
						</div>
					</div>
					<table class="table table-striped">
						<thead>
							<tr>
								<th><input type="checkbox" id="chkAllDept"></th>
								<th scope="col">부서 번호</th>
								<th scope="col">부서 명칭</th>
								<th scope="col">부서 권한 및 정보</th>
							</tr>
						</thead>
						<tbody>
						<%
						Vector<ViewPermissionBean> depts = adminCon.getDeptPermissions("name", deptKeyword);
						
						for (int i = 0; i < depts.size(); ++i) {
							ViewPermissionBean dept = depts.get(i);
							
							int beanNum = dept.getId();
							int beanId = dept.getClassId();
							String beanName = dept.getClassName();
						%>
							<tr>
								<td><input type="checkbox" name="deptId" value="<%=beanId%>"></td>
								<td><%=beanNum%></td>
								<td><%=beanName%></td>
								<td><a href="deptUpdate.jsp?deptId=<%=beanId%>">수정</a></td>
							</tr>
						<%
						}
						%>
						</tbody>
						<tfoot>
							<td colspan="3"></td>
							<td></td>
						</tfoot>
					</table>
				</form>
			</div>
		</div>
	</div>
	</div>
	</div>
</body>

</html>