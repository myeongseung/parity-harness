<!-- proposalRouteRegister.jsp -->
<%@page import="java.net.URLDecoder"%>
<%@page import="java.util.Base64"%>
<%@page import="controller.UserController"%>
<%@page import="model.view.ViewUserBean"%>
<%@page import="java.util.Vector"%>
<%@page import="controller.PermissionController"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="model.UserBean"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<%
final String PROGRAM_CODE = "C6409ACB532D53B2C7F4065039A8E7FED426810370BCD39330526F662A115A72";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || 
		!permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String userId = user.getId();
String receiver = "";
//Vector<String> receivers = new Vector<String>();
String[] receivers = null;
String nickname = "";
String routeUserNames = "";

if (request.getParameter("receiver") != null) {
	String encoded = request.getParameter("receiver");
	receiver = URLDecoder.decode(new String(Base64.getDecoder().decode(encoded)), "UTF-8");
	receivers = receiver.split(";");
	nickname = request.getParameter("nickname");
}

if (receivers != null) {
	for (String id : receivers) {
		routeUserNames += activityCon.getUserView(id).getName() + " ";
	}
}

if (receivers == null) {
	receivers = new String[0];
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>결재 생성</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/proposal/proposalRegister.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/proposal/proposalRouteRegister.js"></script>
<script src="../js/main/index.js"></script>

</head>
<body>
	<%@include file="/indexHeader.jsp"%>
	<div class="content-wrap">
		<%@include file="/indexSide.jsp"%>
		<div class="proposal-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > 전자결재 > 결재라인 등록</span>
			</div>
			<div class="proposal-insert">
				<div class="proposal-form">
					<form action="proposalRouteRegisterProc.jsp" method="post" name="proposalFrm">
						<input type="hidden" id="userId" name="userId" value="<%=userId%>">
						
						<div class="input-group mb-3">
						  <div class="input-group-prepend">
						    <span class="input-group-text" id="basic-addon1">결재명:</span>
						  </div>
						  <input type="text" class="form-control" id="proposalName" placeholder="결재명" aria-label="nickname" aria-describedby="basic-addon1"
						  name="nickname" value="<%=nickname %>">
						</div>
						<div class="input-group mb-3">
						  <div class="input-group-prepend">
						    <span class="input-group-text" id="basic-addon1">결재라인:</span>
						  </div>
						  <input type="text" class="form-control" id="route" name="route" 
						  value="<%=routeUserNames %>" placeholder="결재라인" readonly>
						  <input type="button" class="btn btn-secondary btn-lg" id="find-user" value="직원 찾기">
						</div>
						<div class="form-group d-grid gap-2" id="table-form">
							<table id="myTable" class="table table-striped">
								<tr>
									<td>No</td>
									<td>이름</td>
									<td>사번</td>
									<td>직급</td>
								</tr>
								<%
						for (int i = 0; i < receivers.length; ++i) {
							if (i == receivers.length) {
								break;
							}
							ViewUserBean routeUser = activityCon.getUserView(receivers[i]);

							String routeUserId =routeUser.getId();
							String routeUserName =routeUser.getName();
							String routeUserJob =routeUser.getJobName();
						%>
						<tr>
							<td><%=i+1%></td>
							<td><%=routeUserName%></td>
							<td>
								<%=routeUserId %>
								<input type="hidden" name="routeId" value="<%=routeUserId%>">
							</td>
							<td><%=routeUserJob %></td>
						</tr>
						<%
						}
						%>
							</table>
						</div>
						
						<!-- <div class="form-group d-grid gap-2">
							<select name="type" id="" class="">
								<option value="0">진행</option>
								<option value="1">완료</option>
								<option value="2">취소</option>
							</select>
						</div> -->
						<div class="btn-group">
							<button class="proposalRegister-btn" type="submit">결재 등록</button>
						</div>
					</form>
					<form name="readFrm">
						<input type="hidden" name="receiver">
						<input type="hidden" name="nickname">
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>