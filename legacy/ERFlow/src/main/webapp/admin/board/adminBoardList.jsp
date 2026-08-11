<%@page import="model.view.ViewPostBean"%>
<%@page import="model.BoardBean"%>
<%@page import="java.util.Vector"%>
<%@page import="helper.WebHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
String paramBoardId = request.getParameter("boardId");
String boardName = "";
String keyfield = "";
String keyword = "";

int boardId = 1;	// 기본 뷰는 공지사항으로 세팅

if (request.getParameter("board") != null) {
	boardName = request.getParameter("board");
}
if (request.getParameter("boardId") != null) {
	try {
		boardId = WebHelper.parseInt(request, "boardId");
	} catch (NumberFormatException e) {
		response.sendRedirect("../../accessError.jsp");
		return;
	}
}
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
<title>게시판 관리 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/common/page.css">
<link rel="stylesheet" href="../../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../../css/admin/admin.css">
<link rel="stylesheet" href="../../css/admin/adminSidebar.css">
<link rel="stylesheet" href="../../css/admin/adminHeader.css">
<link rel="stylesheet" href="../../css/admin/adminBoardList.css">
<script src="../../js/admin/admin.js"></script>
<script src="../../js/admin/adminBoardList.js"></script>
<script src="../../js/bootjs/bootstrap.js"></script>
</head>
<body>
	<%@include file="../adminSide.jsp"%>
	<%@include file="../adminHeader.jsp"%>
	<div class="empty-space"></div>
	<div class="main-notice">

		<div class="left">
			<div class="empty-space"></div>
			<div class="left-subject">게시판 목록</div>
			<div class="empty-space"></div>

			<div class="empty-space"></div>
			<form name="boardFrm" action="boardDeleteProc.jsp" method="post">
				<!-- method 는 get또는 post , action은 따로 입력-->
				<div class="left-group">
					<div class="admin-menu">
						<button type="button" class="btn btn-secondary register-board">생성</button>
						<button type="button" class="btn btn-secondary update-board">수정</button>
						<button type="submit" class="btn btn-danger">삭제</button>
					</div>
					<div class="left-group">
						<div class="main-search-container">
							<div class="main-search-icon">
								<i class="fa-solid fa-magnifying-glass fa-lg" title="검색하기"></i>
							</div>
							<div class="main-search-border">
								<input class="main-search-text" name="board" type="search"
									placeholder="검색하기" aria-label="Search" value="<%=boardName%>">
							</div>
						</div>
						<button type="button" id="boardSearch" class="btn btn-primary">검색</button>
					</div>
				</div>
				<%
				// Pagenation
				final int pagePerBlock = 5;
				final int numPerPage = 15;
								
				int totalRecord = adminCon.getBoardCounts(boardName);
				int totalPage = (int)Math.ceil(1.0 * totalRecord / numPerPage);
				int totalBlock = (int)Math.ceil(1.0 * totalPage / pagePerBlock);
				int nowBoard = 1;
								
				if (request.getParameter("nowBoard") != null) {
					nowBoard = WebHelper.parseInt(request, "nowBoard");
				}
				int nowBlock = (int)Math.ceil(1.0 * nowBoard / pagePerBlock);
				
				int start = (nowBoard - 1) * numPerPage;
				
				Vector<BoardBean> boards = adminCon.getBoards(boardName, start, numPerPage);
				%>
				<div class="empty-space"></div>
				<!-- 각 box안에 체크박스에 name태그로 변화를 둠-->
				<div class="box">
					<table class="table table-striped">
						<thead>
							<tr>
								<th style="width: 5%;"><input type="checkbox" id="chkAllBoard"></th>
								<th style="width: 30%;">게시판 이름</th>
								<th style="width: 20%;">최초 생성시각</th>
								<th style="width: 15%;">부서 읽기</th>
								<th style="width: 15%;">부서 쓰기</th>
								<th style="width: 15%;">직급 읽기</th>
								<th style="width: 15%;">직급 쓰기</th>
								<th style="width: 5%;"></th>
							</tr>
						</thead>
						<tbody>
						<%
						// 사용자 정보 출력
						for (int i = 0; i < numPerPage; ++i) {
							if (i == boards.size()) {
								break;
							}
							BoardBean board = boards.get(i);
							
							int beanBoardId = board.getId();
							String subject = board.getSubject();
							String createdAt = board.getCreateAt();
						%>
							<tr>
								<td>
								<%
								if (beanBoardId > 2) {
								%>
								<input type="checkbox" name="boardId" value="<%=beanBoardId%>">
								<%
								}
								%>
								</td>
								<td><a href="../../post/postList.jsp?boardId=<%=beanBoardId%>"><%=subject%></a></td>
								<td><%=createdAt%></td>
								<td><a href="boardDeptUpdate.jsp?id=<%=beanBoardId%>&flag=read">수정</a></td>
								<td><a href="boardDeptUpdate.jsp?id=<%=beanBoardId%>&flag=write">수정</a></td>
								<td><a href="boardJobUpdate.jsp?id=<%=beanBoardId%>&flag=read">수정</a></td>
								<td><a href="boardJobUpdate.jsp?id=<%=beanBoardId%>&flag=write">수정</a></td>
								<td><i class="board-view fa-solid fa-magnifying-glass fa-lg" data-id="<%=beanBoardId%>" title="보기"></i></td>
							</tr>
						<%
						}
						%>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="8"></td>
							</tr>
						</tfoot>
					</table>
				</form>
			</div>
			<!-- --box -->
			
			<!-- 페이징 -->
			<div class="page-controller">
				<%
			 	if (nowBlock > 1) {
			 	%>
				<a href="javascript:blockBoard('<%=pagePerBlock%>', '<%=nowBlock - 1%>')" class="disabled">이전</a>
				<%
			 	}
			 	int pageStart = (nowBlock - 1) * pagePerBlock + 1;
			 	int pageEnd = pageStart + pagePerBlock;
			 	pageEnd = pageEnd <= totalPage ? pageEnd : totalPage + 1;
			 	%>
				<ul class="page-numbers">
					<%
			 	for ( ; pageStart < pageEnd; ++pageStart) {
		 		%>
					<li>
						<a href="javascript:pagingBoard('<%=pageStart%>')" <%=(pageStart == nowBoard) ? "class=\"disabled\"" : ""%>>
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
				<a href="javascript:blockBoard('<%=pagePerBlock%>', '<%=nowBlock + 1%>')">다음</a>
				<%
			 	}
			 	%>
			</div>
		</div>
		<!--  --left -->
		<div class="right">
			<div class="empty-space"></div>
			<div class="right-subject">게시글 목록</div>
			<div class="empty-space"></div>
			<div class="right-group">
			<form name="postFrm" action="postDeleteProc.jsp" method="post">
				<input type="hidden" name="boardId" value="<%=boardId%>">
				<div class="right-group-left">
					<div class="admin-menu">
						<button type="submit" class="btn btn-danger">삭제</button>
					</div>
					<div class="main-leftgroup">
						<div class="main-search-select">
							<select name="keyfield">
								<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
								<option value="subject" <%=keyfield.equals("subject") ? "selected" : ""%>>제목</option>
								<option value="author" <%=keyfield.equals("author") ? "selected" : ""%>>작성자</option>
							</select>
						</div>
						<div class="main-search-container">
							<div class="main-search-icon">
								<i class="fa-solid fa-magnifying-glass fa-lg"></i>
							</div>
							<div class="main-search-border">
								<input class="main-search-text" id="keyword" name="keyword" type="search"
									placeholder="검색하기" class="button" aria-label="Search" value="<%=keyword%>">
							</div>
						</div>
						<button type="button" id="postSearch" class="btn btn-primary">검색</button>
					</div>
				</div>
				<%
				// Pagenation
								
				totalRecord = adminCon.getTotalCount(boardId, keyfield, keyword);
				totalPage = (int)Math.ceil(1.0 * totalRecord / numPerPage);
				totalBlock = (int)Math.ceil(1.0 * totalPage / pagePerBlock);
				int nowPage = 1;
								
				if (request.getParameter("nowPage") != null) {
					nowPage = WebHelper.parseInt(request, "nowPage");
				}
				nowBlock = (int)Math.ceil(1.0 * nowBoard / pagePerBlock);
				
				start = (nowPage - 1) * numPerPage;
				
				Vector<ViewPostBean> posts = adminCon.getPostViews(boardId, keyfield, keyword, start, numPerPage);
				%>
				<div class="empty-space"></div>
				<div>
					<table class="table table-striped">
						<thead>
							<tr>
								<th style="width: 5%;"><input type="checkbox" id="chkAllPost"></th>
								<th style="width: 10%;">글번호</th>
								<th style="width: 30%;">제목</th>
								<th style="width: 15%;">작성자</th>
								<th style="width: 30%;">작성날짜</th>
								<th style="width: 5%;">조회수</th>
								<th style="width: 5%;">댓글수</th>
							</tr>
						</thead>
						<tbody>
						<%
						// 사용자 정보 출력
						for (int i = 0; i < numPerPage; ++i) {
							if (i == posts.size()) {
								break;
							}
							ViewPostBean post = posts.get(i);
							
							int postId = post.getId();
							String subject = post.getSubject();
							String userId = post.getUserId();
							String userName = post.getName();
							String createdAt = post.getCreatedAt();
							int count = post.getCount();
							int commentCount = adminCon.getTotalCommentCount(postId);
						%>
							<tr>
								<td><input type="checkbox" name="postId" value="<%=postId%>"></td>
								<td><%=postId%></td>
								<td><a href="../../post/postView.jsp?boardId=<%=boardId%>&id=<%=postId%>"><%=subject%></a></td>
								<td><a href="../../profile.jsp?id=<%=userId%>"><%=userName%></a></td>
								<td><%=createdAt%></td>
								<td><%=count%></td>
								<td><%=commentCount%></td>
							</tr>
						<%
						}
						%>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="6"></td>
								<td></td>
							</tr>
						</tfoot>
						<tr>
						</tr>
					</table>
				</div>
			</form>
		</div>
		<!-- 페이징 -->
		<div class="page-controller">
		<%
	 	if (nowBlock > 1) {
	 	%>
		<a href="javascript:blockPost('<%=pagePerBlock%>', '<%=nowBlock - 1%>')" class="disabled">이전</a>
		<%
	 	}
	 	pageStart = (nowBlock - 1) * pagePerBlock + 1;
	 	pageEnd = pageStart + pagePerBlock;
	 	pageEnd = pageEnd <= totalPage ? pageEnd : totalPage + 1;
	 	%>
		<ul class="page-numbers">
			<%
	 	for ( ; pageStart < pageEnd; ++pageStart) {
 		%>
			<li>
				<a href="javascript:pagingPost('<%=pageStart%>')" <%=(pageStart == nowPage) ? "class=\"disabled\"" : ""%>>
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
		<a href="javascript:blockPost('<%=pagePerBlock%>', '<%=nowBlock + 1%>')">다음</a>
		<%
	 	}
	 	%>
			</div>
		<form name="readFrm">
			<input type="hidden" name="nowBoard" value="<%=nowBoard%>">
			<input type="hidden" name="nowPage" value="<%=nowPage%>">
			<input type="hidden" name="board" value="<%=boardName%>">
			<input type="hidden" name="boardId" value="<%=boardId%>">
			<input type="hidden" name="keyfield" value="<%=keyfield%>">
			<input type="hidden" name="keyword" value="<%=keyword%>">
		</form>
	</div>
</div>
</body>
</html>