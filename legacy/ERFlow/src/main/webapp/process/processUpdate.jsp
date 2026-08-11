<!-- processUpdate.jsp -->
<%@page contentType="text/html; charset=UTF-8"%>
<%
String id = "";
String name = "";

boolean flag = true;
if (request.getParameter("id") == null || request.getParameter("name") == null) {
	flag = false;
}

id = request.getParameter("id");
name = request.getParameter("name");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>공정명 변경</title>

<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/main/findBank.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/process/processUpdate.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="../js/process/processUpdate.js"></script>
</head>
<body>
	<div class="container">
		<form action="processUpdateProc.jsp" method="post"
			name="processUpdateFrm">
			<input type="hidden" name="id" value="<%=id%>">
			<div class="form-group">
				<h5 class="procoess-update-title">공정명 변경</h5>
			</div>
			<label for="processName">공정명:</label>
			<div class="modal-body index-process-content">
				<div class="input-group mb-3">
					<input type="text" class="form-control process-explanation process-subject"
						name="processName" value="<%=name%>">
				</div>
			</div>
			<div class="modal-footer index-process-button-group">
				<button type="submit" class="btn btn-primary process-set"
					id="update">설정</button>
			</div>
		</form>
	</div>
</body>
</html>
