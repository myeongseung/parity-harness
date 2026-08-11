<!-- companyList.jsp -->
<%@page import="java.util.Optional"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.Vector"%>
<%@page import="model.CompanyBean"%>
<%@page import="model.UserBean"%>
<%@page import="repository.FieldCodeRepository"%>
<jsp:useBean id="companyCon" class="controller.CompanyController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
final String IN_PROGRAM_CODE = "4F6B21882D7C310CC96B01D478A381D0DCF410B574B7185E6EA449173834A561";
final String OUT_PROGRAM_CODE = "90739BBD5C68921E8D32D44EAB220654A1C51710FDE97F6639DD0CFCBB91E2DB";

//검색 기능 지원
String paramFlag = request.getParameter("flag");
String programId = "";
String keyfield = "";
String keyword = "";

FieldCodeRepository repository = FieldCodeRepository.getInstance();

//만약 사용자를 검색하려고 하는 시도가 있다면
if (request.getParameter("keyfield") != null) {
	keyfield = request.getParameter("keyfield");
	keyword = request.getParameter("keyword");
}
if (paramFlag != null && !paramFlag.trim().equals("")) {
	switch (paramFlag) {
		case "0":
			programId = OUT_PROGRAM_CODE;
			break;
		case "1":
			programId = IN_PROGRAM_CODE;
			break;
	}
}
int flag = 2;

if (!WebHelper.isLogin(session) && programId.equals("")) {
	flag = 2;
} else if (permissionCon.hasProgramPermission(session, programId)) {
	switch (programId) {
		case OUT_PROGRAM_CODE:
			flag = 0;
			break;
		case IN_PROGRAM_CODE:
			flag = 1;
			break;
	}
}
if (flag == 2) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>협력업체 관리 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/companyList.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/company.js"></script>
<script src="../js/main/index.js"></script>
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
				<span class="menu-name">사용자 > <%=(flag == 0 ? "영업" : "구매")%> > 협력업체 관리</span>
			</div>

			<form action="companyDeleteProc.jsp" method="post" name="companyFrm">
				<input type="hidden" name="flag" value="<%=flag%>">
				<div class="table-header-container">
					<div class="button-group">
						<button type="button" class="btn btn-danger" id="delete-button"value="delete">삭제</button>
						<button type="button" class="btn btn-primary" id="register-button" data-id="<%=flag%>">추가</button>
					</div>
					
					<div class="search-option">
						<select name="keyfield" id="">
							<option value="" <%=keyfield.equals("") ? "selected" : ""%>>전체조회</option>
							<option value="name" <%=keyfield.equals("name") ? "selected" : ""%>>협력업체명</option>
							<option value="field" <%=keyfield.equals("field") ? "selected" : ""%>>업체 종목</option>
						</select>

						<div class="main-search-container">
							<div class="main-search-border">
								<input class="main-search-text" type="search"
									placeholder="업체 검색" aria-label="Search" name="keyword" value="<%=keyword%>">
							</div>
							<div class="main-search-icon">
								<i class="fa-solid fa-magnifying-glass fa-lg" id="searchIcon"
									title="검색하기">
								</i>
							</div>
						</div>
					</div>
					
				</div>


				<table class="table table-striped">
					<thead>
						<tr>
							<th scope="col"><input type="checkbox" id="chkAll"></th>
							<th scope="col">번호</th>
							<th scope="col">이름</th>
							<th scope="col">주소</th>
							<th scope="col">전화 번호</th>
							<th scope="col">사업자 번호</th>
							<th scope="col">종목</th>
						</tr>
					</thead>
					<tbody>
						<%
						// Pagenation

						final int pagePerBlock = 5;
						final int numPerPage = 15;

						int totalRecord = companyCon.companyCount(keyfield, keyword, flag);
						
						int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
						int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
						int nowPage = 1;

						if (request.getParameter("nowPage") != null) {
							nowPage = WebHelper.parseInt(request, "nowPage");
						}
						int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);

						int start = (nowPage - 1) * numPerPage;

						Vector<CompanyBean> companies = companyCon.getCompanies(keyfield, keyword, flag, start, numPerPage);
						int cnt = numPerPage > companies.size() ? numPerPage : companies.size();

						// 협력업체 정보 출력
						for (int i = 0; i < cnt; ++i) {
							if (i == companies.size())
								break;
							
							CompanyBean company = companies.get(i);

							if (company.getSubcontract() == flag) {
							int id = company.getId();
							String name = company.getName();
							String address1 = company.getAddress1();
							String address2 = company.getAddress2();
							String phone = company.getPhone();
							String businesscode = company.getBusinessCode();
							String field = Optional.ofNullable(repository.getFieldName(company.getField())).orElse("");
						%>
						<tr>
							<th scope="col"><input type="checkbox" name="companyId"
								value="<%=id%>"></th>
							<td><a href="companyUpdate.jsp?flag=<%=flag%>&id=<%=id%>"><%=id%></a></td>
							<td><%=name%></td>
							<td><%=address1 + " " + address2%></td>
							<td><%=phone%></td>
							<td><%=businesscode%></td>
							<td><%=field%></td>
						</tr>
						<%
							}
						}
						%>
					</tbody>
					<tfoot>
						<td colspan="6"></td>
						<td></td>
					</tfoot>
				</table>

			</form>
			<form name="readFrm">
				<input type="hidden" name="flag" value=<%=flag%>>
				<input type="hidden" name="nowPage" value="<%=nowPage%>">
				<input type="hidden" name="keyfield" value="<%=keyfield%>">
				<input type="hidden" name="keyword" value="<%=keyword%>">
			</form>

			<!-- 페이징 -->
			<div class="page-controller">
				<%
				if (nowBlock > 1) {
				%>
				<a href="javascript:block('<%=pagePerBlock%>', '<%=nowBlock - 1%>')"
					class="disabled">이전</a>
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