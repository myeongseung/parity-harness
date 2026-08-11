<%@page import="model.view.ViewMessageBean"%>
<%@page import="java.util.Vector"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="model.UserBean"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="messageCon" class="controller.MessageController" />
<%
final String PROGRAM_CODE = "919BD592CBDEE0FFEF28AC010022C19445DF326E5C26C708C5EC0DA7D9985B02";

UserBean user = WebHelper.getValidUser(session);

//권한 확인 코드
if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;	
}
String className = request.getParameter("class");

if (className == null || className.trim().equals("")) {
	className = "receiver";
}
if (!className.equals("receiver") && !className.equals("sender")) {
	response.sendRedirect("../accessError.jsp");
	return;
}
String keyfield = "";
String keyword = "";

if (request.getParameter("keyfield") != null) {
	keyfield = request.getParameter("keyfield");
	keyword = request.getParameter("keyword");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>쪽지 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/index.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/message/index.js"></script>
<script src="../js/main/findUser.js"></script>
<link rel="stylesheet" href="../css/message/index.css">
</head>
<body>
	<div class="messageindex-wrap">
		<div class="messageindex-body">
		<%@include file="../indexHeader.jsp"%>
	<div class="message-header">
		<div class="message-header-leftside"></div>
		<div class="message-header-rightside"></div>
	</div>
	<div class="message-nav-wrap">
		<div class="message-nav">
			<!-- 사이드바 시작 -->
			<div class="message-nav-all">
<!-- 				<div class="message-nav-header">&nbsp;</div> -->
				<div class="empty-space"></div>
				<div class="message-nav-button">
					<button class="btn btn-primary write-message">쪽지쓰기</button>
				</div>
				<div class="message-nav-list">
					<div class="<%=className.equals("receiver") ? "message-nav-list-subject" : "message-nav-sent-message"%>">받은쪽지함</div>
					<div class="<%=className.equals("sender") ? "message-nav-list-subject" : "message-nav-sent-message"%>">보낸쪽지함</div>
				</div>
			</div>
		</div>
		<!-- 사이드바 끝 -->
		<form method="" action="" name="messageFrm">
			<input type="hidden" name="class" value="<%=className%>">
			<div class="message-main">
				<div class="message-main-header">
					<div class="middle-header-button-group">
						<button class="btn btn-danger delete-message" type="submit">삭제</button>
						<%
						if (className.equals("receiver")) {
						%>
						<button class="btn btn-primary reply-message" type="submit">답장</button>
						<% } %>
					</div>
					<div class="message-main-header-search">
						<!-- 오른쪽 위 검색창 -->
						<div class="search-option">
							<select class="searchkeyfield" name="keyfield" id="">
								<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
								<%
								if (className.equals("receiver")) {
								%>
								<option value="sender" <%=keyfield.equals("sender") ? "selected" : ""%>>보낸사람</option>
								<%
								}
								%>
								<%
								if (className.equals("sender")) {
								%>
								<option value="receiver" <%=keyfield.equals("receiver") ? "selected" : ""%>>받는사람</option>
								<%
								}
								%>
								<option value="content" <%=keyfield.equals("content") ? "selected" : ""%>>내용</option>
							</select>
	
							<div class="main-search-container">
								<div class="main-search-border">
									<input class="main-search-text" type="search"
										placeholder="검색" aria-label="Search" name="keyword" value="<%=keyword%>">
								</div>
								<div class="main-search-icon">
									<i class="fa-solid fa-magnifying-glass fa-lg"
										id="message-search" title="검색하기"></i>
								</div>
							</div>
						</div>
					</div>
				</div>
				<!-- header -->
				<hr>
				<div class="message-body">
					<div class="message-body-table">
						<table class="table table-striped table-hover">
							<thead>
								<tr>
									<th class="message-body-check"><input name="check" id="chkAll" type="checkbox"></th>
									<!-- 전체클릭 체크  -->
									<th class="message-body-sender"><%=className.equals("receiver") ? "보낸 사람" : "받는 사람"%></th>
									<th class="message-body-content read-message">내용</th>
									<th class="message-body-date">날짜</th>
								</tr>
							</thead>
							<tbody>
								<%
								// Pagenation
	
								final int pagePerBlock = 5;
								final int numPerPage = 15;
	
								int totalRecord = messageCon.getMessageCount(keyfield, keyword, className, user.getId());
	
								int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
								int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
								int nowPage = 1;
	
								if (request.getParameter("nowPage") != null) {
									nowPage = WebHelper.parseInt(request, "nowPage");
								}
								int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);
								int start = (nowPage - 1) * numPerPage;
	
								Vector<ViewMessageBean> messages = messageCon.getMessageViews(keyfield, keyword, className, user.getId(), start, numPerPage);
	
								for (int i = 0; i < numPerPage; ++i) {
									if (i == messages.size()) {
										break;
									}
									ViewMessageBean message = messages.get(i);
	
									int id = message.getId();
									String userId = message.getReceiverId();
									String senderId = message.getSenderId();
									String senderName = message.getSenderName();
									String senderDept = message.getSenderDeptName();
									String senderJob = message.getSenderJobName();
									String receiverName = message.getReceiverName();
									String receiverDept = message.getReceiverDeptName();
									String receiverJob = message.getReceiverJobName();
									String content = message.getContent();
									String messageDate = message.getCreatedAt();
									String name = className.equals("receiver") ?
											"[" + senderDept + "] " + senderName + " " + senderJob :
											"[" + receiverDept + "] " + receiverName + " " + receiverJob;
								%>
								<tr>
									<td class="message-body-check">
										<input type="checkbox" name="messageId" value="<%=id%>">
										<input type="hidden" name="senderId" value="<%=senderId%>">
									</td>
									<td class="message-body-sender"><%=name%></td>
									<td class="message-body-content read-message"><%=content%></td>
									<td class="message-body-date"><%=messageDate%></td>
								</tr>
								<%
								}
								%>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="5"></td>
								</tr>
							</tfoot>
						</table>
					</div>
				</div>
			</form>
			<div class="message-footer">
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
					<a
						href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock + 1%>')">다음</a>
					<%
					}
					%>
				</div>
				<form name="classFrm" method="get">
					<input type="hidden" name="keyfield" value="<%=keyfield %>">
					<input type="hidden" name="keyword" value="<%=keyword %>">
					<input type="hidden" name="class" value="<%=className%>">
				</form>
			</div>
		</div>
	</div>
	</div>
	</div>
</body>
</html>