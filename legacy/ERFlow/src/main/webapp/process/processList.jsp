<!-- processList.jsp -->
<%@page import="model.ProcessBean"%>
<%@page import="java.util.Vector"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="processCon" class="controller.ProcessController"/>
<%
final String PROGRAM_CODE = "F03BEAC83BFF5F1D13D55F26C10040BED42E0C5BBAC8EC850574D03C13AE8CFF";

if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

//검색 기능 지원
String keyfield = "";
String keyword = "";

//만약 사용자를 검색하려고 하는 시도가 있다면
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
<title>공정 관리 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/process/processList.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/process/processList.js"></script>
</head>
<body>
	<!-- 여기까지 -->
	<%@include file="/indexHeader.jsp"%>
	<!-- indexHeader -->

	<!--  본문  -->
	<div class="content-wrap">
		<%@include file="/indexSide.jsp"%>
		<!-- indexSide -->
		<div class="table-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > 생산관리 > 공정 관리</span>
			</div>

			<form action="processDeleteProc.jsp" method="post" name="processFrm">

				<div class="table-header-container">
					<div class="button-group">
						<button type="button" class="btn btn-danger" id="delete-button" name="flag" value="delete">삭제</button>
						<button type="button" class="btn btn-primary" id="register-button">추가</button>
					</div>
					<div class="search-option">
						<select name="keyfield" id="">
							<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
							<option value="id" <%=keyfield.equals("id") ? "selected" : ""%>>현재
								공정ID</option>
							<option value="name"
								<%=keyfield.equals("name") ? "selected" : ""%>>공정명</option>
							<option value="priority"
								<%=keyfield.equals("priority") ? "selected" : ""%>>우선
								순위</option>
						</select>
						<div class="main-search-container">
							<div class="main-search-border">
								<input class="main-search-text" type="search"
									placeholder="공정 검색" aria-label="Search" name="keyword">
							</div>
							<div class="main-search-icon">
								<i class="fa-solid fa-magnifying-glass fa-lg" id="searchIcon"
									title="검색하기"></i>
							</div>
						</div>
					</div>
				</div>

				<table class="table table-striped">
					<thead>
						<tr>
							<th scope="col"><input type="checkbox" id="chkAll"></th>
							<th scope="col">이전 공정ID</th>
							<th scope="col">현재 공정ID</th>
							<th scope="col">다음 공정ID</th>
							<th scope="col">현재 공정명</th>
							<th scope="col">우선순위</th>
						</tr>
					</thead>
					<tbody>
						<%
						// Pagenation

						final int pagePerBlock = 5;
						final int numPerPage = 15;

						int totalRecord = processCon.processesCount(keyfield, keyword);
						int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
						int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
						int nowPage = 1;

						if (request.getParameter("nowPage") != null) {
							nowPage = WebHelper.parseInt(request, "nowPage");
						}
						int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

						int start = (nowPage - 1) * numPerPage;

						Vector<ProcessBean> processes = processCon.getProcesses(keyfield, keyword, start, numPerPage);
						int cnt = numPerPage > processes.size() ? numPerPage : processes.size();
						// 원재료 정보 출력
						for (int i = 0; i < cnt; ++i) {
							if (i == processes.size())
								break;
							ProcessBean process = processes.get(i);
							String id = process.getId();
							String prevId = process.getPrevId();
							String nextId = process.getNextId();
							String name = process.getName();
							int priority = process.getPriority();
						%>
						<tr>
							<th scope="col"><input type="checkbox" name="processId"
								value="<%=id%>"></th>
							<td><%=prevId != null ? prevId : ""%></td>
							<td><%=id%></td>
							<td><%=nextId != null ? nextId : ""%></td>
							<td><a href="processUpdate.jsp?id=<%=id%>&name=<%=name%>" target="_blank" onclick="window.open(this.href, 'myWindow', 'width=600, height=400'); return false;"><%=name%></a></td>
							<td><%=priority%></td>
						</tr>
						<%
						}
						%>
					</tbody>
					<tfoot>
						<td colspan="5"></td>
						<td></td>
					</tfoot>
				</table>
			</form>

			<form name="readFrm">
				<input type="hidden" name="nowPage" value="<%=nowPage%>"> <input
					type="hidden" name="keyfield" value=""> <input
					type="hidden" name="keyword" value="">
			</form>

			<!-- 페이징 -->
			<div class="page-controller">
				<%
				if (nowBlock > 1) {
				%>
				<a href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock - 1%>')"
					class="disabled">이전</a>
				<%
				}
				int pageStart = (nowBlock - 1) * pagePerBlock + 1;
				int pageEnd = pageStart + pagePerBlock;
				pageEnd = pageEnd <= totalPage ? pageEnd : totalPage + 1;
				%>
				<ul class="page-numbers">
					<%
					for (; pageStart < pageEnd; ++pageStart) {
					%>
					<li><a href="javascript:paging('<%=pageStart%>')"
						<%=(pageStart == nowPage) ? "class=\"disabled\"" : ""%>> <%=pageStart%>
					</a></li>
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