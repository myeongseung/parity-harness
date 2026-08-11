<!-- boundRegisterProc.jsp -->
<%@page import="model.BoundBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="boundCon" class="controller.BoundController" />
<%
final String INBOUND_PROGRAM_CODE = "8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0";
final String OUTBOUND_PROGRAM_CODE = "E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7";

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"productId", "userId", "postalCode", "address1", "address2", "boundedAt", "count", "flag"
};
String message = " 정보를 등록하지 못했습니다.";
String nextPage = "inbound.jsp";
boolean isValid = true;
boolean result = false;

for (String key : keys) {
	String value = request.getParameter(key);
	
	if (value == null) {
		isValid = false;
		break;
	}
	parameters.put(key, value.trim());
}
String flag = null;
boolean isInbound = false;

String programCode = isInbound ? INBOUND_PROGRAM_CODE : OUTBOUND_PROGRAM_CODE;

//프로그램 권한이 있는가?
if (!permissionCon.hasProgramPermission(session, programCode)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

if (isValid) {
	BoundBean bound = new BoundBean();
	
	flag = parameters.get("flag");
	isInbound = flag.equals("inbound");
	
	bound.setProductId(parameters.get("productId"));
	bound.setUserId(parameters.get("userId"));
	bound.setPostalCode(parameters.get("postalCode"));
	bound.setAddress1(parameters.get("address1"));
	bound.setAddress2(parameters.get("address2"));
	bound.setBoundedAt(parameters.get("boundedAt"));
	bound.setCount(Integer.parseInt(parameters.get("count")));
	
	if (isInbound) {
		result = boundCon.createBound(bound, 0);
		nextPage = "inbound.jsp";
	} else {
		result = boundCon.createBound(bound, 1);
		nextPage = "outbound.jsp";
	}
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
if (result) {
	message = " 정보를 등록하였습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=(isInbound ? "입고" : "출고") + message%>');
		location.href = '<%=nextPage%>';
	</script>
</body>