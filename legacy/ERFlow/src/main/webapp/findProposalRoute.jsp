<!-- findWork.jsp -->
<%@page import="model.view.ViewUserBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="model.view.ViewProposalRouteBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map.Entry"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="proposalRouteCon" class="controller.ProposalRouteController"/>
<%
UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}

Vector<ViewProposalRouteBean> vlist = proposalRouteCon.getProposalRouteViews("", "", 0, 1000, user.getId());
HashMap<Integer, String> fieldRoute = new HashMap<>();

for (int i = 0; i < vlist.size(); ++i) {
	ViewProposalRouteBean route = vlist.get(i);
	fieldRoute.put(route.getId(), route.getNickname());
}
List<Entry<Integer, String>> routeList = new ArrayList<>(fieldRoute.entrySet());
String search = null;

routeList.sort(Entry.comparingByValue());

if (request.getParameter("search") != null) {
	search = request.getParameter("search");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>결재라인 찾기</title>
<link rel="stylesheet" href="css/main/findBank.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="js/main/findProposalRoute.js"></script>
</head>
<body>
	<form action="findProposalRoute.jsp">
		<div class="container">
			<div class="search-box">
				<input type="text" name="search"
					id="findProposalRoute-input" value="<%=search == null ? "" : search%>" placeholder="예)결재라인명을 검색해주세요.">
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
				for (Entry<Integer, String> info : routeList) {
					int key = info.getKey();
					String value = info.getValue();
					boolean flag = search.equals("");
					
					ViewProposalRouteBean route = proposalRouteCon.getProposalRouteView(key);
					Vector<ViewUserBean> routeUsers = route.getRoute();
					String routeString = String.join(";", routeUsers.stream().map(mapper -> String.format("%s", mapper.getId())).toList());

					if (flag || value.contains(search)) {
				%>
				<p>
					<a href="javascript:void(0);" class="a_route_info" data-key="<%=key%>" data-value="<%=routeString%>" data-name="<%=value%>"><%=key%> <%=value%></a>
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
				Powered by 권명승<br> © 협력업체 찾기 서비스 제공
			</div>
		</div>
		<input type="hidden" id="routeId" name="routeId">
		<input type="hidden" id="route" name="route">
	</form>
</body>
</html>
