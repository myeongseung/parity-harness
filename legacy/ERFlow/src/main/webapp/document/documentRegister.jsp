<!-- documentRegister.jsp -->
<%@page import="java.util.Vector"%>
<%@page import="model.TemplateBean"%>
<%@page import="model.view.ViewDocumentBean"%>
<%@page import="helper.WebHelper"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
final String PROGRAM_CODE = "4BB57A61E4CE88D4416CD611F74308C927913669E7FE78AE4106D01A9ABAB75A";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || 
		!permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String paramFlag = request.getParameter("flag");
String paramDocId = request.getParameter("docId");
String paramTemplate = request.getParameter("template");
long docId = -1;

if (paramFlag == null) {
	paramFlag = "insert";
}
ViewDocumentBean bean = null;
String docUser = null;

if (paramDocId != null && !paramDocId.trim().equals("")) {
	docId = WebHelper.parseLong(request, "docId");
	bean = activityCon.getDocumentView(docId);
	docUser = bean.getUserId();
}
if (paramFlag.equals("update") && !user.getId().equals(docUser)) {
	response.sendRedirect("../accessError.jsp");
	return;
}
String content = "";
String subject = "";

if (bean != null) {
	content = bean.getContent();
	subject = bean.getSubject();
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>문서 작성</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="//cdn.ckeditor.com/4.22.1/full/ckeditor.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer">
</script>
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/index.css">
<link rel="stylesheet" href="../css/main/documentList.css">
<link rel="stylesheet" href="../css/main/documentRegister.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/main/documentRegister.js"></script>
</head>
<body>
	<div class="wrap">
		<%@include file="/indexHeader.jsp"%>
		<!-- indexHeader -->

		<!--  본문  -->
		<div class="content-wrap">
			<%@include file="/indexSide.jsp"%>
			<!-- indexSide -->
			
			<!-- 여기가 본문 페이지 -->
			 <div class="body">
			 	<div class="right-align">
	           		<span class="menu-name">사용자 > 문서 관리 > 기안 작성</span>
	         	</div>
		        <div class="section">
		            <form action="documentRegisterProc.jsp" name="writeFrm" method="post">
		            	<input type="hidden" name="flag" value="<%=paramFlag%>">
		            	<input type="hidden" name="id" value="<%=docId%>">
		                <ul class="column">
		                    <li class="title">양식분류</li>
		                    <li>
		                        <select class="select" name="templateId">
		                        	<option value="0">빈 문서</option>
		                        	<%
		                        	Vector<TemplateBean> templates = activityCon.getTemplates();
		                        	
		                        	for (TemplateBean template : templates) {
		                        		int templateId = template.getId();
		                        		boolean isChecked = String.valueOf(templateId).equals(paramTemplate);
		                        		String templateSubject = template.getSubject();
		                        		
		                        		if (isChecked) {
		                        			content = template.getContent();
		                        		}
		                        	%>
		                        	<option value="<%=templateId%>" <%=isChecked ? "selected" : ""%>><%=templateSubject%></option>
		                            <%
		                            }
		                            %>
		                        </select>
		                    </li>
		                    <li class="title">제목</li>
		                    <li>
		                        <input type="text" name="subject" class="input-width" value="<%=subject%>"/>
		                    </li>
		                </ul>
		                <textarea id="editor1" name="content" class="ckeditor-textarea" rows="10" cols="80"><%=content%></textarea>
		                <h2>
		                    <input type="submit" value="확인"/>
		                    <input type="reset" value="다시입력"/>
		                </h2>
		            </form>
					<form name="readFrm">
						<input type="hidden" name="flag" value=<%=paramFlag%>>
		            	<input type="hidden" name="id" value=<%=docId%>>
						<input type="hidden" name="template">
					</form>
		        </div>
		    </div>
		</div>
	</div>
	<!-------------------------------------------------------------------------------------------------->
	<script type="text/javascript">
		CKEDITOR.replace("editor1", {
			width: "80%",
			height: "400px",
		});
	</script>
</body>
</html>