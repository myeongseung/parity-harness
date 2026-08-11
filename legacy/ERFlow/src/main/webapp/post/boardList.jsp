<!-- boardList.jsp -->
<%@page import="model.view.ViewPostBean"%>
<%@page import="model.BoardBean"%>
<%@page import="java.util.Vector"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
final String PROGRAM_CODE = "4D0B4292FC1A94E622F319AEFB34E9AD4270E32A898591F6DE299357A711C7E4";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
//검색 기능 지원
String keyword = "";

//만약 사용자를 검색하려고 하는 시도가 있다면
if (request.getParameter("keyword") != null) {
	keyword = request.getParameter("keyword");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시판 목록</title>
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
<link rel="stylesheet" href="../css/post/boardList.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/post/boardList.js"></script>
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
			<div class="notice1">
				<div class="box">
				<div class="userBoardList-header">
					<div class="right-align">
						<span class="menu-name">사용자 > 게시판 </span>
					</div>
					
					<!-- 검색창 관련 폼 -->
					<div class="userBoardInfo">
						<div>&nbsp;</div>
						<div class="search-option">
							<select name="" id="">
	                     		<option>게시판 이름</option>
	                  		</select>
	                  		<form name="boardFrm">
								<div class="main-search-container">
									<div class="main-search-border">
										<input class="main-search-text" type="search"
											placeholder="검색" aria-label="Search" name="keyword" value="<%=keyword%>">
									</div>
									<div class="main-search-icon">
										<i class="fa-solid fa-magnifying-glass fa-lg"
											title="검색하기"></i>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
					<table class="table table-striped">
						<!--부트스트랩-->
						<thead>
							<tr>
								<th class="board-number">번호</th>
								<th class="board-list">이름</th>
								<th class="board-count">게시글 수</th>
								<th class="board-title">최신글 제목</th>
								<th class="">업로드 시간</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<%
	                 		// Pagenation
	
							final int pagePerBlock = 5;
							final int numPerPage = 15;
	
							int totalRecord = activityCon.getBoardCount(keyword);
							int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
							int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
							int nowPage = 1;
	
							if (request.getParameter("nowPage") != null) {
								nowPage = WebHelper.parseInt(request, "nowPage");
							}
							int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);
							int start = (nowPage - 1) * numPerPage;
	                    
	                		Vector<BoardBean> boardList = activityCon.getBoards(keyword);
	                		
	                		for (int i = 0; i < numPerPage; ++i) {
	                			if (boardList.size() == i) {
	                				break;
	                			}
	                			BoardBean board = boardList.get(i);
	                			int id = board.getId();
	                			String subject = board.getSubject();
	                			int postCount = activityCon.getTotalCount(id, null, null);
	                			Vector<ViewPostBean> recents = activityCon.getPostViews(id, null, null, 0, 1);
	                			boolean hasRecent = recents.size() > 0;
	                	%>
	                    <tr>
	                    	<td><%=id%></td>
	                    	<td><%=subject%></td>
	                    	<td><%=postCount%></td>
	                    	<%
	                    	if (hasRecent) {
	                    	%>
	                    	<td>
	                    		<a href="postView.jsp?boardId=<%=id%>&id=<%=recents.get(0).getId()%>" style="text-decoration: none;">
	                    			<%=recents.get(0).getSubject()%>
	                    		</a>
                    		</td>
	                    	<%
	                    	} else {
	                    	%>
	                    	<td>작성글 없음</td>
	                    	<%
	                    	}
	                    	%>
	                    	<td><%=hasRecent ? recents.get(0).getCreatedAt() : "작성글 없음"%></td>
	                    	<td><button type="button" class="btn btn-light board-select" data-id="<%=id%>">선택</button></td>
	                    </tr>
	                    <%
                		}
	                    %>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="4"></td>
								<td></td>
								<td></td>
							</tr>
						</tfoot>
					</table>
				</div>
				<div class="userBoardList-footer">
			            <!-- 페이징 -->
					<div class="page-controller">
						<%
						if (nowBlock > 1) {
						%>
						<a href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock - 1%>')" class="disabled">이전</a>
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
			<form name="readFrm">
				<input type="hidden" name="nowPage" value="<%=nowPage%>">
				<input type="hidden" name="keyword" value="<%=keyword%>">
			</form>
			</div>
		</div>
	</div>
</body>
</html>