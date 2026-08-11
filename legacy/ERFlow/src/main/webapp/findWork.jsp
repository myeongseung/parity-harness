<!-- findWork.jsp -->
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="repository.FieldCodeRepository" %>
<%@page contentType="text/html; charset=UTF-8"%>
<%
FieldCodeRepository repository = FieldCodeRepository.getInstance();
List<Entry<String, String>> fieldList = new ArrayList<>(repository.getEntries());
String search = null;

fieldList.sort(Entry.comparingByValue());

if (request.getParameter("search") != null) {
	search = request.getParameter("search");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>업종 찾기</title>
<link rel="stylesheet" href="css/main/findBank.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="js/main/findWork.js"></script>
</head>
<body>
	<form action="findWork.jsp">
		<div class="container">
			<div class="search-box">
				<input type="text" placeholder="예) 업체 코드 or 업체 명 검색" name="search"
					id="findWork-input" value="<%=search == null ? "" : search%>">
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
					<strong>업체 코드</strong><br> 예) 010301 -> 어로 어업
				</div>
				<div class="tip-item">
					<strong>업체 명</strong><br> 예) 수산물 가공 및 저장 처리업 -> 031002
				</div>
			</div>
			<%
			} else {
			%>
			<div>
				<%
				for (Entry<String, String> info : fieldList) {
					String key = info.getKey();
					String value = info.getValue();
					boolean flag = search.equals("");

					if (flag || key.contains(search) || value.contains(search)) {
				%>
				<p>
					<a href="javascript:void(0);" class="a-work-info" data-key="<%=key%>" data-value="<%=value%>"><%=key%> <%=value%></a>
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
				Powered by 권명승<br> © 업체 코드 찾기 서비스 제공
			</div>
		</div>
		<input type="hidden" id="workCode" name="workCode">
		<input type="hidden" id="workName" name="workName">
	</form>
</body>
</html>
