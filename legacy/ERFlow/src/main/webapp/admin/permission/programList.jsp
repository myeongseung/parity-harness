<!-- programList.jsp -->
<%@page import="model.ProgramBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" scope="page" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" scope="page" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String keyword = "";

if (request.getParameter("keyword") != null) {
	keyword = request.getParameter("keyword");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>프로그램 리스트</title>
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
<script src="../../js/admin/programList.js"></script>
</head>
<body>
	<%@ include file="../adminSide.jsp"%>
	<%@ include file="../adminHeader.jsp"%>
	<!-- 여기가 본문 페이지 -->
	<div class="table-wrap">
		<div class="right-align">
			<span class="menu-name">관리자 > 권한 관리 > 프로그램 리스트</span>
		</div>
		<form name="searchFrm">
			<div class="search-option">
				<div class="main-search-container">
					<div class="main-search-border">
						<input class="main-search-text" type="search" name="keyword"
							placeholder="프로그램 검색" aria-label="Search">
					</div>
					<div class="main-search-icon">
						<i class="fa-solid fa-magnifying-glass fa-lg" onclick="search()"
							title="검색하기"></i>
					</div>
				</div>
			</div>
		</form>
		<table class="table table-striped">
			<thead>
				<tr>
					<th scope="col">번호</th>
					<th scope="col">프로그램ID</th>
					<th scope="col">프로그램 이름</th>
					<th scope="col">부서 권한</th>
					<th scope="col">직급 권한</th>
				</tr>
			</thead>
			<tbody>
			<%
			// Pagenation
			final int pagePerBlock = 5;
			final int numPerPage = 15;
			
			int totalRecord = adminCon.getProgramCount(session, keyword);
			int totalPage = (int)Math.ceil(1.0 * totalRecord / numPerPage);
			int totalBlock = (int)Math.ceil(1.0 * totalPage / pagePerBlock);
			int nowPage = 1;
							
			if (request.getParameter("nowPage") != null) {
				nowPage = WebHelper.parseInt(request, "nowPage");
			}
			int nowBlock = (int)Math.ceil(1.0 * nowPage / pagePerBlock);
			
			int start = (nowPage - 1) * numPerPage;
			
			Vector<ProgramBean> vlist = adminCon.getPrograms(session, keyword, start, numPerPage);
			
			for (int i = 0; i < numPerPage; ++i) {
				if (i == vlist.size()) {
					break;
				}
				ProgramBean program = vlist.get(i);
				
				int id = program.getId();
				String programId = program.getProgramId();
				String programName = program.getProgramName();
			%>
			<tr>
				<td><%=id%></td>
				<td><%=programId%></td>
				<td><%=programName%></td>
				<td><a href="programDeptUpdate.jsp?id=<%=id%>">수정</a></td>
				<td><a href="programJobUpdate.jsp?id=<%=id%>">수정</a></td>
			</tr>
			<%}%>
			</tbody>
			<tfoot>
				<td></td>
				<td colspan="2"></td>
				<td></td>
				<td></td>
			</tfoot>
		</table>
		<form name="readFrm">
			<input type="hidden" name="nowPage" value="<%=nowPage%>">
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
			<a href="javascript:paging('<%=pageStart%>')"
				<%=(pageStart == nowPage) ? "class=\"disabled\"" : ""%>>
				<li><%=pageStart%></li>
			</a>
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
</body>
</html>