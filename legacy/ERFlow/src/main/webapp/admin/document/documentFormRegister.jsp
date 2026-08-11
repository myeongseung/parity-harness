<!-- documentFromRegister.jsp -->
<%@page import="model.TemplateBean"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}

String flag = request.getParameter("flag");

if (flag == null) {
	flag = "insert";
}
String subject = "";
String content = "";
int id = -1;

if (flag.equals("update")) {
	String paramId = request.getParameter("id");
	
	if (paramId != null && !paramId.trim().equals("")) {
		id = WebHelper.parseInt(request, "id");
	}
	if (id != -1) {
		TemplateBean bean = adminCon.getTemplate(id);
		
		subject = bean.getSubject();
		content = bean.getContent();
	} else {
		response.sendRedirect("../../accessError.jsp");
		return;
	}
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>문서 양식 작성</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="//cdn.ckeditor.com/4.22.1/full/ckeditor.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer">
</script>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/admin.css">
<link rel="stylesheet" href="../../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../../css/admin/adminSidebar.css">
<link rel="stylesheet" href="../../css/admin/adminHeader.css">
<link rel="stylesheet" href="../../css/admin/userList.css">
<link rel="stylesheet" href="../../css/main/documentRegister.css">
<script src="../../js/admin/admin.js"></script>
<script src="../../js/admin/documentFormRegister.js"></script>
<script src="../../js/bootjs/bootstrap.js"></script>
</head>
<style>
	.form-control{
		width:300px;
	}
	.body{
		min-width: 1200px;
	}
</style>
<body>
	<%@ include file="../../admin/adminSide.jsp"%>
	<%@ include file="../../admin/adminHeader.jsp"%>

	<!-- 여기가 본문 페이지 -->
	<div class="body">
		<div class="right-align">
			<span class="menu-name">관리자 > 문서 관리 > 문서 양식 추가</span>
		</div>
		<div class="section">
			<form action="documentFormRegisterProc.jsp" method="post">
				<input type="hidden" name="id" value="<%=id%>">
				<input type="hidden" name="flag" value=<%=flag%>>
				<ul class="column">
					<li class="title">양식명</li>
					<li><input class="form-control form-control-lg" type="text" name="subject" value="<%=subject%>">
					</li>
				</ul>
				<textarea id="editor1" name="content" class="ckeditor-textarea"
					rows="10" cols="80"><%=content%></textarea>
				<input type="file" id="html-selector" accept=".htm, .html">
				<h2>
					<input type="submit" value="확인"/> <input type="reset" value="다시입력"/>
				</h2>
			</form>
		</div>
	</div>
	</div>
	</div>
	<!-------------------------------------------------------------------------------------------------->
	<script type="text/javascript">
		CKEDITOR.replace("editor1", {
			width : "80%",
			height : "400px",
		});
	</script>
</body>
</html>