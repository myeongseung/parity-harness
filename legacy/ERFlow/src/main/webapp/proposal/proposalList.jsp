<!-- proposalRouteList.jsp -->
<%@page import="model.view.ViewProposalBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.Vector"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="proposalCon" class="controller.ProposalController"/>
<%
final String PROGRAM_CODE = "53A5157A4BAB5F1B75921C9B42888D7E1539466CD3DD258C1E318F29B62EC586";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || 
		!permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
int keyfield = 3;

if (request.getParameter("keyfield") != null) {
	keyfield = WebHelper.parseInt(request, "keyfield");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>결재 관리</title>
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
<script src="../js/proposal/proposal.js"></script>
<script src="../js/main/index.js"></script>
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
				<span class="menu-name">사용자 > 전자결재 > 결재 리스트</span>
			</div>

			<form action="" method="post"
				name="proposalFrm">

				<div class="table-header-container">
				<div class="button-group">
						<button type="button" class="btn btn-primary" id="register-button">생성</button>
					</div>
					<div class="search-option">
						<select name="keyfield" id="" class="selectbox">
							<option value="3" <%=keyfield == 3 ? "selected" : ""%>>결재진행중</option>
							<option value="1" <%=keyfield == 1 ? "selected" : ""%>>승인</option>
							<option value="2" <%=keyfield == 2 ? "selected" : ""%>>반려</option>
						</select>
						<div class="main-search-container">
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
							<th scope="col">문서번호</th>
							<th scope="col">문서제목</th>
							<th scope="col">문서보기</th>
							<th scope="col">생성날짜</th>
						</tr>
					</thead>
					<tbody>
						<%
						// Pagenation
						final int pagePerBlock = 5;
						final int numPerPage = 15;

						int totalRecord = proposalCon.getProposalViewsCount(user.getId(), keyfield);
						int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
						int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
						int nowPage = 1;
						
						if (request.getParameter("nowPage") != null) {
							nowPage = WebHelper.parseInt(request, "nowPage");
						}
						
						int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

						int start = (nowPage - 1) * numPerPage;

						Vector<ViewProposalBean> proposals = proposalCon.getProposalViews(user.getId(), keyfield, start, numPerPage);
						int cnt = numPerPage > proposals.size() ? numPerPage : proposals.size();
						// 결재 정보 출력
						for (int i = 0; i < proposals.size(); ++i) {
							if (i == proposals.size()) {
								break;
							}
							ViewProposalBean proposal = proposals.get(i);

							long id = proposal.getId();
							long documentId = proposal.getDocumentId();
							String subject = proposal.getSubject();
							String date = proposal.getReceivedAt();
						%>
						<tr>
							<th scope="col" class="proposal-id"><input type="checkbox" name="proposalId"
								value="<%=id%>"></th>
							<td><%=id%></td>
							<td><%=documentId%></td>
							<td><%=subject%></td>
							<td><a href="proposalDocument.jsp?proposalId=<%=id%>">문서 보기</a></td>
							<td><%=date %></td>
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
			<input type="hidden" name="nowPage" value="<%=nowPage%>">
				<input type="hidden" name="keyfield" value=""> 
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