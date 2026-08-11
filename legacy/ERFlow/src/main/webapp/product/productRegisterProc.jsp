<!-- .jsp -->
<%@page import="java.util.HashMap"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissonCon" class="controller.PermissionController" />
<jsp:useBean id="productCon" class="controller.ProductController" />
<jsp:useBean id="product" class="model.ProductBean" />
<%
//UserBean user = WebHelper.getValidUser(session);

//if (user == null) {
//	response.sendRedirect("../permissionError.jsp");
//	return;
//}

final HashMap<String, String> parameters = new HashMap<>();
final String[] keys = new String[] {
	"productId", "productName", "count"
};
String message = "등록에 성공했습니다.";

String nextPage = "";
int type = WebHelper.parseInt(request, "type");

if (type == 0) {
	nextPage = "ingredientProduct.jsp";
} else if (type == 1) {
	nextPage = "processedProduct.jsp";
} else if (type == 2) {
	nextPage = "productedProduct.jsp";
} 

boolean result = true;

for (String key : keys) {
	String value = request.getParameter(key);
	
	if (value == null) {
		result = false;
		break;
	}
	value = value.trim();
	parameters.put(key, value);
}
if (result) {
	product.setId(parameters.get("productId"));
	product.setName(parameters.get("productName"));
	product.setCount(Integer.parseInt(parameters.get("count")));
	product.setType(type);
	
	result = productCon.createProduct(product);
}
if (!result) {
	message = "등록에 실패했습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = '<%=nextPage%>'; 
	</script>
</body>