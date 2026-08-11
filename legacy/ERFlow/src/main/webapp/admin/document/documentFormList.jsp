<!-- documentFormList.jsp -->
<%@page import="model.TemplateBean"%>
<%@page import="java.util.Vector"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="adminCon" class="controller.AdminController" />
<jsp:useBean id="permissionCon" class="controller.PermissionController" />
<%
if (!permissionCon.isAdmin(session)) {
	response.sendRedirect("../../permissionError.jsp");
	return;
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>문서 양식 리스트</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="../../css/common/common.css">
<link rel="stylesheet" href="../../css/admin/admin.css">
<link rel="stylesheet" href="../../css/bootcss/bootstrap2.css">
<link rel="stylesheet" href="../../css/admin/adminSidebar.css">
<link rel="stylesheet" href="../../css/admin/adminHeader.css">
<link rel="stylesheet" href="../../css/admin/documentFormList.css">
<link rel="stylesheet" href="../../css/admin/documentModal.css">
<script src="../../js/bootjs/bootstrap.js"></script>
<script src="../../js/admin/admin.js"></script>
<script src="../../js/admin/documentFormList.js"></script>
</head>
<body>
	<%@ include file="../../admin/adminSide.jsp"%>
	<%@ include file="../../admin/adminHeader.jsp"%>
	<!-- 여기가 본문 페이지 -->
	<div class="table-wrap">
		<div class="right-align">
			<span class="menu-name">관리자 > 문서 관리 > 문서 양식 리스트</span>
		</div>
		<!-- 삭제만 submit 추가는 새 페이지 -->
		<form action="documentFormDeleteProc.jsp" method="post" name="docFrm">
			<div class="table-header-container">
				<div class="button-group">
					<button type="submit" class="btn btn-danger" name="flag"
						value="delete">삭제</button>
					<button type="button" class="btn btn-primary" id="btn-register">추가</button>
				</div>
			</div>
			<table class="table table-striped">
				<thead>
					<tr>
						<th><input type="checkbox" id="chkAll"></th>
						<th scope="col" style="width: 20%">문서 번호</th>
						<th scope="col" style="width: 30%">문서 제목</th>
						<th scope="col" style="width: 50%">내용 보기</th>
					</tr>
				</thead>
				<tbody>
					<%
					Vector<TemplateBean> vlist = adminCon.getTemplates();

					for (int i = 0; i < vlist.size(); ++i) {
						TemplateBean bean = vlist.get(i);

						int id = bean.getId();
						String subject = bean.getSubject();
						String content = bean.getContent();
					%>
					<tr>
						<td><input type="checkbox" name="templateId" value=<%=id%>></td>
						<td><a href="documentFormRegister.jsp?flag=update&id=<%=id%>"><%=id%></a></td>
						<td><%=subject%></td>
						<td><button type="button" class="btn btn-primary"
								data-bs-toggle="modal" data-bs-target="#calendar-plus">보기</button>
							<div class="modal fade" id="calendar-plus"
								data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1"
								aria-labelledby="calendar-plus-label" aria-hidden="true">
								<div class="modal-dialog">
									<div class="modal-content">
										<span class="close-button">×</span>
										<div class="document-content"><%=content%></div>
									</div>
								</div>
							</div>
						</td>
					</tr>
					<%
					}
					%>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="3"></td>
						<td></td>
					</tr>
				</tfoot>
			</table>
		</form>
	</div>
</body>
</html>
