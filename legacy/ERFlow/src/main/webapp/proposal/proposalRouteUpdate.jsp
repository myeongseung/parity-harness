<!-- proposalRouteUpdate.jsp -->
<%@page import="java.util.Vector"%>
<%@page import="model.view.ViewUserBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="proposalRouteCon" class="controller.ProposalRouteController" />
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="bean" class="model.view.ViewProposalRouteBean" />
<%
final String PROGRAM_CODE = "C6409ACB532D53B2C7F4065039A8E7FED426810370BCD39330526F662A115A72";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || 
		!permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
int id = WebHelper.parseInt(request, "id");

bean = proposalRouteCon.getProposalRouteView(id);

if (bean == null) {
	return;
}

String nickname = bean.getNickname();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>결재라인 수정 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/proposal/proposalRegister.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/proposal/proposalRegister.js"></script>
</head>
<body>
	<%@ include file="/indexHeader.jsp"%>
	<div class="content-wrap">
		<%@ include file="/indexSide.jsp"%>
		<!-- 여기가 본문 페이지 -->
		<div class="proposal-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > 전자결재 > 결재라인 수정 페이지</span>
			</div>
			<div class="proposal-insert">
				<div class="proposal-form">
					<form action="proposalRouteUpdateProc.jsp" method="post"
						name="proposalFrm">
						<div class="input-group mb-3">
						  <div class="input-group-prepend">
						    <span class="input-group-text" id="basic-addon1">결재경로 ID:</span>
						  </div>
						  <input type="text" class="form-control" id="proposalId" placeholder="결재경로 ID" aria-label="proposalId" aria-describedby="basic-addon1"
						  name="proposalId" value="<%=id %>">
						</div>
						<div class="input-group mb-3">
						  <div class="input-group-prepend">
						    <span class="input-group-text" id="basic-addon1">결재명:</span>
						  </div>
						  <input type="text" class="form-control" id="proposalName" placeholder="결재명" aria-label="nickname" aria-describedby="basic-addon1"
						  name="nickname" value="<%=nickname %>">
						</div>
						<div class="form-group d-grid gap-2">
							<table id="myTable" class="table table-striped">
								<tr>
									<td>No</td>
									<td>이름</td>
									<td>사번</td>
									<td>직급</td>
								</tr>
								<%
								Vector<ViewUserBean> vlist = bean.getRoute();
								for (int i = 0; i < (vlist != null ? vlist.size() : 0); ++i) {
									if (i == vlist.size()) {
										break;
									}
									ViewUserBean routeUser = activityCon.getUserView(vlist.get(i).getId());

									String routeUserId = routeUser.getId();
									String routeUserName = routeUser.getName();
									String routeUserJob = routeUser.getJobName();
								%>
								<tr>
									<td><%=i + 1%></td>
									<td><%=routeUserName%></td>
									<td><%=routeUserId%> <input type="hidden" name="routeId"
										value="<%=routeUserId%>"></td>
									<td><%=routeUserJob%></td>
								</tr>
								<%
								}
								%>
							</table>
						</div>
						<div class="form-group text-center">
							<button type="submit" class="form-Register-btn">제출</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
