<!-- postView.jsp -->
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Vector"%>
<%@page import="model.FileBean"%>
<%@page import="model.UserBean"%>
<%@page import="model.view.ViewCommentBean"%>
<%@page import="model.view.ViewPostBean"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" scope="page" />
<jsp:useBean id="fileCon" class="controller.PostFileController" scope="page" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" scope="page" />
<%
String paramBoardId = request.getParameter("boardId");
String paramId  = request.getParameter("id");
int boardId = 2;		// 테스트용으로 바꾼 것이므로, 나중에 -1로 바꿀 것.
int postId = -1;

if (paramBoardId != null && !paramBoardId.trim().equals("") &&
	paramId != null && !paramId.trim().equals("")) {
	try {
		boardId = WebHelper.parseInt(request, "boardId");
		postId = WebHelper.parseInt(request, "id");
	} catch(NumberFormatException e) {
		e.printStackTrace();
	}
}
if (boardId == -1 || postId == -1) {
	response.sendRedirect("../accessError.jsp");
	return;
}
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || !permissionCon.hasBoardReadPermission(session, boardId)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
ViewPostBean postView = activityCon.getPostView(session, boardId, postId);

if (postView != null) {
	boolean isCreatedUser = postView.getUserId().equals(user.getId());
	
	if (!isCreatedUser) {
		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null && cookies.length > 0) {
			for (Cookie c : cookies) {
				if (c.getName().equals("postId")) {
					cookie = c;
				}
			}
		}
		boolean result = false;
		
		if (cookie == null) {
			cookie = new Cookie("postId", postId + "");
			result = activityCon.updateCount(postId);
		} else {
			String[] cookiePosts = cookie.getValue().split(";");
			List<String> cookiePostList = Arrays.asList(cookiePosts);
			
			if (cookiePostList.indexOf(postId + "") != -1) {
				cookie.setValue(cookie.getValue() + ";" + postId);
				result = activityCon.updateCount(postId);
			}
			if (result) {
				cookie.setPath(request.getContextPath());
				cookie.setMaxAge(60*60*1);
				response.addCookie(cookie);
			}
		}
		if (result) {
			postView = activityCon.getPostView(session, boardId, postId);
		}
	}
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
String subject = postView.getSubject();
String name = postView.getName();
String createdAt = postView.getCreatedAt();
int pcount = postView.getCount();
String content = postView.getContent();
int cCount = activityCon.getTotalCommentCount(postId);
String userId = postView.getUserId();

boolean isAdmin = permissionCon.isAdmin(session);
boolean isDeleted = postView.getDelete() == 1;
boolean isVisible = user.getId().equals(userId);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><%=subject%></title>
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
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/post/postView.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/post/postView.js"></script>
</head>
<body>
	<!-- 여기까지 -->
	<%@include file="../indexHeader.jsp"%>
	<!-- indexHeader -->

	<!--  본문  -->
	<div class="content-wrap">
		<%@include file="../indexSide.jsp"%>
		<!-- indexSide -->
		<div class="main-notice">
			<div class="wrap">
				<div class="context">
					<div class="empty-space"></div>
					<div class="body-border">
						<div class="iheader">
							<table class="subject">
								<div class="subject-one-wrap">
									<div class="subject-one">
										<div class="subject-center"><%=subject%></div>
									</div>
								</div>
								<hr>
								<tr>
									<div class="subject-group">
										<div class="subject-middle-group">
											<div class="left-subject">
												<div>작성자: &nbsp;</div>
												<div class="subject-content2"><%=name%></div>
											</div>

											<div class="right-subject">
												<div class="empty-space-right"></div>
												<div>작성일: &nbsp;</div>
												<div><%=createdAt%></div>
												<div class="empty-space-right"></div>
												<div>댓글수: &nbsp;</div>
												<div><%=cCount%></div>
												<div class="empty-space-right"></div>
												<div>조회수: &nbsp;</div>
												<div><%=pcount%></div>
												<div class="dropstart" role="group">
													<button type="button" class="btn btn-light dropdown-toggle"
														data-bs-toggle="dropdown" aria-expanded="false">
														<i class="fa-solid fa-ellipsis-vertical"></i>
													</button>
													<ul class="dropdown-menu">
														<%
														if (isVisible) {
														%>
														<li>
															<form id="updateForm" action="postUpdate.jsp" method="post">
																<input type="hidden" name="id" value="<%=postId%>">
																<input type="hidden" name="boardId" value="<%=boardId%>">
																<a id="updateLink" class="dropdown-item" href="#">수정하기</a>
															</form>
														</li>
														<%
														}
														if (isAdmin || isVisible) {
														%>
														<li>
															<form id="deleteForm" action="postDeleteProc.jsp"
																method="post">
																<input type="hidden" name="boardId" value="<%=boardId%>">
																<input type="hidden" name="postId" value="<%=postId%>">
																<a id="deleteLink" class="dropdown-item" href="#">삭제하기</a>
															</form>
														</li>
														<%
														}
														if (!isDeleted) {
														%>
														<li>
														<!-- 게시글 답변 -->
															<form id="replyForm" action="postReply.jsp" method="post">
																<input type="hidden" name="boardId" value="<%=boardId%>">
																<input type="hidden" name="postId" value="<%=postId%>">
																<a id="replyLink" class="dropdown-item" href="#">답변하기</a>
															</form>
														</li>
														<%
														}
														%>
													</ul>
												</div>
											</div>
										</div>
									</div>
									<hr>
								</tr>
								<tr>
									<td>
										<%
				            			if (!isDeleted) {
				            				Vector<FileBean> attachments = fileCon.getFiles(postId);
				            		
				            				if (attachments != null && !attachments.isEmpty()) {	            		
				            			%>
										<div class="btn-group drop">
											<button type="button"
												class="btn btn-secondary dropdown-toggle"
												data-bs-toggle="dropdown" aria-expanded="false">첨부파일</button>
											<ul class="dropdown-menu">
												<%
										    		for (FileBean attachment : attachments) {
										    			String dest = attachment.getName();
										    			String filename = attachment.getOriginalName();
										    			String extension = attachment.getExtension();
								   				%>
												<li><a href="downloadFile.jsp?file=<%=dest%>"
													class="dropdown-item"><%=filename + "." + extension%></a></li>
												<%
												}
												%>
											</ul>
										</div>
										<%
						            			}
						            		}
										%>
									</td>
								</tr>
							</table>
						</div>
						<div class="ibody">
							<div class="content"><% out.print(content); %></div>
						</div>
					</div>
				</div>
				<a href="#comment"></a>
				<div class="all-comment-wrap">
					<div class="all-comment">
						<%
						if (!isDeleted) {
						%>
						<form action="commentRegisterProc.jsp" method="post">
							<input type="hidden" name="boardId" value="<%=boardId%>">
							<input type="hidden" name="postId" value="<%=postId%>">
							<table class="comment">
								<tr class="comment-header">
									<td class="name"><%=user.getName()%></td>
								</tr>
								<!-- 댓글 -->
								<tr class="comment-body">
									<td class="post-body"><textarea rows="3" name="comment"
											placeholder="댓글쓰기"></textarea></td>
									<td class="post-enter"><button type="submit">입력</button></td>
								</tr>
							</table>
						</form>
						<%
			       		Vector<ViewCommentBean> comments = activityCon.getCommentViews(postId);
			        	
			       		if (!comments.isEmpty()) {
			       			for (ViewCommentBean commentBean : comments){
			       				int id = commentBean.getId();
			       				int refId = commentBean.getCommentRefId();
			       				int depth = commentBean.getDepth();
			       				String commentUserId = commentBean.getUserId();
			       				String commentUserName = commentBean.getUserName();
			       				String comment = commentBean.getComment();
			       				String commentAt = commentBean.getCreatedAt();
			       				
			       				boolean hasPermission = user.getId().equals(commentUserId);
			        	%>
							<table class="comment">
								<tr class="comment-header">
									<td class="left-header"
										style="padding-left:<%=depth > 0 ? 30 : 0%>px">
										<div class="name"><%=commentUserName%>(<%=commentUserId%>)</div>
										<div class="time"><%=commentAt%></div>
									</td>
									<td class="drop-menu">
										<div class="dropstart" role="group">
										<%
										if (hasPermission || isAdmin || depth == 0) {
										%>
											<button type="button" class="btn btn-light dropdown-toggle"
												data-bs-toggle="dropdown" aria-expanded="false">
												<i class="fa-solid fa-ellipsis-vertical"></i>
											</button>
											<ul class="dropdown-menu">
												<%
												if (hasPermission) {
												%>
												<li><a class="dropdown-item edit-expand-button" role="form" aria-expanded="false"
													aria-controls="collapseExample" data-id="<%=id%>">수정하기</a></li>
												<%
												}
												if (isAdmin || hasPermission) {
												%>
												<li><a class="dropdown-item"
													href="commentDeleteProc.jsp?boardId=<%=boardId%>&postId=<%=postId%>&id=<%=id%>">삭제하기</a></li>
												<%
												}
												if (depth == 0) {
												%>
												<li><a class="dropdown-item reply-expand-button" data-bs-toggle="collapse" role="form" aria-expanded="false"
													aria-controls="collapseExample" data-id="<%=id%>">답글달기</a></li>
												<%
												}
												%>
											</ul>
										<%
										}
										%>
										</div>
									</td>
								</tr>
								<tr class="comment-body">
									<td class="content-body"
										style="padding-left:<%=depth > 0 ? 30 : 0%>px"><%=comment%></td>
								</tr>
								<hr>
								<form action="commentReplyProc.jsp" method="post">
									<input type="hidden" name="boardId" value="<%=boardId%>">
									<input type="hidden" name="postId" value="<%=postId%>">
									<input type="hidden" name="refId" value="<%=refId%>">
									<!-- 답글 -->
									<div class="reply-component">
										<tr class="comment-footer">
											<td class="post-reply" data-id="<%=id%>">
												<div class="collapse">
													<div class="card card-body">
														<div class="post-reply-comments">
															<textarea rows="3" name="comment" placeholder="답글달기"></textarea>
															<button type="submit">입력</button>
														</div>
													</div>
												</div>
											</td>
										</tr>
									</div>
								</form>
								<form action="commentUpdateProc.jsp" method="post">
									<input type="hidden" name="boardId" value="<%=boardId%>">
									<input type="hidden" name="postId" value="<%=postId%>">
									<input type="hidden" name="id" value="<%=id%>">
									<div class="edit-component">
										<tr class="comment-footer">
											<td class="edit-reply" data-id="<%=id%>">
												<div class="collapse">
													<div class="card card-body">
														<div class="post-reply-comments">
															<textarea rows="3" name="comment" placeholder="댓글수정"><%=comment%></textarea>
															<button type="submit">수정</button>
														</div>
													</div>
												</div>
											</td>
										</tr>
									</div>
								</form>
								<%
					      			}
					        	%>
							</table>
						<%
       					} else {
           				%>
							<p>등록된 댓글이 없습니다.</p>
						<%
			       		}
			            %>
						<hr>
						<%
			        	} else {
			        	%>
						<p>삭제된 게시물에는 댓글을 작성할 수 없습니다.</p>
						<%
						}
						%>
					</div>

				</div>
				<!-- main-notice -->
			</div>
</body>
</html>