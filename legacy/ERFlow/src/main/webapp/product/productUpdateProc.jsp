<!-- companyUpdateProc.jsp -->
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="product" class="model.ProductBean" />
<jsp:useBean id="productCon" class="controller.ProductController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
//UserBean user = WebHelper.getValidUser(session);

//if (user == null) {
//	response.sendRedirect("../permissionError.jsp");
//	return;
//}

final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"productId","productName", "count",  "type"
};
String message = "수정에 성공했습니다.";
String nextPage = "product.jsp";

boolean result = true;

for (String key : keys) {
	String value = request.getParameter(key);

	if (value == null) {
		result = false;
		break;
	}
	value = value.trim();
	parameters.put(key, value.equals("") ? null : value);
}
if (result) {
	product.setId(parameters.get("productId"));
	product.setName(parameters.get("productName"));
	product.setCount(Integer.parseInt(parameters.get("count")));
	product.setType(Integer.parseInt(parameters.get("type")));

	result = productCon.updateProduct(product);
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