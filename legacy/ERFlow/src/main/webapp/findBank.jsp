<!-- findBank.jsp -->
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="repository.BankCodeRepository"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%
BankCodeRepository repository = BankCodeRepository.getInstance();
List<Entry<String, String>> bankList = new ArrayList<>(repository.getEntries());
String search = null;

bankList.sort(Entry.comparingByValue());

if (request.getParameter("search") != null) {
	search = request.getParameter("search");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>은행 찾기</title>
<link rel="stylesheet" href="css/main/findBank.css">
<script src="js/main/findBank.js"></script>
</head>
<body>
	<form action="findBank.jsp">
		<div class="container">
			<div class="search-box">
				<input type="text" placeholder="예) 은행 코드 or 은행 명 검색" name="search"
					id="findBack-input" value="<%=search == null ? "" : search%>">
				<button type="submit">검색</button>
			</div>
			<hr>
			<%
			if (search == null) {
			%>
			<div class="tips">
				<h3>tip</h3>
				<p>아래와 같은 조합으로 검색하면 더욱 정확한 검색 결과가 제공됩니다.</p>

				<div class="tip-item">
					<strong>은행 코드</strong><br> 예) 002 -> 산업은행
				</div>
				<div class="tip-item">
					<strong>은행 명</strong><br> 예) 기업은행 -> 003
				</div>
			</div>
			<%
			} else {
			%>
			<div>
				<%
				for (Entry<String, String> info : bankList) {
					String key = info.getKey();
					String value = info.getValue();
					boolean flag = search.equals("");

					if (flag || key.contains(search) || value.contains(search)) {
				%>
				<p>
					<a href="javascript:void(0);"
						onclick="selectBank('<%=key%>', '<%=value%>')"><%=key%> <%=value%></a>
				</p>
				<%
				}
				}
				%>
			</div>
			<%
			}
			%>
			<hr>

			<div class="footer">
				Powered by 권명승<br> © 은행 코드 찾기 서비스 제공
			</div>
		</div>
		<input type="hidden" id="bankCode" name="bankCode"> <input
			type="hidden" id="bankName" name="bankName">
	</form>
</body>
</html>

