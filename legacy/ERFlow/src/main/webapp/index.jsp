<!-- index.jsp -->
<%@page import="model.view.ViewMessageBean"%>
<%@page import="model.CalendarBean"%>
<%@page import="repository.ProposalRepository"%>
<%@page import="model.view.ViewProposalBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page import="model.UserBean"%>
<%@page import="model.view.ViewPostBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="messageCon" class="controller.MessageController"/>
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("permissionError.jsp");
	return;
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Welcome to ERFlow</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer">
	
</script>
<link rel="stylesheet" href="css/main/modal.css">
<link rel="stylesheet" href="css/bootcss/bootstrap.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="css/common/common.css">
<link rel="stylesheet" href="css/main/header.css">
<link rel="stylesheet" href="css/main/aside.css">
<link rel="stylesheet" href="css/main/index.css">
<link rel="stylesheet" href="css/main/footer.css">
<link rel="stylesheet" href="css/main/documentList.css">
<script type="module" src="https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/6.1.9/index.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.9/index.global.min.js"></script>
<script src="js/bootjs/bootstrap.js"></script>
<script src="js/main/index.js"></script>
<script src="js/main/calendar.js"></script>
<script src="js/main/indexCalendar.js"></script>
</head>
<body>
	<div class="wrap">

		<%@include file="indexHeader.jsp"%>
		<!-- indexHeader -->

		<!--  본문  -->
		<div class="content-wrap">
			<%@include file="indexSide.jsp"%>
			<!-- indexSide -->
			<div>
				<div class="main-content-wrap">
					<div class="main-wrap">
						<div>
							<!-- 테이블 하나 시작 -->
							<div class="main-table-container">
								<div class="table-panel-header">
									<div class="main-table-head">
										<span class="widget-name">공지사항</span>
									</div>
									<div class="btn-group">
										<a href="post/postList.jsp?boardId=1"><i class="fa-solid fa-arrow-up-right-from-square fa-lg"></i></a>
										<a href=""><i class="fa-solid fa-arrows-rotate fa-lg"></i></a>
										<a href=""><i class="fa-solid fa-plus fa-lg"></i></a>
									</div>
								</div>
								<div class="table-commend">
									<table class="table">
										<thead class="main-thead">
											<tr>
												<th scope="col">번호</th>
												<th scope="col">제목</th>
												<th scope="col">작성자</th>
												<th scope="col">작성일</th>
												<th scope="col">조회수</th>
											</tr>
										</thead>
										<tbody>
											<%
											Vector<ViewPostBean> noticeList = activityCon.getPostViews(1, null, null, 0, 15);
											int noticeTotalCount = activityCon.getTotalCount(1, null, null);

											for (int i = 0; i < noticeList.size(); ++i) {
												ViewPostBean post = noticeList.get(i);

												String subject = post.getSubject();
												String createdAt = post.getCreatedAt();
												String userId = post.getUserId();
												String name = post.getName();
												int id = post.getId();
												int count = post.getCount();
												int commentCount = activityCon.getTotalCommentCount(id);
												
												if (subject.length() > 10) {
													subject = subject.substring(0, 10) + "...";
												}
											%>
											<tr>
												<td><%=noticeTotalCount - i%></td>
												<td><a href="post/postView.jsp?boardId=1&id=<%=id%>"><%=subject%></a>
													<a class="comment-count" href="">[<%=commentCount%>]
												</a></td>
												<td><%=name%>(<%=userId%>)</td>
												<td><%=createdAt%></td>
												<td><%=count%></td>
											</tr>
											<%
											}
											%>
										</tbody>
									</table>
								</div>
							</div>
							<!-- 하나종료 -->
							<div class="main-table-container">
								<div class="table-panel-header">
									<div class="main-table-head">
										<span class="widget-name">전자결재</span>
									</div>
									<div class="btn-group">
										<a href="proposal/proposalList.jsp"><i class="fa-solid fa-arrow-up-right-from-square fa-lg"></i></a>
										<a href=""><i class="fa-solid fa-arrows-rotate fa-lg"></i></a>
										<a href=""><i class="fa-solid fa-plus fa-lg"></i></a>
									</div>
								</div>
								<div class="table-commend">
									<table class="table">
										<thead class="main-thead">
											<tr>
												<th scope="col">결재 번호</th>
												<th scope="col">제목</th>
												<th scope="col">문서 상세</th>
												<th scope="col">결재 상태</th>
												<th scope="col">도착 시각</th>
											</tr>
										</thead>
										<tbody>
											<%
											Vector<ViewProposalBean> proposals = activityCon.getRecentProposalViews(user.getId(), 0, 15);
											ProposalRepository proposalRepo = ProposalRepository.getInstance();

											// 결재 정보 출력
											for (int i = 0; i < proposals.size(); ++i) {
												ViewProposalBean proposal = proposals.get(i);

												long id = proposal.getId();
												long documentId = proposal.getDocumentId();
												String subject = proposal.getSubject();
												String date = proposal.getReceivedAt();
												int result = proposal.getResult();
											%>
											<tr>
												<td><%=id%></td>
												<td><%=subject%></td>
												<td><a
													href="proposal/proposalDocument.jsp?proposalId=<%=id%>">문서
														보기</a></td>
												<td><%=proposalRepo.getProposalCode(result)%></td>
												<td><%=date%></td>
											</tr>
											<%
											}
											%>
										</tbody>
									</table>
								</div>
							</div>

						</div>

						<div>
							<div class="main-table-container">
								<div class="table-panel-header">
									<div class="main-table-head">
										<span class="widget-name">자유게시판</span>
									</div>
									<div class="btn-group">
										<a href="post/postList.jsp?boardId=2"><i class="fa-solid fa-arrow-up-right-from-square fa-lg"></i></a>
										<a href=""><i class="fa-solid fa-arrows-rotate fa-lg"></i></a>
										<a href=""><i class="fa-solid fa-plus fa-lg"></i></a>
									</div>
								</div>
								<div class="table-commend">
									<table class="table">
										<thead class="main-thead">
										<thead class="main-thead">
											<tr>
												<th scope="col">번호</th>
												<th scope="col">제목</th>
												<th scope="col">작성자</th>
												<th scope="col">작성일</th>
												<th scope="col">조회수</th>
											</tr>
										</thead>
										<tbody>
											<%
											Vector<ViewPostBean> freeList = activityCon.getPostViews(2, null, null, 0, 15);
											int freeTotalCount = activityCon.getTotalCount(2, null, null);

											for (int i = 0; i < freeList.size(); ++i) {
												ViewPostBean post = freeList.get(i);

												String subject = post.getSubject();
												String createdAt = post.getCreatedAt();
												String userId = post.getUserId();
												String name = post.getName();
												int id = post.getId();
												int count = post.getCount();
												int commentCount = activityCon.getTotalCommentCount(id);
												
												if (subject.length() > 10) {
													subject = subject.substring(0, 10) + "...";
												}
												if (name.length() > 3) {
													name = name.substring(0, 3) + "...";
												}
											%>
											<tr>
												<td><%=freeTotalCount - i%></td>
												<td><a href="post/postView.jsp?boardId=2&id=<%=id%>"><%=subject%></a>
													<a class="comment-count" href="">[<%=commentCount%>]
												</a></td>
												<td><%=name%>(<%=userId%>)</td>
												<td><%=createdAt%></td>
												<td><%=count%></td>
											</tr>
											<%
											}
											%>
										</tbody>
									</table>
								</div>
							</div>
							<div class="main-table-container">
								<div class="table-panel-header">
									<div class="main-table-head">
										<span class="widget-name">받은 쪽지함</span>
									</div>
									<div class="btn-group">
										<a href="message/index.jsp"><i
											class="fa-solid fa-arrow-up-right-from-square fa-lg"></i></a> <a
											href=""><i
											class="fa-solid fa-arrows-rotate fa-lg"></i></a> <a href=""><i
											class="fa-solid fa-plus fa-lg"></i></a>
									</div>
								</div>
								<div class="table-commend">
									<table class="table">
										<thead class="main-thead">
											<tr>
												<th scope="col">번호</th>
												<th scope="col">보낸이</th>
												<th scope="col">내용</th>
												<th scope="col">수신 시각</th>
											</tr>
										</thead>
										<tbody>
											<%
											Vector<ViewMessageBean> msgList = messageCon.getMessageViews(null, null, "receiver", user.getId(), 0, 15);
											int msgTotalCount = messageCon.getMessageCount(null, null, "receiver", user.getId());

											for (int i = 0; i < msgList.size(); ++i) {
												ViewMessageBean msg = msgList.get(i);
												
												int id = msg.getId();
												String sender = msg.getSenderName();
												String senderDept = msg.getSenderDeptName();
												String senderJob = msg.getSenderJobName();
												String content = msg.getContent();
												String createdAt = msg.getCreatedAt();
												
												if (content.length() > 10) {
													content = content.substring(0, 10) + "...";
												}
											%>
											<tr>
												<td><%=msgTotalCount - i%></td>
												<td>[<%=senderDept%>] <%=sender%> <%=senderJob%></td>
												<td><a href="message/read.jsp?messageId=<%=id%>"><%=content%></a></td>
												<td><%=createdAt%></td>
											</tr>
											<%
											}
											%>
										</tbody>
									</table>
								</div>
							</div>

						</div>
					</div>

					<div>
						<div class="main-calendar-container">
							<div class="table-panel-header">
								<div class="main-table-head">
									<span class="widget-name">일정관리</span>
								</div>
								<div class="btn-group">
									<a href=""> <i class="fa-solid fa-arrow-up-right-from-square fa-lg"></i>
									</a> <a href="http://localhost:5501/index.html"><i
										class="fa-solid fa-arrows-rotate fa-lg"></i></a> <a href="#">
										<i class="fa-solid fa-plus fa-lg" data-bs-toggle="modal"
										data-bs-target="#calendar-plus"></i>
										<div class="modal fade" id="calendar-plus"
											data-bs-backdrop="static" data-bs-keyboard="false"
											tabindex="-1" aria-labelledby="calendar-plus-label"
											aria-hidden="true">
											<div class="modal-dialog">
												<div class="modal-content">
													<form>
														<input type="hidden" name="userId"
															value="<%=user.getId()%>">
														<div class="modal-header">
															<h5 class="modal-title" id="calendar-plus-label">일정
																정하기</h5>
															<button type="button" class="btn-close"
																data-bs-dismiss="modal" aria-label="Close"></button>
														</div>
														<div class="modal-body index-calendar-content">
															<table>
																<tr>
																	<td>시작일</td>
																	<td><input class="calendar-start"
																		type="datetime-local"></td>
																</tr>
																<tr>
																	<td>종료일</td>
																	<td><input class="calendar-end"
																		type="datetime-local"></td>
																</tr>
															</table>
															<select class="form-select calendar-select">
																<option value="0">개인일정</option>
																<option value="1">부서일정</option>
																<option value="2">전체일정</option>
															</select>
															<div class="input-group mb-3">
																<span class="input-group-text" id="basic-addon1">제목</span>
																<input type="text"
																	class="form-control calendar-explanation calendar-subject">
																<span class="input-group-text" id="basic-addon1">내용</span>
																<input type="text"
																	class="form-control calendar-explanation calendar-content">
															</div>

														</div>
														<div class="modal-footer index-calendar-button-group">
															<button type="button"
																class="btn btn-primary calendar-set">설정</button>
															<button type="button"
																class="btn btn-secondary calendar-cancel">취소</button>
														</div>
												</div>
												</form>
											</div>

										</div>
									</a>

								</div>
							</div>

							<!-- 달력 시작 -->
							<div id="calendar"></div>
							<!-- 달력 종료 -->
						</div>
					</div>
				</div>
				<jsp:include page="footer.jsp" />
			</div>
		</div>

		<div id="eventModal" class="modal">
			<!-- 모달 컨텐츠 -->
			
				<div class="modal-content-wrap">
					<div class="modal-header">
						<div class="modal-btn-group">
							<button type="button" class="btn btn-primary" id="update-button">수정</button>
							<button type="button" class="btn btn-danger" id="delete-button">삭제</button>
						</div>
						<h3 id="eventId"></h3>
						<span class="close-button">×</span>
					</div>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1">사번</span> <input
							type="text" id="userId"
							class="form-control calendar-explanation calendar-subject">
					</div>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1">제목</span> <input
							type="text" id="modalTitle"
							class="form-control calendar-explanation calendar-subject">
					</div>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1">내용</span> <input
							type="text" id="modalContent"
							class="form-control calendar-explanation calendar-subject">
					</div>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1">시작</span> <input
							type="datetime-local" id="modalStartDate"
							class="form-control calendar-explanation calendar-subject">

					</div>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1">종료</span> <input
							type="datetime-local" id="modalEndDate"
							class="form-control calendar-explanation calendar-subject">

					</div>
					<select class="form-select calendar-select" name="calendar-type" id="modalType">
						<option value="0">개인일정</option>
						<option value="1">부서일정</option>
						<option value="2">전체일정</option>
					</select>
				</div>
			

		</div>


	</div>
	</div>
	</div>


	<!-------------------------------------------------------------------------------------------------->


</body>

</html>