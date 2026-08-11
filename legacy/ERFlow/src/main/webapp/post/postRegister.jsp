<!-- postRegister.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="model.BoardBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
String paramBoardId = request.getParameter("boardId");
int boardId = -1;

if (paramBoardId != null) {
	boardId = WebHelper.parseInt(request, "boardId");
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || !permissionCon.hasBoardWritePermission(session, boardId)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
BoardBean board = activityCon.getBoard(boardId);

String boardName = board.getSubject();
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
<script src="../js/post/postRegister.js"></script>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<script src="//cdn.ckeditor.com/4.22.1/full/ckeditor.js"></script>
<script src="../js/main/index.js"></script>
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
			<form method="post" action="postRegisterProc.jsp" id="board-write" enctype="multipart/form-data">
			<input type="hidden" name="boardId" value="<%=boardId%>">
				<div class="board-bodyeditor">
					<div class="empty-space">&nbsp;</div>
					<div class="empty-space"></div>
					<div class="empty-space">&nbsp;</div>
					<div class="empty-space"></div>
					
					<div class="write-header-wrap">
						<div class="write-header">
							<input type="text" class="write-header-type" value="자유게시판"
								readonly> <input type="text" name="subject"
								class="write-header-subject" placeholder="제목을 입력해주세요" required>
						</div>
					</div>
				</div>
				<div class="write-body">
					<textarea name="content" id="editor1" class="ckeditor-textarea"
						rows="10" cols="80">
				</textarea>
				</div>
				<div class="write-header-wrap">
					<div class="write-header">
					<div class="left-align">
						<div>
							<input type="file" name="filename" class="write-header-type" multiple>
						</div>
						<div id="listbox" style="display:none">
							<div id="listfile">
								첨부파일 :
								<div class="center-align" id="fileList"></div>
							</div>
						</div>
					</div>
					</div>
				</div>
				<div class="empty-space"></div>
				<div class="board-button">
					<div>
						<button type="submit" class="btn btn-primary btn-lg">게시하기</button>
						<button type="button" class="btn btn-secondary btn-lg" data-id="<%=boardId%>">돌아가기</button>
					</div>
				</div>	
			</form>
		</div>
	</div>
</body>
<script src="../js/common/editor.js"></script>
</html>