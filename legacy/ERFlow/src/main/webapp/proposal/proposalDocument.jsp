<!-- proposalDocument.jsp -->
<%@page import="java.util.Optional"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="model.view.ViewProposalBean"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.util.Base64"%>
<%@page import="controller.UserController"%>
<%@page import="model.view.ViewUserBean"%>
<%@page import="java.util.Vector"%>
<%@page import="controller.PermissionController"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="model.UserBean"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="proposalCon" class="controller.ProposalController" />
<jsp:useBean id="userCon" class="controller.UserController" />
<%
final String PROGRAM_CODE = "53A5157A4BAB5F1B75921C9B42888D7E1539466CD3DD258C1E318F29B62EC586";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || 
		!permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
boolean isValid = true;
long proposalId = -1;

if (request.getParameter("proposalId") != null) {
	try {
		proposalId = WebHelper.parseLong(request, "proposalId");
	} catch (NumberFormatException e) {
		isValid = false;
	}
}
ViewProposalBean bean = proposalCon.getProposalView(proposalId);

isValid = bean != null;

if (!isValid) {
	response.sendRedirect("../accessError.jsp");
	return;	
}

Vector<ViewProposalBean> history = proposalCon.getProposalViews(bean.getDocumentId());
int result = bean.getResult();
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
SimpleDateFormat targetSdf = new SimpleDateFormat("MM/dd");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>결재 문서 보기</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap2.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/proposal/proposalDocument.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/proposal/proposalDocument.js"></script>
<script src="../js/main/index.js"></script>

</head>
<body>
	<%@include file="/indexHeader.jsp"%>
	<div class="content-wrap">
		<%@include file="/indexSide.jsp"%>
		<div class="proposal-insert-wrap">
			<div class="proposal-left-wrap">
				<div class="proposal-left-header"></div>
				<div class="proposal-left-content">
					<div class="page">
						<div class="subpage">
							<div class="approval-header">
								<div class="approval-title">
									<span class="approval-title-span"><%=bean.getSubject() %></span>
								</div>
								<div class="approval-wrap">
									<table class="approval-table">
										<tr>
											<th rowspan="4">결<br>재<br>라<br>인
											</th>
											<td>담당</td>
											<%
											String[] routes = bean.getRoute().split(";");
											int routeSize = routes.length;
											
											for (int i = 1; i < routeSize - 1; ++i) {
											%>
											<td>검토</td>
											<%
											}
											%>
											<td>승인</td>
										</tr>
										<tr>
											<%
											for (int i = 0; i < routeSize; ++i) {
												if (i < history.size()) {
													ViewProposalBean proposal = history.get(i);
													String name = userCon.getUser(routes[i]).getName();
													boolean isApproved = proposal.getApprovedAt() != null;
											%>
												<td rowspan="2" class="proposal-stamp"><%=isApproved ? name : ""%></td>
											<%
												} else {
											%>
												<td rowspan="2">&nbsp;&nbsp;</td>
											<%
												}
											}
											%>
										</tr>
										<tr></tr>
										<tr>
											<%
											for (int i = 0; i < routeSize; ++i) {
												if (i < history.size()) {
													ViewProposalBean proposal = history.get(i);
													String approvedAt = proposal.getApprovedAt();
													boolean isApproved = approvedAt != null;
													
													if (isApproved) {
														approvedAt = targetSdf.format(sdf.parse(approvedAt));
													}
											%>
												<td><%=isApproved ? approvedAt : ""%></td>
											<%
												} else {
											%>
												<td>&nbsp;&nbsp;</td>
											<%	
												}
											}
											%>
										</tr>
									</table>
								</div>
							</div>
							<hr>
							<div class="approval-content">
								<!-- 문서 가져오기 -->
								<% out.println(Optional.ofNullable(bean.getContent()).orElse("")); %>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="proposal-right-wrap">
				<div class="proposal-comment-list">
					<table class="table table-striped">
						<thead>
							<tr>
								<th class="comment-name">결재자</th>
								<th class="comeent-content">코멘트</th>
							</tr>
						</thead>
						<tbody>
							<%
								for (ViewProposalBean proposal : history) {
									String comment = Optional.ofNullable(proposal.getComment()).orElse("");
									String userName = userCon.getUser(proposal.getUserId()).getName();
									
									if (!comment.trim().equals("")) {
							%>
							<tr>
								<td class="comment-name"><%=userName%></td>
								<td class="comment-content"><%=comment%></td>
							</tr>
							<%
									}
								}
							%>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="4"></td>
							</tr>
						</tfoot>
					</table>
				</div>
				<form action="proposalDocumentProc.jsp" method="post" name="proposalFrm">
					<input type="hidden" id="proposalId" name="proposalId"
						value="<%=bean.getId()%>"> <input type="hidden" id="step"
						name="step" value="<%=bean.getStep()%>">
					<div class="input-group"
						style="<%= result == 3 || result == 0 ? "" : "display:none;" %>">
						<div class="input-group-prepend">
							<span class="input-group-text">코멘트</span>
						</div>
						<textarea class="form-control" aria-label="With textarea"
							name="comment"></textarea>
					</div>
					<div class="button-group"
						style="<%= result == 3 || result == 0  ? "" : "display:none;" %>">
						<input type="hidden" value="" name="result">
						<button class="btn btn-primary confirm-button" type="button">승인</button>
						
						<% if (bean.getStep() != 0) { %>
						<button class="btn btn-danger reject-button" type="button">반려</button>
						<% } %>
					</div>
				</form>

			</div>
		</div>
	</div>
</body>
</html>