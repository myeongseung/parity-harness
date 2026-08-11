<!-- findWork.jsp -->
<%@page import="model.CompanyBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map.Entry"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="companyCon" class="controller.CompanyController"/>
<%
Vector<CompanyBean> vlist = companyCon.getCompanies();
HashMap<Integer, String> fieldCompany = new HashMap<>();

for (int i = 0; i < vlist.size(); ++i) {
	CompanyBean company = vlist.get(i);
	fieldCompany.put(company.getId(), company.getName());
}
List<Entry<Integer, String>> companyList = new ArrayList<>(fieldCompany.entrySet());
String search = null;

companyList.sort(Entry.comparingByValue());

if (request.getParameter("search") != null) {
	search = request.getParameter("search");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>협력업체 찾기</title>
<link rel="stylesheet" href="css/main/findBank.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="js/main/findCompany.js"></script>
</head>
<body>
	<form action="findCompany.jsp">
		<div class="container">
			<div class="search-box">
				<input type="text" placeholder="예) 협력업체 ID or 협력업체 검색" name="search"
					id="findCompany-input" value="<%=search == null ? "" : search%>">
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
					<strong>협력업체 ID</strong><br> 예) 1 -> 삼성
				</div>
				<div class="tip-item">
					<strong>협력업체 명</strong><br> 예) 삼성 -> 1
				</div>
			</div>
			<%
			} else {
			%>
			<div>
				<%
				for (Entry<Integer, String> info : companyList) {
					int key = info.getKey();
					String value = info.getValue();
					boolean flag = search.equals("");

					if (flag || value.contains(search)) {
				%>
				<p>
					<a href="javascript:void(0);" class="a_company_info" data-key="<%=key%>" data-value="<%=value%>"><%=key%> <%=value%></a>
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
				Powered by 권명승시치<br> © 협력업체 찾기 서비스 제공
			</div>
		</div>
		<input type="hidden" id="companyId" name="companyId">
		<input type="hidden" id="companyName" name="companyName">
	</form>
</body>
</html>
