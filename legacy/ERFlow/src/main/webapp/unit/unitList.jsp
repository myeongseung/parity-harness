<!-- unitList.jsp -->
<!-- 
	@author 권명승 & 장진원
	@version 1.1.3
	@see
 -->
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page import="model.UserBean"%>
<%@page import="model.view.ViewUnitBean"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
final String PROGRAM_CODE = "8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2";

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
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>생산 설비 관리 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/unitList.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/main/unitList.js"></script>
</head>
<body>
	<!-- 여기까지 -->
	<%@include file="/indexHeader.jsp"%>
	<!-- indexHeader -->

	<!--  본문  -->
	<div class="content-wrap">
		<%@include file="/indexSide.jsp"%>
		<!-- indexSide -->

		<!-- 여기가 본문 페이지 -->
		<div class="table-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > 생산관리 > 생산 설비 관리</span>
			</div>

			<form action="unitDeleteProc.jsp" method="post" name="unitFrm">

				<div class="table-header-container">
					<div class="button-group">
						<button type="button" class="btn btn-danger" id="delete-button" name="flag" value="delete">삭제</button>
						<button type="button" class="btn btn-primary" id="register-button">추가</button>
					</div>
					<div class="search-option">
						<select name="keyfield" id="">
							<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
							<option value="id" <%=keyfield.equals("id") ? "selected" : ""%>>장비ID</option>
							<option value="name" <%=keyfield.equals("name") ? "selected" : ""%>>장비명</option>
							<option value="charger" <%=keyfield.equals("charger") ? "selected" : ""%>>관리자명</option>
							<option value="document" <%=keyfield.equals("document") ? "selected" : ""%>>문서명</option>
							<option value="status" <%=keyfield.equals("status") ? "selected" : ""%>>장비 상태</option>
							<option value="date" <%=keyfield.equals("date") ? "selected" : ""%>>장비 제조일자</option>
						</select>
						<div class="main-search-container">
							<div class="main-search-border">
								<input class="main-search-text" type="search"
									placeholder="장비 검색" aria-label="Search" name="keyword">
							</div>
							<div class="main-search-icon">
								<i class="fa-solid fa-magnifying-glass fa-lg" id="searchIcon"
									title="검색하기"></i>
							</div>
						</div>
					</div>
				</div>

				<table class="table table-striped">
					<thead>
						<tr>
							<th scope="col"><input type="checkbox" id="chkAll"></th>
							<th scope="col">장비ID</th>
							<th scope="col">장비명</th>
							<th scope="col">관리자</th>
							<th scope="col">장비 상태</th>
							<th scope="col">장비 제조일자</th>
							<th scope="col"></th>
						</tr>
					</thead>
					<tbody>
						<%
						// Pagenation

						final int pagePerBlock = 5;
						final int numPerPage = 15;

						int totalRecord = activityCon.getUnitCount(keyfield, keyword);
						int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
						int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
						int nowPage = 1;

						if (request.getParameter("nowPage") != null) {
							nowPage = WebHelper.parseInt(request, "nowPage");
						}
						int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

						int start = (nowPage - 1) * numPerPage;

						Vector<ViewUnitBean> units = activityCon.getUnits(keyfield, keyword, start, numPerPage);
						int cnt = numPerPage > units.size() ? numPerPage : units.size();

						// 장비 정보 출력
						for (int i = 0; i < cnt; ++i) {
							if (i == units.size()) {
								break;
							}
							ViewUnitBean unit = units.get(i);

							String id = unit.getUnitId();
							String chargerId = unit.getUserName();
							String documentId = unit.getDocumentName();
							String name = unit.getUnitName();
							int status = unit.getStatus();
							String createAt = WebHelper.getDate(unit.getCreatedAt());
						%>
						<tr>
							<th scope="col"><input type="checkbox" name="unitId"
								value="<%=id%>"></th>
							<td><%=id%></td>
							<td><%=name%></td>
							<td><%=chargerId%></td>
							<td><%=status%></td>
							<td><%=createAt%></td>
							<td><button type="button" id="update-button" data-id="<%=id%>">수정</button></td>
						</tr>
						<%
						}
						%>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="7"></td>
						</tr>
					</tfoot>
				</table>
			</form>

			<form name="readFrm">
				<input type="hidden" name="nowPage" value="<%=nowPage%>">
				<input type="hidden" name="keyfield" value="">
				<input type="hidden" name="keyword" value="">
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