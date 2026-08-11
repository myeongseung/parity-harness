<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.Duration"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.util.Optional"%>
<%@page import="model.view.ViewWorkBean"%>
<%@page import="java.time.LocalDate"%>
<%@page import="repository.TaskRepository"%>
<%@page import="repository.ProposalRepository"%>
<%@page import="model.UserBean"%>
<%@page import="model.view.ViewPostBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.ProposalBean"%>
<%@page import="model.view.ViewUserBean"%>
<%@page import="model.view.ViewProposalBean"%>
<%@page import="model.view.ViewTaskBean"%>
<%@page import="java.util.Vector"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<jsp:useBean id="taskCon" class="controller.TaskController" />
<jsp:useBean id="postCon" class="controller.PostController" />
<jsp:useBean id="proposalCon" class="controller.ProposalController" />
<%
UserBean user = WebHelper.getValidUser(session);

if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>ERFlow Administrator Configuration</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/admin/admin.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/admin/adminSidebar.css">
<link rel="stylesheet" href="../css/admin/adminHeader.css">
<script src="../js/admin/admin.js"></script>
<script src="../js/admin/adminGraph.js"></script>
<script src="../js/admin/adminCategory.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<script src="https://d3js.org/d3.v7.min.js"></script>
</head>
<body>
	<%@ include file="adminSide.jsp"%>
	<%@ include file="adminHeader.jsp"%>
	<!-- 여기가 본문 페이지 -->
		<div class="admin-body-wrap">
			<div class="admin-body-left">
				<div class="workstatus">
					<div class="graph-header">
						<div class="graph-header-name">근무 현황</div>
						<div>&nbsp;</div> 
					</div>
					<svg id="graph" width="100%" height="100%"></svg>
					
				</div> <!-- 근무현황 -->
				<div class="approval">
					
					<div class="body-header">
						<div class="body-header-name">결재</div>
						<div>&nbsp;</div> 
					</div>
					<div class="approval-content-div">
						<table class="approval-content">
							<tr>
								<th>사번</th>
								<th class="approval-subject">문서제목</th>
								<th class="approval-date">결재요청시각</th>
								<th class="approval-status">진행상황</th>
							</tr>
							<%
								Vector<ViewProposalBean> proposals = proposalCon.getRecentProposalViews();
								
								for (ViewProposalBean proposal : proposals) {
									long proposalId = proposal.getId();
									String userId = proposal.getUserId();
									String subject = proposal.getSubject();
									String approvedAt = proposal.getReceivedAt();
									int result = proposal.getResult();
									String colorCode = "#E4E4E4";
									
									switch (result) {
										case 1:
											colorCode = "#C2FF63";
											break;
										case 2:
											colorCode = "#EA7B86";
											break;
										case 3:
											colorCode = "#FFFF40";
											break;
									}
									if (subject.length() > 10) {
										subject = subject.substring(0, 10) + "...";
									}
							%>
							<tr>
								<td><%=userId%></td>
								<td colspan="3">
									<div class="input-group mb-3">
									  <span class="input-group-text">
									  	<a href="../proposal/proposalDocument.jsp?proposalId=<%=proposalId%>" style="text-decoration: none;">
									  		<%=subject%>
								  		</a>
							  		  </span>
									  <input type="text" readonly class="category-date form-control" value="<%=approvedAt%>">
									  <span class="input-group-text" style="background-color: <%=colorCode%>;">
									  	<div class="category">
									  		<%=ProposalRepository.getInstance().getProposalCode(result)%>
									  	</div>
									  </span>
									</div>
								</td>
							</tr>
							<%
								}
							%>
						</table>
					</div> <!-- 결재 -->
				</div>
			</div>
			<div class="admin-body-right">
				<div class="admin-right-group">
					<div class="right-group-element">
						<div class="basic-info">
							<div class="body-header">
								<div class="body-header-name">수/발주 내역</div>
								<div>&nbsp;</div> 
							</div>
							<div class="temporary-div">
								<table class="temporary table">
									<tr>
										<th>등록인</th>
										<th>수주/발주</th>
										<th>요청일</th>
										<th>상태</th>
									</tr>
									<%
										String keyfield = "";
										Vector<ViewTaskBean> tasks = taskCon.getTasks(keyfield, null, 0, 15, -1);
										
										for (int i = 0; i < tasks.size(); ++i) {
											ViewTaskBean task = tasks.get(i);
											
											String name = task.getUserName();
											int type = task.getType();
											String createdAt = task.getCreatedAt();
											int status = task.getStatus();
									%>
									<tr>
										<td><%=name%></td>
										<td>
											<%=TaskRepository.getInstance().getTaskTypeCode(type)%>
										</td>
										<td><%=createdAt%></td>
										<td>
											<%=TaskRepository.getInstance().getTaskStatusCode(status)%>
										</td>
									</tr>
									<%
										}
									%>						
								</table>
							</div>
								
						</div> <!-- 기본정보 -->
						<div class="notification">
							<div class="body-header">
								<div class="body-header-name">공지사항</div>
								<div>&nbsp;</div> 
							</div>
							<div class="notification-table-div">
								<table class="table table-bordered table-hover notification-table">
								  <thead>
								    <tr>
								      <th scope="col" class="notification-subject">제목</th>
								      <th scope="col" class="notification-date">작성일</th>
								    </tr>
								  </thead>
								  <tbody>
								  <%
								  	String keyword = "";
								  	Vector<ViewPostBean> noticeList = activityCon.getPostViews(1, keyfield, keyword, 0, 15);

									for (ViewPostBean post : noticeList) {
										int postId = post.getId();
										String subject = post.getSubject();
										String createdAt = post.getCreatedAt();
								  %>
								    <tr>
								      <td><a href="../post/postView.jsp?boardId=1&id=<%=postId%>" style="text-decoration: none;"><%=subject%></a></td>
								      <td class="notification-date"><%=createdAt%></td>
								    </tr>
								    <%
									}
								    %>
								    </tbody>
								</table>
							</div>
						</div> <!-- 공지사항 -->
					</div>
					<div class="attendance-management">
						<div class="body-header">
							<div class="body-header-name"><i class="fa-regular fa-clock fa-lg"></i> 출/퇴근관리</div>
							<div>&nbsp;</div> 
						</div>
						<div class="attendance-management-content-div">
							<table class="attendance-management-content">
							<%
							final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
							final String[] workStatus = {
								"결근", "근무 중", "퇴근", "조퇴", "지각", "반차", "연차"
							};
							
							LocalDate ld = LocalDate.now();
							Vector<ViewWorkBean> works = activityCon.getWorkViews(ld.toString());
							
							for (ViewWorkBean work : works) {
								int status = work.getStatus();
								String name = work.getUserName();
								String message = workStatus[status];

								LocalDateTime startedAt = work.getStartedAt() != null ?
										LocalDateTime.parse(work.getStartedAt(), dtf) :
										LocalDateTime.now();
								LocalDateTime endedAt = work.getEndedAt() != null ?
										LocalDateTime.parse(work.getEndedAt(), dtf) :
										LocalDateTime.now();
								Duration d = null;
								String time = "-";
								
								if (status == 1 || status == 2) {
									d = Duration.between(startedAt, endedAt)
											.minusHours(1L);
									time = String.format("%02d:%02d",
							                d.toHoursPart(), 
							                d.toMinutesPart());
								}
								if (name.length() > 6) {
									name = name.substring(0, 6) + "...";
								}
							%>
								<tr>
									<td class="coworker"><%=name%></td>
									<td colspan="2">
										<div class="input-group mb-3">
										  <span class="input-group-text"><div class="category"><%=message%></div></span>
										  <input type="text" class="form-control time-cell" readonly value="<%=time%>">
										</div>
									</td>
								</tr>
							<%
							}
							%>
							</table>
						</div>
					</div> <!-- 출퇴근 관리 -->
				</div>
				<jsp:include page="adminFooter.jsp"/>	
				</div>
			</div>
		</div>
	</div>
	</div>
</body>
</html>