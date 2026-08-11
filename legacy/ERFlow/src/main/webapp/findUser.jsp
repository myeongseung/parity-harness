<!-- .jsp -->
<%@page import="model.JobBean"%>
<%@page import="model.DepartmentBean"%>
<%@page import="model.view.ViewUserBean" %>
<%@page import="java.util.Vector"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<%


String deptKeyfield = "";
String jobKeyfield = "";
String keyword = "";

if (request.getParameter("deptKeyfield") != null){
	deptKeyfield = request.getParameter("deptKeyfield");
}
if (request.getParameter("jobKeyfield") != null){
	jobKeyfield = request.getParameter("jobKeyfield");
}
if (request.getParameter("keyword") != null){
	keyword = request.getParameter("keyword");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Document</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="css/common/common.css">
<link rel="stylesheet" href="css/common/page.css">
<link rel="stylesheet" href="css/main/header.css">
<link rel="stylesheet" href="css/main/index.css">
<link rel="stylesheet" href="css/main/aside.css">
<script src="js/bootjs/bootstrap.js"></script>
<link rel="stylesheet" href="css/bootcss/bootstrap.css">
<script src="js/admin/admin.js"></script>
<script src="js/main/findUser.js"></script>
<link rel="stylesheet" href="css/main/findUser.css">
</head>
<body>

	<div class="search-message-wrap">
		<div class="search-message-body">
			<div class="search-select-option-wrap">
				<div class="search-select-option">
					<form method="get" action="" name="searchFrm">
						<!-- 부서, 직급 선택후 제출하는 폼 -->
						<select class="search-people-dept" name="deptKeyfield">
							<option value="">전체부서</option>
							<%
							Vector<DepartmentBean> depts = activityCon.getDepartments(null);

							for (int i = 0; i < depts.size(); ++i) {
								DepartmentBean dept = depts.get(i);

								int id = dept.getId();
								String name = dept.getName();
							%>
							<option value="<%=name%>" <%=deptKeyfield.equals(name) ? "selected" : ""%>><%=name%></option>
							<%
							}
							%>
						</select> 
						<select class="search-people-job" name="jobKeyfield">
							<option value="">전체직급</option>
							<%
							Vector<JobBean> jobs = activityCon.getJobs(null);

							for (int i = 0; i < jobs.size(); ++i) {
								JobBean job = jobs.get(i);

								int id = job.getId();
								String name = job.getName();
							%>
							<option value="<%=name%>" <%=jobKeyfield.equals(name) ? "selected" : ""%>><%=name%></option>
							<%
							}
							%>
						</select>
						<input type="text" name="keyword" value="<%=keyword%>">
						<button class="btn btn-secondary" type="submit">찾기</button>
					</form>
				</div>
				<form method="post" name="search-people-body" id="selectFrm">
				<input type="hidden" name="receivers">
					<!-- 쪽지 보내기 원하는 사람 선택 -->
					<div class="search-button">

						<button class="btn btn-primary" type="submit">선택</button>
					</div>
			</div>



			<hr>
			<table class="search-people-body table">
				<thead>
					<tr>
						<th><input type="checkbox" id="chkAll"></th>
						<th>사번</th>
						<th>이름</th>
						<th>부서</th>
						<th>직급</th>
					</tr>
				</thead>

				<tbody>
				<%
				Vector<ViewUserBean> users = activityCon.getUserViews(deptKeyfield, jobKeyfield, keyword);
				
				for (int i = 0; i < users.size(); ++i) {
					ViewUserBean user = users.get(i);

					String id = user.getId();
					String name = user.getName();
					String deptName = user.getDeptName();
					String jobName = user.getJobName();
				%>
				<tr>
					<td class="search-people-body-check"><input name="userId" type="checkbox" value="<%=id%>"></td>
					<td class="search-people-body-receiver-id"><%=id%></td>
					<td class="search-people-body-receiver"><%=name%></td>
					<td class="search-people-body-dept"><%=deptName%></td>
					<td class="search-people-body-job"><%=jobName%></td>
				</tr>
				<%
				}
				%>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="5">made by 김시찬</td>
					</tr>
				</tfoot>
			</table>
			</form>
		</div>
	</div>
</body>
</html>