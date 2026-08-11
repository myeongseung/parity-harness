<!-- findWork.jsp -->
<%@page import="java.util.HashMap"%>
<%@page import="model.ProductBean"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="repository.FieldCodeRepository"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="productCon" class="controller.ProductController" />
<%
Vector<ProductBean> vlist = productCon.getAllProducts();
HashMap<String, String> fieldProduct = new HashMap<>();

for (int i = 0; i < vlist.size(); ++i) {
	ProductBean product = vlist.get(i);
	fieldProduct.put(product.getId(), product.getName());
}
List<Entry<String, String>> productList = new ArrayList<>(fieldProduct.entrySet());
String search = null;

productList.sort(Entry.comparingByValue());

if (request.getParameter("search") != null) {
	search = request.getParameter("search");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>제품 찾기</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="css/main/findBank.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="css/bootcss/bootstrap.css">
<script src="js/main/findMultiProduct.js"></script>
</head>
<body>
	<form action="">
		<div class="container">
			<div class="search-box">
				<input type="text" placeholder="예) 제품 코드 or 제품 명 검색" name="search"
					id="findWork-input" value="<%=search == null ? "" : search%>">
				<button type="submit">검색</button>
			</div>
			<div class="select-box">
				<button type="button" class="btn btn-primary" id="select-button">확인</button>
			</div>
			<hr>
			<%
			if (search == null) {
			%>
			<div class="tips">
				<h3>tip</h3>
				<p>아래와 같은 조합으로 검색하면 더욱 정확한 검색 결과가 제공됩니다.</p>

				<div class="tip-item">
					<strong>제품 코드</strong><br> 예) 031002 -> 환공어묵
				</div>
				<div class="tip-item">
					<strong>제품 명</strong><br> 예) 환공어묵 -> 031002
				</div>
			</div>
			<%
			} else {
			%>
			<div class="product">
				<table class="search-people-body table">
					<thead>
						<tr>
							<th><input type="checkbox" id="chkAll"></th>
							<th>제품번호</th>
							<th>이름</th>
						</tr>
					</thead>

					<tbody>
						<%
						for (Entry<String, String> info : productList) {
							String key = info.getKey();
							String value = info.getValue();
							boolean flag = search.equals("");

							if (flag || key.contains(search) || value.contains(search)) {
						%>
						<tr>
							<td class="search-people-body-check"><input name="productId"
								type="checkbox" value="<%=key%>"></td>
							<td class="search-people-body-product-id"><%=key%></td>
							<td class="search-people-body-product"><%=value%></td>
						</tr>
						<%
						}
						}
						%>
					
			</div>
			<%
			}
			%>

			<div class="footer">
				Powered by 권명승<br> © 제품 코드 찾기 서비스 제공
			</div>
		</div>
		<input type="hidden" id="productId" name="productId"> <input
			type="hidden" id="productName" name="productName">
	</form>
</body>
</html>
