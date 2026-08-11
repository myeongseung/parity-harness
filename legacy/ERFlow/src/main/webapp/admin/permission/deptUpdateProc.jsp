<!-- deptUpdateProc.jsp -->
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page import="model.DepartmentBean"%>
<%@page import="model.view.ViewPermissionBean"%>
<jsp:useBean id="adminCon" class="controller.AdminController" scope="page" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" scope="page" />
<%
	final String[] keys = { "deptId", "deptName" };
	String[] permissions = request.getParameterValues("permissions");
	HashMap<String, String> parameters = new HashMap<>();
	boolean accessable = true;
	
	if (!permissionCon.isAdmin(session)) {
		response.sendRedirect("../../permissionError.jsp");
		return;
	}
	// Parameter Null 조건 확인 구문
	for (String key : keys) {
		String value = request.getParameter(key);
		
		if (value == null) {
			accessable = false;
			break;
		}
		parameters.put(key, value);
	}
	if (!accessable) {
		response.sendRedirect("../../accessError.jsp");
		return;
	}
	Vector<ViewPermissionBean> vlist = permissionCon.getDeptPermissions(null, null);
	String message = "부서 정보를 수정하지 못했습니다.";
	boolean flag = false;
	long result = 0L;
	int id = WebHelper.parseInt(request, "deptId");
	
	for (ViewPermissionBean bean : vlist) {
		if (bean.getClassId() == id) {
			result |= bean.getLevel();
			break;
		}
	}
	// 권한 조합하기
	if (permissions != null) {
		for (String permission : permissions) {
			int current = Integer.parseInt(permission);
			
			for (ViewPermissionBean bean : vlist) {
				if (bean.getClassId() == current) {
					result |= bean.getLevel();
				}
			}
		}
	}
	// 이름과 권한을 여기서 수정한다.
	DepartmentBean prevDept = adminCon.getDept(id);
	
	if (!adminCon.hasDept(parameters.get("deptName")) ||
			prevDept.getName().equals(parameters.get("deptName"))) {
		prevDept.setAddress1(request.getParameter("address1"));
		prevDept.setAddress2(request.getParameter("address2"));
		prevDept.setName(parameters.get("deptName"));
		prevDept.setPostalCode(request.getParameter("postalCode"));
		// 먼저 부서 정보를 수정하고,
		flag = adminCon.updateDept(session, prevDept);
		// 권한을 수정한다.
		flag &= permissionCon.changeDeptPermission(session, prevDept, result);
	}	
	if (flag) {
		message = "부서 정보를 수정했습니다.";
	}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'jobDeptList.jsp';
	</script>
</body>