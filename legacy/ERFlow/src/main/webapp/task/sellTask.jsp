<%@page import="repository.TaskRepository"%>
<%@page import="model.view.ViewTaskBean"%>
<%@page import="model.TaskBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page import="model.UserBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="taskCon" class="controller.TaskController" />
<%
final String PROGRAM_CODE = "5D2B70044459C29621CC45B55113F427F933BDE9D43404D387ED8585EFE64AD9";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || 
		!permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
// 검색 기능 지원
String keyfield = "";
String keyword = "";

// 만약 사용자를 검색하려고 하는 시도가 있다면
if (request.getParameter("keyfield") != null) {
	keyfield = request.getParameter("keyfield");
	System.out.println("Gd");
	keyword = request.getParameter("keyword");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>수주 관리 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="../css/bootcss/bootstrap.css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/task.css" />
<script src="../js/main/index.js"></script>
<script src="../js/main/task.js"></script>
<script src="../js/bootjs/bootstrap.js"></script>
</head>

<body>
	<div class="wrap">
		<%@include file="../indexHeader.jsp"%>
		<!-- indexHeader -->

		<!--  본문  -->
		<div class="content-wrap">
			<%@include file="../indexSide.jsp"%>
			<!-- indexSide -->

			<!--본문 내용 작성----------------------------------------------------------------->
			<div class="table-wrap">
				<div class="right-align">
					<span class="menu-name">사용자 > 영업 > 수주 관리</span>
				</div>

				<form action="taskDeleteProc.jsp" method="post" name="taskFrm">
					<input type="hidden" name="flag" value="sell">
					<div class="table-header-container">
						<div class="button-group">
							<button type="button" class="btn btn-danger" id="delete-button"
								name="sell" value="delete">삭제</button>
							<button type="button" class="btn btn-primary"
								id="register-button" data-value="sell">추가</button>

						</div>
						<div class="search-option">
							<select name="keyfield" id="">
								<option value="" <%= keyfield.equals("") ? "selected" : ""%>>전체조회</option>
								<option value="userName" <%= keyfield.equals("userName") ? "selected" : ""%>>직원명</option>
								<option value="deptName" <%= keyfield.equals("deptName") ? "selected" : ""%>>부서명</option>
								<option value="companyName" <%= keyfield.equals("companyName") ? "selected" : ""%>>회사명</option>
								<option value="date" <%= keyfield.equals("date") ? "selected" : ""%>>날짜</option>
								<option value="status" <%= keyfield.equals("status") ? "selected" : ""%>>상태</option>
							</select>

							<div class="main-search-container">
								<div class="main-search-border">
									<input class="main-search-text" type="search" placeholder="검색"
										aria-label="Search" name="keyword" value="<%=keyword%>">
								</div>
								<div class="main-search-icon">
									<i class="fa-solid fa-magnifying-glass fa-lg" title="검색하기"></i>
								</div>
							</div>
						</div>
					</div>


					<table class="table table-striped">
						<thead>
							<tr>
								<th scope="col"><input type="checkbox" id="chkAll"></th>
								<th scope="col">번호</th>
								<th scope="col">담당직원</th>
								<th scope="col">부서</th>
								<th scope="col">회사명</th>
								<th scope="col">문서</th>
								<th scope="col">수주 시각</th>
								<th scope="col">상태</th>
								<th scope="col">보기</th>
							</tr>
						</thead>
						<tbody>
							<%
							// Pagenation

							final int pagePerBlock = 5;
							final int numPerPage = 15;

							int totalRecord = taskCon.getTaskCount(keyfield, keyword, 0);
							int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
							int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
							int nowPage = 1;

							if (request.getParameter("nowPage") != null) {
								nowPage = WebHelper.parseInt(request, "nowPage");
							}
							int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

							int start = (nowPage - 1) * numPerPage;

							Vector<ViewTaskBean> tasks = taskCon.getTasks(keyfield, keyword, start, numPerPage, 0);
							int cnt = numPerPage > tasks.size() ? numPerPage : tasks.size();
							// 수주 정보 출력
							for (int i = 0; i < cnt; ++i) {
								if (i == tasks.size())
									break;
								ViewTaskBean task = tasks.get(i);

								int id = task.getId();
								String userName = task.getUserName();
								String deptName = task.getDeptName();
								String companyName = task.getCompanyName();
								String subject = task.getSubject();
								int type = task.getType();
								String taskAt = task.getTaskAt();
								int status = task.getStatus();
							%>
							<tr>
								<th scope="col"><input type="checkbox" name="taskId"
									value="<%=id%>"></th>
								<td><a
									href="taskUpdate.jsp?id=<%=id%>&flag=sell&userName=<%=userName%>&companyName=<%=companyName%>"><%=id%></a></td>
								<td><%=userName%></td>
								<td><%=deptName%></td>
								<td><%=companyName%></td>
								<td><%=subject%></td>
								<td><%=taskAt%></td>
								<td><%=TaskRepository.getInstance().getTaskStatusCode(status)%></td>
								<td>
									<button class="openModalBtn" type="button" value=<%=id %>>내역 보기</button>
								</td>
							</tr>
							<%
							}
							%>
						</tbody>
						<tfoot>
							<td colspan="8"></td>
							<td></td>
						</tfoot>
					</table>

				</form>
				<form name="readFrm">
					<input type="hidden" name="nowPage" value="<%=nowPage%>"> <input
						type="hidden" name="keyfield" value="<%=keyfield%>"> <input
						type="hidden" name="keyword" value="<%=keyword%>">
				</form>

				<!-- 페이징 -->
				<div class="page-controller">
					<%
					if (nowBlock > 1) {
					%>
					<a
						href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock - 1%>')"
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
						<li>
						<a href="javascript:paging('<%=pageStart%>')"
							<%=(pageStart == nowPage) ? "class=\"disabled\"" : ""%>>
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
					<a
						href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock + 1%>')">다음</a>
					<%
					}
					%>
				</div>
			</div>
		</div>
</body>
</html>