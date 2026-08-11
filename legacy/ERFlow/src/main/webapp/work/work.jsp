<!-- work.jsp -->
<%@page import="java.time.DayOfWeek"%>
<%@page import="java.util.TimeZone"%>
<%@page import="java.time.Duration"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.view.ViewWorkBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="model.view.ViewUserBean"%>
<%@page import="java.time.Period"%>
<%@page import="java.time.Month"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.YearMonth"%>
<%@page import="model.UserBean"%>
<%@page import="java.util.Vector"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<%
final String PROGRAM_CODE = "B02E912CD7BFFC1760E1C5C009F97386770E5A280C11425C4F94A7B2D11554EA";

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String paramDate = request.getParameter("date");

boolean flag = true;
int endOfDay = -1;

YearMonth current = YearMonth.now();
YearMonth minDate = YearMonth.of(current.getYear(), Month.JANUARY);
LocalDate endDate = current.atEndOfMonth();
LocalDate today = LocalDate.now();

if (paramDate != null && !paramDate.trim().equals("")) {
	try {
		current = YearMonth.parse(paramDate);
	} catch (Exception e) {
		e.printStackTrace();
		flag = false;
	}
}
if (flag) {
	endDate = current.atEndOfMonth();
	endOfDay = endDate.getDayOfMonth();
}
if (endOfDay == -1) {
	response.sendRedirect("../accessError.jsp");
	return;
}

// 검색 기능 지원
String keyfield = "";
String keyword = "";

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
<title>근태 확인 페이지</title>
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer">
</script>
<script src="https://code.jquery.com/jquery-latest.min.js"></script> <!-- Jquery -->
<link rel="stylesheet" href="../css/bootcss/bootstrap.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css"> <!-- datepicker -->
<script src="https://code.jquery.com/jquery-1.12.4.js"></script> <!-- datepicker -->
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script> <!-- datepicker -->
<link rel="stylesheet" href="../css/common/common.css">
<link rel="stylesheet" href="../css/common/page.css">
<link rel="stylesheet" href="../css/main/header.css">
<link rel="stylesheet" href="../css/main/aside.css">
<link rel="stylesheet" href="../css/main/index.css">
<link rel="stylesheet" href="../css/main/work.css">
<script src="../js/bootjs/bootstrap.js"></script>
<script src="../js/main/index.js"></script>
<script src="../js/work/work.js"></script>
</head>
<body>
	<div class="wrap">

		<%@include file="../indexHeader.jsp"%>
		<!-- indexHeader -->

		<!--  본문  -->
		<div class="content-wrap">
			<%@include file="../indexSide.jsp"%>
			<!-- indexSide -->

			<div class="main-content-wrap">
				<div class="article">
					<div class="content">

						<div class="right-align">
							<span class="menu-name">사용자 > 근태 관리 > 근태 확인</span>
						</div>
						<form name="workFrm">
							<div class="header">
								<div class="select-date">
									<input type="month" min="<%=minDate%>" value="<%=current%>">
									<button type="button" class="btn btn-light date-search">검색</button>
								</div>
								<div class="search-option">
									<select name="keyfield">
										<option value="">전체조회</option>
										<option value="dept">부서</option>
										<option value="name">이름</option>
									</select>

									<div class="main-search-container">
										<div class="main-search-border">
											<input class="main-search-text" type="search"
												placeholder="검색" aria-label="Search" name="keyword">
										</div>
										<div class="main-search-icon">
											<i class="fa-solid fa-magnifying-glass fa-lg" id="searchIcon"
												title="검색하기"></i>
										</div>
									</div>
								</div>
							</div>
						</form>

						<div class="section">
							<table class="table table-striped">
								<tr style="background-color: beige;">
									<th colspan="3">구분</th>
									<%
									for (int i = 1; i <= 16; ++i) {
										LocalDate ld = LocalDate.of(current.getYear(), current.getMonthValue(), i);
										boolean isSunday = ld.getDayOfWeek() == DayOfWeek.SUNDAY;
										boolean isSaturday = ld.getDayOfWeek() == DayOfWeek.SATURDAY;
									%>
										<th<%=isSunday ? " style=\"color: red;\"" : (isSaturday ? " style=\"color: blue;\"" : "")%>>
											<%=String.format("%02d", i)%>
											</th>
									<%
									}
									%>
									<th colspan="4">통계</th>

								</tr>
								<tr style="background-color: beige;">
									<th style="width: 5%;">성명</th>
									<th style="width: 4%;">부서</th>
									<th>직책</th>
									<%
									
									for (int i = 17; i <= 32; ++i) {
										if (i <= endOfDay) {
											LocalDate ld = LocalDate.of(current.getYear(), current.getMonthValue(), i);
											boolean isSunday = ld.getDayOfWeek() == DayOfWeek.SUNDAY;
											boolean isSaturday = ld.getDayOfWeek() == DayOfWeek.SATURDAY;
									%>
										<th<%=isSunday ? " style=\"color: red;\"" : (isSaturday ? " style=\"color: blue;\"" : "")%>>
											<%=String.format("%02d", i)%>
										</th>
									<%
										} else {
									%>
										<th></th>
									<%
										}
									}
									%>
									<th>정상</th>
									<th>지각</th>
									<th>조퇴</th>
									<th>연차</th>
								</tr>
								<%
								// Pagenation
								final int pagePerBlock = 5;
								final int numPerPage = 8;
										
								int totalRecord = activityCon.getUserTotalCount(
									keyfield.equals("dept") ? keyword : "",
									null,
									keyfield.equals("name") ? keyword : ""
								);
								int totalPage = (int) Math.ceil(1.0 * totalRecord / numPerPage);
								int totalBlock = (int) Math.ceil(1.0 * totalPage / pagePerBlock);
								int nowPage = 1;
		
								if (request.getParameter("nowPage") != null) {
									nowPage = WebHelper.parseInt(request, "nowPage");
								}
								int nowBlock = (int) Math.ceil(1.0 * nowPage / pagePerBlock);
		
								int start = (nowPage - 1) * numPerPage;
		
								Vector<ViewUserBean> users = activityCon.getUserViews(
									keyfield.equals("dept") ? keyword : "",
									null,
									keyfield.equals("name") ? keyword : "",
									start,
									numPerPage
								);
								final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
								
								// 사용자 정보 출력
								for (int i = 0; i < numPerPage; ++i) {
									if (i == users.size()) {
										break;
									}
									final HashMap<LocalDate, Integer> map = new HashMap<>();
									
									ViewUserBean userView = users.get(i);
									String userId = userView.getId();
									String name = userView.getName();
									String deptName = userView.getDeptName();
									String jobName = userView.getJobName();
									
									Vector<ViewWorkBean> works = activityCon.getWorkViews(userId, current.toString());
									
									for (int j = 0; j < works.size(); ++j) {
										ViewWorkBean work = works.get(j);
										
										LocalDateTime ldtStart = work.getStartedAt() != null ?
												LocalDateTime.parse(work.getStartedAt(), dtf) :
												LocalDateTime.MIN;
										
										if (ldtStart == LocalDateTime.MIN) {
											continue;
										}
										LocalDate ld = ldtStart.toLocalDate();
										
										map.put(ld, j);
									}
								%>
								<tr>
									<td rowspan="2"><%=name%></td>
									<td rowspan="2"><%=deptName%></td>
									<td rowspan="2"><%=jobName%></td>
									<%
									int normalCount = 0;
									int lateCount = 0;
									int leaveCount = 0;
									double vacationCount = 0f;
									
									for (int j = 1; j <= 16; ++j) {
										LocalDate ld = LocalDate.of(current.getYear(), current.getMonthValue(), j);
										Period period = Period.between(ld, today);
										
										if (map.containsKey(ld) && period.getMonths() >= 0 && period.getDays() >= 0) {
											int key = map.get(ld);
											ViewWorkBean work = works.get(key);
											
											LocalDateTime startedAt = work.getStartedAt() != null ?
													LocalDateTime.parse(work.getStartedAt(), dtf) :
													LocalDateTime.now();
											LocalDateTime endedAt = work.getEndedAt() != null ?
													LocalDateTime.parse(work.getEndedAt(), dtf) :
													LocalDateTime.now();
											Duration d = null;
											
											boolean isSunday = ld.getDayOfWeek() == DayOfWeek.SUNDAY;
											boolean isSaturday = ld.getDayOfWeek() == DayOfWeek.SATURDAY;
											int status = work.getStatus();
											String message = "결근";
											String colorStyle = "";
											
											switch (status) {
												case 0:
													// 결근인데, 주말에는 체크를 하지 않아야 함.
													if (isSunday || isSaturday) {
														message = "";
													}
													colorStyle = " style=\"color: #E51A2E; font-weight: bold;\"";
													break;
												case 1:
													// 근무 중인 상태
													d = Duration.between(startedAt, endedAt)
														.minusHours(1L);
												
													message = String.format("%02d:%02d",
										                d.toHoursPart(), 
										                d.toMinutesPart());
												case 2:
													// 정상 퇴근
													d = Duration.between(startedAt, endedAt)
														.minusHours(1L);
													
													message = String.format("%02d:%02d",
										                d.toHoursPart(), 
										                d.toMinutesPart());
													++normalCount;
													break;
												case 3:
													// 조퇴
													message = "조퇴";
													++leaveCount;
													colorStyle = " style=\"color: #8507F7; font-weight: bold;\"";
													break;
												case 4:
													// 지각
													if (isSunday || isSaturday) {
														message = "";
													} else {
														message = "지각";
														++lateCount;
														colorStyle = " style=\"color: #F06F0A; font-weight: bold;\"";
													}
													break;
												case 5:
													// 반차
													if (isSunday || isSaturday) {
														message = "";
													} else {
														message = "반차";
														vacationCount += 0.5f;
														colorStyle = " style=\"color: #5F2700; font-weight: bold;\"";
													}
													break;
												case 6:
													// 연차
													message = "연차";
													vacationCount += 1.0f;
													colorStyle = " style=\"color: #9B9696; font-weight: bold;\"";
													break;
											}
											
									%>
										<td<%=colorStyle%>><%=message%></td>
									<%
										} else {
									%>
										<td></td>
									<%
										}
									}
									%>
									<td><%=normalCount%></td>
									<td><%=lateCount%></td>
									<td><%=leaveCount%></td>
									<td><%=vacationCount%></td>
								</tr>
								<tr>
									<%
									normalCount = 0;
									lateCount = 0;
									leaveCount = 0;
									vacationCount = 0f;
									
									for (int j = 17; j <= 32; ++j) {
										if (j <= endOfDay) {
											LocalDate ld = LocalDate.of(current.getYear(), current.getMonthValue(), j);
											Period period = Period.between(ld, today);
											
											if (map.containsKey(ld) && period.getMonths() >= 0 && period.getDays() >= 0) {
												int key = map.get(ld);
												ViewWorkBean work = works.get(key);
												
												LocalDateTime startedAt = work.getStartedAt() != null ?
														LocalDateTime.parse(work.getStartedAt(), dtf) :
														LocalDateTime.now();
												LocalDateTime endedAt = work.getEndedAt() != null ?
														LocalDateTime.parse(work.getEndedAt(), dtf) :
														LocalDateTime.now();
												Duration d = null;
												
												boolean isSunday = ld.getDayOfWeek() == DayOfWeek.SUNDAY;
												boolean isSaturday = ld.getDayOfWeek() == DayOfWeek.SATURDAY;
												int status = work.getStatus();
												String message = "결근";
												String colorStyle = "";
												
												switch (status) {
													case 0:
														// 결근인데, 주말에는 체크를 하지 않아야 함.
														if (isSunday || isSaturday) {
															message = "";
														}
														colorStyle = " style=\"color: #E51A2E; font-weight: bold;\"";
														break;
													case 1:
														// 근무 중인 상태
														d = Duration.between(startedAt, endedAt)
															.minusHours(1L);
													
														message = String.format("%02d:%02d",
											                d.toHoursPart(), 
											                d.toMinutesPart());
													case 2:
														// 정상 퇴근
														d = Duration.between(startedAt, endedAt)
															.minusHours(1L);
														
														message = String.format("%02d:%02d",
											                d.toHoursPart(), 
											                d.toMinutesPart());
														++normalCount;
														break;
													case 3:
														// 조퇴
														if (isSunday || isSaturday) {
															message = "";
														} else {
															message = "조퇴";
															++leaveCount;
															colorStyle = " style=\"color: #8507F7; font-weight: bold;\"";
														}
														break;
													case 4:
														// 지각
														if (isSunday || isSaturday) {
															message = "";
														} else {
															message = "지각";
															++lateCount;
															colorStyle = " style=\"color: #F06F0A; font-weight: bold;\"";
														}
														break;
													case 5:
														// 반차
														if (isSunday || isSaturday) {
															message = "";
														} else {
															message = "반차";
															vacationCount += 0.5f;
															colorStyle = " style=\"color: #5F2700; font-weight: bold;\"";
														}
														break;
													case 6:
														// 연차
														message = "연차";
														vacationCount += 1.0f;
														colorStyle = " style=\"color: #9B9696; font-weight: bold;\"";
														break;
												}
												
										%>
											<td<%=colorStyle%>><%=message%></td>
									<%
											} else {
									%>
												<td></td>
									<%
											}
										} else {
									%>
											<td></td>
									<%
										}
									}
									%>
									<td><%=normalCount%></td>
									<td><%=lateCount%></td>
									<td><%=leaveCount%></td>
									<td><%=vacationCount%></td>
								</tr>
								<%}%>
							</table>
						</div>
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
						<form name="readFrm">
							<input type="hidden" name="nowPage" value="<%=nowPage%>">
							<input type="hidden" name="date" value="<%=current%>">
							<input type="hidden" name="keyfield">
							<input type="hidden" name="keyword">
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>