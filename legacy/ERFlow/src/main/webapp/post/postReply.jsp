<!-- postReply.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.BoardBean"%>
<%@page import="model.UserBean"%>
<%@page import="model.view.ViewPostBean"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
String paramBoardId = request.getParameter("boardId");
String paramPostId = request.getParameter("postId");
int boardId = -1;
int postId = -1;

// Parameter 처리 
if (paramBoardId != null && !paramBoardId.trim().equals("") &&
	paramPostId != null && !paramPostId.trim().equals("")) {
	boardId = WebHelper.parseInt(request, "boardId");
	postId = WebHelper.parseInt(request, "postId");
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
ViewPostBean post = activityCon.getPostView(session, boardId, postId);

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
<script src="../js/main/index.js"></script>
<script src="../js/post/postReply.js"></script>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<script src="//cdn.ckeditor.com/4.22.1/full/ckeditor.js"></script>
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/post/postRegister.css">
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
			<form method="post" action="postReplyProc.jsp" id="board-write">
				<input type="hidden" name="boardId" value="<%=boardId%>">
				<input type="hidden" name="id" value="<%=postId%>">
				<div class="board-bodyeditor">
					<div class="empty-space">&nbsp;</div>
					<div class="empty-space"></div>
					<div class="empty-space">&nbsp;</div>
					<div class="empty-space"></div>

					<div class="write-header-wrap">
						<div class="write-header">
							<input type="text" class="write-header-type" value="<%=boardName%>" readonly>
							<input type="text" name="subject" class="write-header-subject" required>
						</div>
					</div>
				</div>
				<div class="write-body">
					<textarea name="content" id="editor1" class="ckeditor-textarea" rows="10" cols="80"></textarea>
				</div>
				<div class="write-header-wrap">
					<div class="write-header">
					<div class="left-align">
						<div>
							<input type="file" name="filename" class="write-header-type" 
							multiple onchange="handleFileSelect(event)">
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
						<button type="submit" class="btn btn-primary btn-lg">답변하기</button>
						<button type="button" class="btn btn-secondary btn-lg" data-id="<%=boardId%>">돌아가기</button>
					</div>
				</div>
			</form>
		</div>
	</div>
<script type="text/javascript">
function handleFileSelect(event) {
    var fileList = document.getElementById("fileList");
    var files = event.target.files; // 사용자가 선택한 파일들을 가져옴
    
    if(files.length > 0){
    	listbox.style.display="block";
    }else{
    	listbox.style.display="none";
    }
    fileList.innerHTML = "";
    
    for (var i = 0; i < files.length; i++) {
        var file = files[i];
        var fileName = document.createElement("div");
        fileName.textContent = file.name;
        fileList.appendChild(fileName);    
    }
}
</script>
</body>
<script src="../js/common/editor.js"></script>
</html>