<!-- boundUpdateProc.jsp -->
<%@page import="model.BoundBean"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="boundCon" class="controller.BoundController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
final String INBOUND_PROGRAM_CODE = "8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0";
final String OUTBOUND_PROGRAM_CODE = "E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7";

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

String program = request.getParameter("flag");
boolean flag = false;

if (program.equals("inbound")) {
	flag = true;
}
String programCode = flag ? INBOUND_PROGRAM_CODE : OUTBOUND_PROGRAM_CODE;

//프로그램 권한이 있는가?
if (!permissionCon.hasProgramPermission(session, programCode)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"boundId", "productId", "userId", "postalCode", "address1", "address2", "boundedAt", "count"
};
String message = "수정에 성공했습니다.";
String nextPage = flag ? "inbound.jsp" : "outbound.jsp";

boolean result = true;

for (String key : keys) {
	String value = request.getParameter(key);
	
	if (value == null) {
		result = false;
		break;
	}
	parameters.put(key, value);
}

if (result) {
	BoundBean bound = new BoundBean();
	bound.setId(Integer.parseInt(parameters.get("boundId")));
	bound.setProductId(parameters.get("productId"));
	bound.setUserId(parameters.get("userId"));
	bound.setPostalCode(parameters.get("postalCode"));
	bound.setAddress1(parameters.get("address1"));
	bound.setAddress2(parameters.get("address2"));
	bound.setBoundedAt(parameters.get("boundedAt"));
	bound.setCount(Integer.parseInt(parameters.get("count")));

	result = boundCon.updateBound(bound, flag ? 0 : 1);
}

if (!result) {
	message = "수정에 실패했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = '<%=nextPage%>';
	</script>
</body>