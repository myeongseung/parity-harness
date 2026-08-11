<!-- boardRegister.jsp -->
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시판 생성</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/common/page.css">
<link rel="stylesheet" href="../../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../../css/admin/boardRegister.css">
<script src="../../js/admin/boardRegister.js"></script>
<script src="../../js/bootjs/bootstrap.js"></script>
</head>
<body>
	<div class="message-wrap">
		<div class="readmessage-body">
			<form name="boardFrm" action="boardRegisterProc.jsp" method="post">
				<table class="readmessage-info">
					<tr>
						<td>
							<div class="input-group mb-3">
							  <div class="new-name"><span class="input-group-text">게시판 이름</span></div>
							  <input type="text" name="subject" class="form-control new-name" placeholder="** 게시판">
							</div>
						</td>
					</tr>
				</table>
				<hr>
				<div class="readmessage-footer">
					<div class="readmessage-footer-button">
				    	<button class="btn btn-primary" type="submit">생성</button>
				    	<button class="btn btn-danger cancel-board" type="button">취소</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>