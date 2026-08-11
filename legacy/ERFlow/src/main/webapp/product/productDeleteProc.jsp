<!-- companyDeleteProc.jsp -->
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="java.io.PrintWriter"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="productCon" class="controller.ProductController"/>
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
boolean isValid = true;

String message = "삭제에 성공했습니다.";
String[] productId = request.getParameterValues("productId");
String type = request.getParameter("type");
String nextPage = type+"Product.jsp";

if (productId == null) {
	isValid = false;
}
if (isValid) {
	for (String id : productId) {
		isValid &= productCon.deleteProduct(id);
	}
} else {
	message = "삭제에 실패했습니다.";
}
%>
<body>

<script type="text/javascript">
	alert('<%=message%>');
	location.href = '<%=nextPage%>';
</script>
</body>