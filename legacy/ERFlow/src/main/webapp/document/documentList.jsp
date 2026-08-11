<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<%@page import="model.view.ViewDocumentBean"%>
<%@page import="java.util.Vector"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
final String PROGRAM_CODE = "F8DC1E7F6122BB87B532BB313D9B51294686A38717B78F97A90DE7FCB53D0F6C";

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
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>문서 관리</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/documentList.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/admin/admin.js"></script>
<script src="../js/main/documentList.js"></script>
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
				<span class="menu-name">사용자 > 문서 관리 > 문서 리스트</span>
			</div>

			<form action="documentDeleteProc.jsp" method="post" name="docFrm">

				<div class="table-header-container">
					<div class="button-group">
						<button type="button" class="btn btn-danger" id="delete-button" name="flag" value="delete">삭제</button>
						<button type="button" class="btn btn-primary" id="register-button">추가</button>
					</div>
					<div class="search-option">
						<select name="keyfield" id="">
							<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
							<option value="id" <%=keyfield.equals("id") ? "selected" : ""%>>문서ID</option>
							<option value="subject" <%=keyfield.equals("subject") ? "selected" : ""%>>문서명</option>
							<option value="template" <%=keyfield.equals("template") ? "selected" : ""%>>양식명</option>
						</select>
						<div class="main-search-container">
							<div class="main-search-border">
								<input class="main-search-text" type="search"
									placeholder="문서 검색" aria-label="Search" name="keyword" value="<%=keyword%>">
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
							<th scope="col">문서번호</th>
							<th scope="col">제목</th>
							<th scope="col">양식명</th>
							<th scope="col">생성시각</th>
							<th scope="col">수정시각</th>
							<th scope="col">문서 상태</th>
							<th scope="col">결재 상태</th>
						</tr>
					</thead>
					<tbody>
						<%
						// Pagenation

						final int pagePerBlock = 5;
						final int numPerPage = 15;

						int totalRecord = activityCon.getDocumentCount(keyfield, keyword, "user", user.getId());
						int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
						int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
						int nowPage = 1;

						if (request.getParameter("nowPage") != null) {
							nowPage = WebHelper.parseInt(request, "nowPage");
						}
						int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

						int start = (nowPage - 1) * numPerPage;

						Vector<ViewDocumentBean> units = activityCon.getDocumentViews(keyfield, keyword, "user", user.getId(), start, numPerPage);
						int cnt = numPerPage > units.size() ? numPerPage : units.size();

						// 문서 정보 출력
						for (int i = 0; i < cnt; ++i) {
							if (i == units.size()) {
								break;
							}
							ViewDocumentBean doc = units.get(i);

							long id = doc.getId();
							String subject = doc.getSubject();
							String template = doc.getTemplateName();
							String createdAt = doc.getCreatedAt();
							String updatedAt = doc.getUpdatedAt();
							int docStatus = doc.getDocumentStatus();
							int proposalStatus = doc.getProposalStatus();
						%>
						<tr>
							<th scope="col"><input type="checkbox" name="docId"
								value="<%=id%>"></th>
							<td><%=id%></td>
							<td><a href="documentRegister.jsp?flag=update&docId=<%=id%>"><%=subject%></a></td>
							<td><%=template != null ? template : "(빈 문서)"%></td>
							<td><%=createdAt%></td>
							<td><%=updatedAt != null ? updatedAt : "(수정하지 않음)"%></td>
							<td><%=docStatus%></td>
							<td><%=proposalStatus%></td>
						</tr>
						<%
						}
						%>
					</tbody>
					<tfoot>
						<td colspan="7"></td>
						<td></td>
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