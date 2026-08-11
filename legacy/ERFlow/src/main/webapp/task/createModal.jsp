<!-- createModal.jsp -->
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="model.ProductBean"%>
<%@page import="model.TaskHistoryBean"%>
<%@page import="java.util.Vector"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="taskCon" class="controller.TaskController" />
<jsp:useBean id="productCon" class="controller.ProductController" />
<%
if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
int taskId = 0;

if (request.getParameter("taskId") != null) {
	try {
	taskId = WebHelper.parseInt(request, "taskId");
	} catch (NumberFormatException e) {
		
	}
}
if (taskId == 0) {
	response.sendRedirect("../accessError.jsp");
	return;
}
Vector<TaskHistoryBean> historys = taskCon.getTaskHistories("task_tbl_id", request.getParameter("taskId"), 0, 1000);
Vector<ProductBean> products = new Vector<>();

if (historys != null) {
	for (TaskHistoryBean history : historys) {
		products.addElement(productCon.getProduct(history.getProductId()));
	}
}
%>
<%@page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>수/발주 제품 등록 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<script src="../js/main/taskHistory.js"></script>

</head>
<body>
	<div class="modal-content">
		<div id="table-content">
			<button type="button" class="btn btn-danger" id="cancel">종료</button>
			<div class="proposal-right-wrap">
				<div class="proposal-comment-list">
					<table class="table table-striped">
						<thead>
							<tr>
								<th class="product-number">제품 번호</th>
								<th class="product-name">제품명</th>
								<th class="product-quantity">수량</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 0; i < historys.size(); i++) {
							%>
							<tr>
								<td><%=products.get(i).getId()%></td>
								<td><%=products.get(i).getName()%></td>
								<td><%=historys.get(i).getCount()%></td>
							</tr>
							<%
							}
							%>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="3"></td>
							</tr>
						</tfoot>
					</table>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
