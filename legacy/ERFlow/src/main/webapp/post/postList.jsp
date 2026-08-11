<!-- postList.jsp -->
<%@page import="model.FileBean"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="model.view.ViewPostBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.BoardBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="fileCon" class="controller.PostFileController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
String paramBoardId = request.getParameter("boardId");
int boardId = -1;

if (paramBoardId != null) {
	boardId = WebHelper.parseInt(request, "boardId");
}
//if (user == null || boardId == -1) {
if (boardId == -1) {
	response.sendRedirect("../accessError.jsp");
	return;
}
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || !permissionCon.hasBoardReadPermission(session, boardId)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

//검색 기능 지원
String keyfield = "";
String keyword = "";

//만약 사용자를 검색하려고 하는 시도가 있다면
if (request.getParameter("keyfield") != null) {
	keyfield = request.getParameter("keyfield");
	keyword = request.getParameter("keyword");
}
BoardBean board = activityCon.getBoard(boardId);
String boardName = board.getSubject();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시글 목록</title>
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
<link rel="stylesheet" href="../css/post/postList.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/post/postList.js"></script>	
</head>
<body>
	<!-- 여기까지 -->
		<%@include file="/indexHeader.jsp"%>
		<!-- indexHeader -->
	
		<!--  본문  -->
		<div class="content-wrap">
			<%@include file="/indexSide.jsp"%>
			<!-- indexSide -->
		<div class="main-notice">
        
        <div class="notice1">
        	<div class="right-align">
				<span class="menu-name">사용자 > 게시판 > <%=boardName%></span>
			</div>
            <div class="box">
                <table class="table table-striped"><!--부트스트랩-->
                	<form action="postRegister.jsp" > <!-- 글쓰기 폼 ------------------->
                		<input type="hidden" name="boardId" value="<%=boardId%>">
	                    <tr>
	                        <td class="table-name" colspan="4"><%=boardName%></td>
	                        <td id="postlist-th"><button type="submit" value="write">글쓰기</button></td>
	                    </tr>
	                </form>
                    	<tr>
                        <th>글번호</th>
                        <th>제목</th>
                        <th>작성자</th>
                        <th>작성날짜</th>
                        <th>조회수</th>
                    	</tr>
                    <%
                 	// Pagenation

						final int pagePerBlock = 5;
						final int numPerPage = 15;

						int totalRecord = activityCon.getTotalCount(boardId, keyfield, keyword);
						int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
						int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
						int nowPage = 1;

						if (request.getParameter("nowPage") != null) {
							nowPage = WebHelper.parseInt(request, "nowPage");
						}
						int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

						int start = (nowPage - 1) * numPerPage;
                    
                		Vector<ViewPostBean> postList = activityCon.getPostViews(boardId, keyfield, keyword, start, numPerPage);
                		
                		for (int i = 0; i < numPerPage; ++i) {
                			if (postList.size() == i) {
                				break;
                			}
                			ViewPostBean post = postList.get(i);
                			int id = post.getId();
                			String userId = post.getUserId();
                			String name = post.getName();
                			int refId = post.getRefId();
                			String subject = post.getSubject();
                			String content = post.getContent();
                			int depth = post.getDepth();
                			int pos = post.getPos();
                			int count = post.getCount();
                			String createdAt = post.getCreatedAt();
                			int delete = post.getDelete();
                			
                			int cCount = activityCon.getTotalCommentCount(id);
                			FileBean fileBean = fileCon.getFileByPostId(id);
                			String filename = (fileBean != null) ? fileBean.getOriginalName() : null;
                	%>
                    <tr>
                    	<td><%=totalRecord - (start + i)%></td>
                    	<td align="left">
                    	<a class="post-reader" data-value="<%=id%>" href="#">
                    	<%
                    	for (int j = 0; j < pos; ++j) {
                    		out.println("&nbsp;&nbsp");
                    	}
                    	if (pos > 0) {
                    		out.println("답글 : ");
                    	}
        				%>
                    	<%=subject%></a>
                    	<% if (filename != null && !filename.isEmpty()) { %>
                    		<img src="img/icon.gif" alt="첨부파일" align="middle" />
                    	<% } %>
                    	<% if(cCount > 0) { %>
                   			<span><a class="comment-reader" href="#">(<%=cCount%>)</a></span>
                   		<% } %>
                    	</td>
                    	<td><a href="../profile.jsp?id=<%=userId%>" style="text-decoration: none"><%=name%></a></td>
                    	<td><%=createdAt%></td>
                    	<td><%=count%></td>
                    </tr>

                    <%
                		}
                    %>
                    <tfoot>
                    	<tr>
                    		<td colspan="5"></td>
                    	</tr>
                    </tfoot>
                </table>
            </div>
            <div class="userBoardList-footer">
            
	            <form name="postFrm"> <!-- 검색창 관련 폼 -->
	            	
	            	<div class="userBoardInfo">
	            		<div>&nbsp;</div>
			            <div class="search-option">
		                  <select name="keyfield">
		                     <option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
		                     <option value="subject" <%=keyfield.equals("subject") ? "selected" : ""%>>제목</option>
		                     <option value="author" <%=keyfield.equals("author") ? "selected" : ""%>>작성자</option>
		                  </select>
		
		                  <div class="main-search-container">
		                     <div class="main-search-border">
		                        <input class="main-search-text" type="search"
		                           placeholder="검색" aria-label="Search" name="keyword" value="<%=keyword%>">
		                     </div>
		                     <div class="main-search-icon">
		                       	<i class="fa-solid fa-magnifying-glass fa-lg" title="검색하기"></i>    
		                     </div>
		                  </div>
		               </div>
	            	</div>
	             </form>
	            	
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
        </div>
    </div>
    <form name="searchFrm">
		<input type="hidden" name="keyfield" value=""> 
		<input type="hidden" name="keyword" value="">
	</form>
	<form name="readFrm">
		<input type="hidden" name="boardId" value="<%=boardId%>">
		<input type="hidden" name="nowPage" value="<%=nowPage%>">
		<input type="hidden" name="keyfield" value="<%=keyfield%>">
		<input type="hidden" name="keyword" value="<%=keyword%>">
	</form>
	<form action="postView.jsp" name="showFrm">
		<input type="hidden" name="boardId" value="<%=boardId%>">
		<input type="hidden" name="id">
	</form>		
</div>
</body>
</html>