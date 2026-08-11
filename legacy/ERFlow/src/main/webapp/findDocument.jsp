<!-- findWork.jsp -->
<%@page import="java.util.Optional"%>
<%@page import="model.UserBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.view.ViewDocumentBean"%>
<%@page import="model.DocumentBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map.Entry"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="proposalCon" class="controller.ProposalController"/>
<%

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("permissionError.jsp");
	return;
}
String search = Optional.ofNullable(request.getParameter("search")).orElse("");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>문서 찾기</title>
<link rel="stylesheet" href="css/main/findBank.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script src="js/main/findDocument.js"></script>
</head>
<body>
	<form action="findDocument.jsp">
		<div class="container">
			<div class="search-box">
				<input type="text" placeholder="예) 문서 ID or 문서명 검색" name="search"
					id="findDocument-input" value="<%=search == null ? "" : search%>">
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
					<strong>문서 ID</strong><br> 예) 20010 -> 21년 하반기 결산문서
				</div>
				<div class="tip-item">
					<strong>문서 명</strong><br> 예) 21년 하반기 결산문서 -> 20010
				</div>
			</div>
			<%
			} else {
			%>
			<div>
				<%
				Vector<ViewDocumentBean> vlist = activityCon.getDocumentViews(null, null, "user", user.getId());

				for (ViewDocumentBean document : vlist) {
					long key = document.getId();
					long searchKey = -1;
					String value = document.getSubject();
					
					try {
						searchKey = Long.parseLong(search);
					} catch (NumberFormatException e) {
						
					}
					if (!proposalCon.hasProposal(document.getId()) &&
						(searchKey == key || value.contains(search))) {
				%>
				<p>
					<a href="javascript:void(0)" class="a_document_info" data-key="<%=key%>" data-value="<%=value%>" > <%=key%> <%=value%> </a>
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
				Powered by 권명승<br> © 문서 찾기 서비스 제공
			</div>
		</div>
	</form>
</body>
</html>
