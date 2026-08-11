<!-- postUpdate.jsp -->
<%@page import="model.FileBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.view.ViewPostBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="model.BoardBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="fileCon" class="controller.PostFileController" />
<%
String paramBoardId = request.getParameter("boardId");
String paramPostId = request.getParameter("id");
int boardId = -1;
int postId = -1;

if (paramBoardId != null && !paramBoardId.equals("") &&
	paramPostId != null && !paramPostId.equals("")) {
	boardId = WebHelper.parseInt(request, "boardId");
	postId = WebHelper.parseInt(request, "id");
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || !permissionCon.hasBoardWritePermission(session, boardId)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

BoardBean board = null;
String boardName = null;

ViewPostBean post = null;
String postContent = null;
String postSubject = null;

if (boardId != -1 && postId != -1) {
	board = activityCon.getBoard(boardId);
	boardName = board.getSubject();
	
	post = activityCon.getPostView(session, boardId, postId);
	postContent = post.getContent();
	postSubject = post.getSubject();
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
String userId = user.getId();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Document</title>
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
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/post/postUpdate.js"></script>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<script src="//cdn.ckeditor.com/4.22.1/full/ckeditor.js"></script>
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/post/postRegister.css">
<link rel="stylesheet" href="../css/common/editor.css">
</head>

<body>
	<!-- 여기까지 -->
	<%@include file="../indexHeader.jsp"%>
	<!-- indexHeader -->

	<!--  본문  -->
	<div class="content-wrap">
		<!-- indexSide -->
		<%@include file="../indexSide.jsp"%>
		<div id="main-wrap">
			<form method="post" action="postUpdateProc.jsp" id="board-write" enctype="multipart/form-data">
			<input type="hidden" name="boardId" value="<%=boardId%>">
			<input type="hidden" name="id" value="<%=postId%>">
				<div class="board-bodyeditor">
					<div class="empty-space">&nbsp;</div>
					<div class="empty-space"></div>
					<div class="empty-space">&nbsp;</div>
					<div class="empty-space"></div>

					<div class="write-type-wrap">
						<div class="write-type">
							<div class="right-align">
								<span class="menu-name">사용자 > 게시판 > 글수정</span>
							</div>
						</div>
					</div>

					<div class="write-header-wrap">
						<div class="write-header">
							<input type="text" class="write-header-type" value="<%=boardName%>"
								readonly> <input type="text" name="subject"
								class="write-header-subject" placeholder="제목을 입력해주세요" value="<%=postSubject%>" required>
						</div>
					</div>
				</div>
				<div class="write-body">
					<textarea name="content" id="editor1" class="ckeditor-textarea"
						rows="10" cols="80">
					<%=postContent%>
				</textarea>
				</div>
				<div class="write-header-wrap">
					<div class="write-header">
					<div class="left-align">
						<div>
							<input type="file" name="filename" class="write-header-type" multiple>
						</div>
						<%
						Vector<FileBean> files = fileCon.getFiles(postId);
						%>
						<div id="listbox" style="display:<%=files.isEmpty() ? "none" : "block"%>">
							<div id="listfile">
								첨부파일 :
								<div class="center-align" id="fileList">
								<%								
								for (FileBean bean : files) {
									String originalName = bean.getOriginalName();
									String extension = bean.getExtension();
								%>
								<p><%=originalName + "." + extension%></p>
								<%
								}
								%>
								</div>
							</div>
						</div>
					</div>
					</div>
				</div>
				<div class="empty-space"></div>
				<div class="board-button">
					<div>
						<button type="submit" class="btn btn-primary btn-lg">수정하기</button>
						<button type="button" class="btn btn-secondary btn-lg" data-id="<%=boardId%>" data-value="<%=postId%>">돌아가기</button>
					</div>
				</div>
				
			</form>
		</div>
	</div>
<script src="../js/common/editor.js"></script>
</body>
</html>