<!-- proposalRouteList.jsp -->
<%@page import="model.view.ViewUserBean"%>
<%@page import="model.view.ViewProposalRouteBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="model.view.ViewUnitBean"%>
<%@page import="java.util.Vector"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="proposalRouteCon" class="controller.ProposalRouteController"/>
<%
final String PROGRAM_CODE = "C6409ACB532D53B2C7F4065039A8E7FED426810370BCD39330526F662A115A72";

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
	keyword = request.getParameter("keyword");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>결재 라인 관리</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/proposal/proposalList.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/admin/admin.js"></script>
<script src="../js/proposal/proposalRoute.js"></script>
</head>
<body>
	<!-- 여기까지 -->

	<%@include file="/indexHeader.jsp"%>
	<!-- indexHeader -->

	<!--  본문  -->
	<div class="content-wrap">
		<%@include file="/indexSide.jsp"%>
		<!-- indexSide -->

		<!-- 여기가 본문 페이지 -->
		<div class="table-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > 전자결재 > 결재라인 관리</span>
			</div>

			<form action="proposalRouteDeleteProc.jsp" method="post" name="proposalFrm">
				<div class="table-header-container">
					<div class="button-group">
						<button type="button" class="btn btn-danger" id="delete-button" name="flag" value="delete">삭제</button>
						<button type="button" class="btn btn-primary" id="register-button">생성</button>
					</div>
					<div class="search-option">
						<select name="keyfield" id="">
							<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
							<option value="id" <%=keyfield.equals("id") ? "selected" : ""%>>문서번호</option>
							<option value="route"
								<%=keyfield.equals("route") ? "selected" : ""%>>결재경로</option>
						</select>
						<div class="main-search-container">
							<div class="main-search-border">
								<input class="main-search-text" type="search"
									placeholder="결재 검색" aria-label="Search" name="keyword">
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
							<th scope="col" style="width: 5%"><input type="checkbox" id="chkAll"></th>
							<th scope="col" style="width: 8%">결재관리번호</th>
							<th scope="col" style="width: 8%">결재라인명</th>
							<th scope="col">결재경로</th>
							<th scope="col" style="width: 12%">생성 시간</th>
						</tr>
					</thead>
					<tbody>
						<%
						// Pagenation

						final int pagePerBlock = 5;
						final int numPerPage = 15;

						int totalRecord = proposalRouteCon.getProposalRoutesCount(keyfield, keyword, user.getId());
						int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
						int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
						int nowPage = 1;

						if (request.getParameter("nowPage") != null) {
							nowPage = WebHelper.parseInt(request, "nowPage");
						}
						int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

						int start = (nowPage - 1) * numPerPage;

						Vector<ViewProposalRouteBean> proposals = proposalRouteCon.getProposalRouteViews(keyfield, keyword, start, numPerPage,
								user.getId());
						int cnt = numPerPage > proposals.size() ? numPerPage : proposals.size();

						// 결재 정보 출력
						for (int i = 0; i < cnt; ++i) {
							if (i == proposals.size()) {
								break;
							}
							ViewProposalRouteBean proposal = proposals.get(i);

							int id = proposal.getId();
							String userName = proposal.getUserName();
							String nickname = proposal.getNickname();
							String createdAt = proposal.getCreatedAt();

							Vector<ViewUserBean> users = proposal.getRoute();
							String route = String.join(" -> ", users.stream().map(mapper -> String.format("[%s/%s] %s(%s)",
							mapper.getDeptName(), mapper.getJobName(), mapper.getName(), mapper.getId())).toList());
						%>
						<tr>
							<th scope="col" class="proposal-id"><input type="checkbox" name="proposalId"
								value="<%=id%>"></th>
							<td><a href="proposalRouteUpdate.jsp?id=<%=id%>"><%=id%></a></td>
							<td><%=nickname%></td>
							<td><%=route%></td>
							<td><%=createdAt %></td>
						</tr>
						<%
						}
						%>
					</tbody>
					<tfoot>
						<td colspan="4"></td>
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
	</div>
</body>
</html>