<%@page import="model.view.ViewBoundBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page import="model.UserBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<jsp:useBean id="boundCon" class="controller.BoundController" />
<%
final String PROGRAM_CODE = "8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0";

// 권한 확인 코드
if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;	
}
// 검색 기능 지원
String keyfield = "";
String keyword = "";

// 만약 사용자를 검색하려고 하는 시도가 있다면
if (request.getParameter("keyfield") != null) {
	keyfield = request.getParameter("keyfield");
	keyword = request.getParameter("keyword");
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>입고 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="../css/bootcss/bootstrap.css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/bound.css"/>
<script src="../js/main/index.js"></script>
<script src="../js/main/bound.js"></script>
<script src="../js/bootjs/bootstrap.js"></script>
</head>

<body>
	<div class="wrap">
		<%@include file="../indexHeader.jsp"%>
		<!-- indexHeader -->
		
		<!--  본문  -->
		<div class="content-wrap">
			<%@include file="../indexSide.jsp"%>
			<!-- indexSide -->
			
			<!--본문 내용 작성----------------------------------------------------------------->
			<div class="table-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > 구매 > 입고 제품 관리 </span>
			</div>

			<form action="boundDeleteProc.jsp" method="post" name="boundFrm">
				<input type="hidden" name="flag" value="inbound">
				<div class="table-header-container">
					<div class="button-group">
						<button type="button" class="btn btn-danger" id="delete-button" name="inbound" value="delete">삭제</button>
						<button type="button" class="btn btn-primary" id="register-button" data-value="inbound">추가</button>
					</div>
					<div class="search-option">
						<select name="keyfield" id="">
							<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
							<option value="id" <%=keyfield.equals("id") ? "selected" : ""%>>제품ID</option>
							<option value="productName" <%=keyfield.equals("productName") ? "selected" : ""%>>제품명</option>
							<option value="userName" <%=keyfield.equals("userName") ? "selected" : ""%>>입고자</option>
							<option value="date" <%=keyfield.equals("date") ? "selected" : ""%>>날짜</option>
						</select>

						<div class="main-search-container">
							<div class="main-search-border">
								<input class="main-search-text" type="search"
									placeholder="검색" aria-label="Search" name="keyword" value="<%=keyword%>">
							</div>
							<div class="main-search-icon">
								<i class="fa-solid fa-magnifying-glass fa-lg" title="검색하기"></i>
							</div>
						</div>
					</div>
				</div>


				<table class="table table-striped">
					<thead>
						<tr>
							<th scope="col"><input type="checkbox" id="chkAll"></th>
							<th scope="col">번호</th>
							<th scope="col">제품명</th>
							<th scope="col">입고자</th>
							<th scope="col">주소</th>
							<th scope="col">입고 시간</th>
							<th scope="col">수량</th>
						</tr>
					</thead>
					<tbody>
						<%
						// Pagenation

						final int pagePerBlock = 5;
						final int numPerPage = 15;

						int totalRecord = boundCon.getBoundCount(keyfield, keyword, 0);
						int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
						int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
						int nowPage = 1;

						if (request.getParameter("nowPage") != null) {
							nowPage = WebHelper.parseInt(request, "nowPage");
						}
						int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

						int start = (nowPage - 1) * numPerPage;
						
						Vector<ViewBoundBean> bounds = boundCon.getBounds(keyfield, keyword, start, numPerPage, 0);
						int cnt = numPerPage > bounds.size() ? numPerPage : bounds.size();
						// 협력업체 정보 출력
						for (int i = 0; i < cnt; ++i) {
							if (i == bounds.size())
								break;
							ViewBoundBean bound = bounds.get(i);

							int id = bound.getId();
							String productName = bound.getProductName();
							String userName = bound.getUserName();
							String address = bound.getAddress1() + " " + bound.getAddress2();
							String boundedAt = bound.getBoundedAt();
							int count = bound.getCount();
						%>
						<tr>
							<th scope="col"><input type="checkbox" name="boundId" value="<%=id%>"></th>
							<td><a href="boundUpdate.jsp?flag=inbound&id=<%=id%>"><%=id%></a></td>
							<td><%=productName%></td>
							<td><%=userName%></td>
							<td><%=address%></td>
							<td><%=boundedAt%></td>
							<td><%=count%></td>
						</tr>
						<%
						}
						%>
					</tbody>
					<tfoot>
						<td colspan="6"></td>
						<td></td>
					</tfoot>
				</table>

			</form>
			<form name="readFrm" method="post">
				<input type="hidden" name="nowPage" value="<%=nowPage%>">
				<input type="hidden" name="keyfield" value="<%=keyfield%>">
				<input type="hidden" name="keyword" value="<%=keyword%>">
			</form>

			<!-- 페이징 -->
			<div class="page-controller">
				<%
				if (nowBlock > 1) {
				%>
				<a href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock - 1%>')" class="disabled">이전</a>
				<%
				}
				int pageStart = (nowBlock - 1) * pagePerBlock + 1;
				int pageEnd = pageStart + pagePerBlock;
				pageEnd = pageEnd <= totalPage ? pageEnd : totalPage + 1;
				
				%>
				<ul class="page-numbers">
					<%
					for (; pageStart < pageEnd; ++pageStart) {
					%>
					<a href="javascript:paging('<%=pageStart%>')"
						<%=(pageStart == nowPage) ? "class=\"disabled\"" : ""%>>
						<li><%=pageStart%></li>
					</a>
					<%
					}
					%>
				</ul>
				<%
				if (nowBlock < totalBlock) {
				%>
				<a href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock + 1%>')">다음</a>
				<%
				}
				%>
			</div>
		</div>
	</div>
</body>
</html>