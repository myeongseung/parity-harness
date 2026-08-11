<!-- deptUpdate.jsp -->
<%@page import="model.DepartmentBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page import="model.view.ViewPermissionBean"%>
<jsp:useBean id="adminCon" class="controller.AdminController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
String message = "등록에 실패하였습니다.";
String nextPage = "jobDeptList.jsp";
String deptName = null;
boolean result = false;
long permission = 0L;
int paramDeptId = -1;
int deptId = -1;

if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
if (request.getParameter("deptId") != null) {
	paramDeptId = WebHelper.parseInt(request, "deptId");
} else {
	response.sendRedirect("../../accessError.jsp");
	return;
}
if (paramDeptId != -1) {
	DepartmentBean dept = adminCon.getDept(paramDeptId);
	deptId = dept.getId();
	deptName = dept.getName();
}
%>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>부서 수정 페이지</title>
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/deptRegister.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="../../js/admin/location.js"></script>
<script src="../../js/admin/permissions.js"></script>
<script
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>
<body>
	<div class="container">
		<form action="deptUpdateProc.jsp" method="post">
			<h2>부서 수정</h2>
			<div>
				<label for="position">기존 부서 명칭: <%=deptName%></label>
				<input type="hidden" name="deptId" value="<%=deptId%>"> 
				<input type="text" id="position" name="deptName" value="<%=deptName%>">
			</div>


			<label for="input4">부서 주소:</label>
			<input type="text" id="sample6_postcode" placeholder="우편번호" name="postalCode" readonly>
			<input type="text" id="sample6_extraAddress" placeholder="동/읍/면" readonly>
			<button type="button" class="btn btn-primary btn_execDaumPostcode">우편 번호 찾기</button>
			<input type="text" id="sample6_address" placeholder="도로명 주소" name="address1" readonly><br>
			<input type="text" id="sample6_detailAddress" placeholder="상세주소" name="address2">

			<div class="checkbox-container">
				<label>부서 권한:</label><br>
				<div class="input-group">
					<input type="checkbox" id="chkAll">
					<label for="chkAll">전체 선택</label>
				</div>
				<br>
				<%
				Vector<ViewPermissionBean> depts = permissionCon.getDeptPermissions(null, null);
				
				for (ViewPermissionBean bean : depts) {
					if (deptId == bean.getClassId()) {
						permission = bean.getPermission();
						break;
					}
				}
				for (int i = 0; i < depts.size(); ++i) {
					ViewPermissionBean dept = depts.get(i);
					
					String beanName = dept.getClassName();
					int beanId = dept.getClassId();
					long level = dept.getLevel();
					
					if (deptId != beanId) {
				%>
					<div class="input-group">
						<input type="checkbox" id="permission<%=i + 1%>" name="permissions"
						value="<%=beanId%>" <%=((permission & level) != 0 ? "checked" : "")%>>
							<label for="permission<%=i + 1%>"><%=beanName%></label>
					</div>
				<%
					}
				}
				%>
				<button type="submit">수정하기</button>
			</div>
		</form>
	</div>
</body>
</html>
