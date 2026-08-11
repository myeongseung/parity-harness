<!-- companyUpdate.jsp -->
<%@page import="model.view.ViewTaskBean"%>
<%@page import="model.ProductBean"%>
<%@page import="model.TaskHistoryBean"%>
<%@page import="java.util.Vector"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<%@page import="model.UserBean"%>
<jsp:useBean id="taskCon" class="controller.TaskController" />
<jsp:useBean id="bean" class="model.TaskBean"/>
<jsp:useBean id="userCon" class="controller.UserController" />
<jsp:useBean id="productCon" class="controller.ProductController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
final String SELL_PROGRAM_CODE = "5D2B70044459C29621CC45B55113F427F933BDE9D43404D387ED8585EFE64AD9";
final String PURCHASE_PROGRAM_CODE = "3879BC2C91F0F2F88E7468299B546E2F2098B09186750212D2247F73306B0A79";

boolean isSell = false;
boolean isPurchase = false;

String paramId = request.getParameter("id"); 
int id = -1;

if (paramId != null && !paramId.trim().equals("")) {
	try {
		id = WebHelper.parseInt(request, "id");
	} catch (NumberFormatException e) {
		
	}
	if (id != -1) {
		bean = taskCon.getTask(id);
	}
}
if (bean == null) {
	return;
}
String program = request.getParameter("flag");
boolean flag = false;

isSell = program.equals("sell"); 
isPurchase = program.equals("purchase");

String programCode = isSell ? SELL_PROGRAM_CODE :
	(isPurchase ? PURCHASE_PROGRAM_CODE : "");

//권한 확인 코드
if (!WebHelper.isLogin(session) || programCode.equals("") ||
		!permissionCon.hasProgramPermission(session, programCode)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String userName = request.getParameter("userName");
String userId = bean.getUserId();
String companyName = request.getParameter("companyName");
int companyId = bean.getCompanyId();
int documentId = bean.getDocumentId();
String taskAt = bean.getTaskAt();
int status = bean.getStatus();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><%=isSell ? "수주" : "발주" %> 관리 수정 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script> <!-- Jquery -->
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="https://code.jquery.com/jquery-1.12.4.js"></script> <!-- datepicker -->
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script> <!-- datepicker -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet" href="../css/jquery-ui.css">
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/taskRegister.css">
<script
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/admin/location.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/main/task.js"></script>
<script src="../js/datepicker.js"></script>
<script src="../js/main/task.js"></script>
</head>
<body>
	<%@ include file="/indexHeader.jsp"%>
	<div class="content-wrap">
		<%@ include file="/indexSide.jsp"%>
		<!-- 여기가 본문 페이지 -->
		<div class="task-insert-wrap">
			<div class="right-align">
				<span class="menu-name">사용자 > <%=isSell ? "영업" : "구매"%> > <%=isSell ? "수주" : "발주" %> 수정</span>
			</div>
			<div class="task-insert">
				<div class="task-form">
					<form action="taskUpdateProc.jsp" method="post" name="taskFrm">
					<input type="hidden" name="flag" value=<%=program %>>
						<div class="form-group">
							<label for="taskId">의뢰 ID</label> <input type="text"
								id="taskId" name="taskId" placeholder="의뢰 ID"
								value="<%=id%>" readonly> 
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input2">담당 직원:</label> 
							<input type="text" id="userName"  name="userName" placeholder="담당직원명" value="<%=userCon.getUserView(userId).getName() %>" readonly>
							<input type="text" id="userId" name="userId" value="<%=userId%>"><br>
							<input type="button" class="btn btn-secondary btn-lg" id="find-user" value="직원 찾기">
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input1">제품:</label> <input type="button"
								class="btn btn-secondary btn-lg" id="find-multi-product"
								value="제품 찾기">
						</div>
						<div class="form-group d-grid gap-2" id="table-form">
							<table id="myTable" class="table table-striped">
								<tr>
								<thead>
									<tr>
										<th>제품번호</th>
										<th>이름</th>
										<th>수량</th>
									</tr>
								</thead>
								</tr>
							<%
							// Pagenation
							
							String taskId = request.getParameter("id");
							Vector<TaskHistoryBean> vlist =  taskCon.getTaskHistories("task_tbl_id",taskId, 0, 1000);
							// 수주 정보 출력
							for (int i = 0; i < vlist.size(); ++i) {
								TaskHistoryBean history = vlist.get(i);
								ProductBean product = productCon.getProduct(history.getProductId());
							%>
							<tr>
								<td><%=history.getProductId()%></td>
								<td><%=product.getName()%></td>
								<td><%=history.getCount()%></td>
							</tr>
							<%
							}
							%>
							</table>
						</div>
						<div class="form-group d-grid gap-2">
							<label for="input1">문서:</label> 
							<input type="text" id="documentId" name="documentId" placeholder="문서번호"  value="<%=documentId %>" readonly>
							<input type="button" class="btn btn-secondary btn-lg" id="find-document" value="문서 찾기">
						</div>
						<div class="form-group">
							<label for="input8">의뢰 시각</label> 
							<input type="text" id="datepicker" name="taskAt" placeholder="의뢰 시각">
						</div>
						<div class="form-group">
							<label for="input8">의뢰 상태</label> 
							<select name="status" id="" >
								<option value="1" >진행중</option>
								<option value="2">완료</option>
								<option value="3">미확인</option>
							</select>
						</div>
						<div class="form-group text-center">
							<button type="submit" class="form-Register-btn">제출</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>
