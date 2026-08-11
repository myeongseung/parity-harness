<%@page import="helper.WebHelper"%>
<%@page import="model.view.ViewUserBean"%>
<%@page import="java.util.Vector"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
//검색 기능 지원
String keyfield = "";
String keyword = "";

// 만약 사용자를 검색하려고 하는 시도가 있다면
if (request.getParameter("keyfield") != null) {
	keyfield = request.getParameter("keyfield");
	keyword = request.getParameter("keyword");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>유저 리스트 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/admin.css">
<link rel="stylesheet" href="../../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../../css/admin/adminSidebar.css">
<link rel="stylesheet" href="../../css/admin/adminHeader.css">
<link rel="stylesheet" href="../../css/admin/userList.css">
<script src="../../js/bootjs/bootstrap.js"></script>
<script src="../../js/admin/admin.js"></script>
<script src="../../js/admin/userListAddress.js"></script>
<script src="../../js/admin/userList.js"></script>
</head>
<body>
	<%@ include file="../adminSide.jsp"%>
	<%@ include file="../adminHeader.jsp"%>
	<!-- 여기가 본문 페이지 -->
	<div class="table-wrap">
		<div class="right-align">
			<span class="menu-name">관리자 > 사원 > 사원관리</span>
		</div>
		<form name="searchFrm">
			<div class="search-option">
				<select name="keyfield">
					<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체 조회</option>
					<option value="id" <%=keyfield.equals("id") ? "selected" : ""%>>사번</option>
					<option value="name" <%=keyfield.equals("name") ? "selected" : ""%>>이름</option>
					<option value="dept_name" <%=keyfield.equals("dept_name") ? "selected" : ""%>>부서</option>
					<option value="job_name" <%=keyfield.equals("job_name") ? "selected" : ""%>>직급</option>
				</select>
				<div class="main-search-container">
					<div class="main-search-border">
						<input class="main-search-text" type="search" name="keyword"
							placeholder="검색" aria-label="Search" value="<%=keyword%>">
					</div>
					<div class="main-search-icon">
						<i class="fa-solid fa-magnifying-glass fa-lg" title="검색하기"></i>
					</div>
				</div>
			</div>
		</form>
		<table class="table table-striped">
			<thead>
				<tr>
					<th scope="col">사번</th>
					<th scope="col">이름</th>
					<th scope="col">주민등록번호</th>
					<th scope="col">성별</th>
					<th scope="col">내·외국인</th>
					<th scope="col">부서</th>
					<th scope="col">직급</th>
					<th scope="col">내선 번호</th>
					<th scope="col">개인 번호</th>
					<th scope="col">이메일</th>
					<th scope="col">입사일</th>
					<th scope="col">주소</th>
				</tr>
			</thead>
			<tbody>
				<%
				// Pagenation
				final int pagePerBlock = 5;
				final int numPerPage = 15;
								
				int totalRecord = adminCon.getUserTotalCount(keyfield, keyword);
				int totalPage = (int)Math.ceil(1.0 * totalRecord / numPerPage);
				int totalBlock = (int)Math.ceil(1.0 * totalPage / pagePerBlock);
				int nowPage = 1;
								
				if (request.getParameter("nowPage") != null) {
					nowPage = WebHelper.parseInt(request, "nowPage");
				}
				int nowBlock = (int)Math.ceil(1.0 * nowPage / pagePerBlock);
				
				int start = (nowPage - 1) * numPerPage;
				
				Vector<ViewUserBean> users = adminCon.getUserViews(keyfield, keyword, start, numPerPage);

				// 사용자 정보 출력
				for (int i = 0; i < numPerPage; ++i) {
					if (i == users.size()) {
						break;
					}
					ViewUserBean user = users.get(i);

					String id = user.getId();
					String name = user.getName();
					String socialNumber = user.getSocialNumber();
					String gender = user.getGender();
					String region = user.getRegion();
					String deptName = user.getDeptName();
					String jobName = user.getJobName();
					int salary = user.getSalary();
					String address = user.getAddress2();
					String extPhone = user.getExtensionPhone();
					String mobilePhone = user.getMobilePhone();
					String email = user.getEmail();
					String hiredAt = user.getHiredAt();
				%>
				<tr>
					<td><a href="userUpdate.jsp?id=<%=id%>"><%=id %></a></td>
					<td><%=name != null ? name : ""%></td>
					<td><%=socialNumber != null ? socialNumber : ""%></td>
					<td><%=gender != null ? gender : ""%></td>
					<td><%=region != null ? region : ""%></td>
					<td><%=deptName != null ? deptName : ""%></td>
					<td><%=jobName != null ? jobName : ""%></td>
					<td><%=extPhone != null ? extPhone : ""%></td>
					<td><%=mobilePhone != null ? mobilePhone : ""%></td>
					<td><%=email != null ? email : ""%></td>
					<td><%=hiredAt%></td>
					<td><a href="javascript:void(0);"
						onclick="openNewWindow('<%=id%>')">보기</a></td>
				</tr>
				<%
				}
				%>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="12"></td>
				</tr>
			</tfoot>
		</table>
		<form name="readFrm">
			<input type="hidden" name="nowPage" value="<%=nowPage%>">
			<input type="hidden" name="keyfield" value="<%=keyfield%>">
			<input type="hidden" name="keyword" value="<%=keyword%>">
		</form>
	</div>

	<!-- 페이징 -->
	<div class="page-controller">
		<%
	 	if (nowBlock > 1) {
	 	%>
		<a href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock - 1%>')" class="disabled">이전</a>
		<%
	 	}
	 	int pageStart = (nowBlock - 1) * pagePerBlock + 1;
	 	int pageEnd = pageStart + pagePerBlock;
	 	pageEnd = pageEnd <= totalPage ? pageEnd : totalPage + 1;
	 	%>
		<ul class="page-numbers">
			<%
	 	for ( ; pageStart < pageEnd; ++pageStart) {
 		%>
 			<li>
				<a href="javascript:paging('<%=pageStart%>')" <%=(pageStart == nowPage) ? "class=\"disabled\"" : ""%>>
					<%=pageStart%>
				</a>
			</li>
			<% 	
	 	}
	 	%>
		</ul>
		<%
		if (nowBlock < totalBlock) {
		%>
		<a href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock + 1%>')">다음</a>
		<%
	 	}
	 	%>
	</div>
	</div>
	</div>
</body>
</html>