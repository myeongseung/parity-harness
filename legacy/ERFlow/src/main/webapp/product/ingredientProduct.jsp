<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="model.ProductBean"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="productCon" class="controller.ProductController"/>
<%
final String PROGRAM_CODE = "BC58BCABC3239263B4D3838A0673BA319C845BEAD96EFFEF412C3349960FE0B2";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || 
		!permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
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
<title>원재료 관리 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="../css/bootcss/bootstrap.css" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/product.css"/>
<script src="../js/main/index.js"></script>
<script src="../js/main/product.js"></script>
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
				<span class="menu-name">사용자 > 구매 > 원재료 관리</span>
			</div>

			<form action="productDeleteProc.jsp" method="post" name="productFrm">
			<input type="hidden" name="type" value="ingredient">
				<div class="table-header-container">
					<div class="button-group">
						<button type="button" class="btn btn-danger" id="delete-button"value="delete">삭제</button>
						<button type="button" class="btn btn-primary" id="register-button" data-value="ingredient">추가</button>

					</div>
					<div class="search-option">
						<select name="keyfield">
							<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
							<option value="productId" <%=keyfield.equals("productId") ? "selected" : ""%>>제품ID</option>
							<option value="productName" <%=keyfield.equals("productName") ? "selected" : ""%>>제품명</option>
						</select>

						<div class="main-search-container">
							<div class="main-search-border">
								<input class="main-search-text" type="search" placeholder="검색" aria-label="Search" name="keyword" value="<%=keyword%>">
							</div>
							<div class="main-search-icon">
								<!-- <i class="fa-solid fa-magnifying-glass fa-lg" onclick="search()" title="검색하기"></i> -->
								<i class="fa-solid fa-magnifying-glass fa-lg" title="검색하기"></i>
							</div>
						</div>
					</div>
				</div>

				<table class="table table-striped">
					<thead>
						<tr>
							<th scope="col"><input type="checkbox" id="chkAll"></th>
							<th scope="col">제품 ID</th>
							<th scope="col">이름</th>
							<th scope="col">수량</th>
							<th scope="col">제품 분류</th>
						</tr>
					</thead>
					<tbody>
						<%
						// Pagenation

						final int pagePerBlock = 5;
						final int numPerPage = 15;

						int totalRecord = productCon.getProductCount(keyfield, keyword, "ingredient");
						int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
						int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
						int nowPage = 1;

						if (request.getParameter("nowPage") != null) {
							nowPage = WebHelper.parseInt(request, "nowPage");
						}
						int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

						int start = (nowPage - 1) * numPerPage;
						
						Vector<ProductBean> products = productCon.getProducts(keyfield, keyword, start, numPerPage, "ingredient");
						int cnt = numPerPage > products.size() ? numPerPage : products.size();
						// 원재료 정보 출력
						for (int i = 0; i < cnt; ++i) {
							if (i == products.size())
								break;
							ProductBean product = products.get(i);
							String id = product.getId();
							String name = product.getName();
							int count = product.getCount();
							int type = product.getType();
						%>
						<tr>
							<th scope="col"><input type="checkbox" name="productId" value="<%=id%>"></th>
							<td><a href="productUpdate.jsp?flag=ingredient&id=<%=id%>"><%=id%></a></td>
							<td><%=name%></td>
							<td><%=count%></td>
							<td>원재료</td>
						</tr>
						<%
						}
						%>
					</tbody>
					<tfoot>
						<td colspan="5"></td>
					</tfoot>
				</table>

			</form>
			<form name="readFrm">
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
					<li>
						<a href="javascript:paging('<%=pageStart%>')" <%=(pageStart == nowPage) ? "class=\"disabled\"" : ""%>>
							<%=pageStart%>
						</a>
					</li>
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