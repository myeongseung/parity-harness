<!-- profile.jsp -->
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Vector"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="model.view.ViewWorkBean"%>
<%@page import="java.time.Period"%>
<%@page import="java.time.Duration"%>
<%@page import="java.time.DayOfWeek"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.Month"%>
<%@page import="java.time.YearMonth"%>
<%@page import="java.util.Optional"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="activityCon" class="controller.ActivityController"/>
<jsp:useBean id="permissionCon" class="controller.PermissionController"/>
<jsp:useBean id="userCon" class="controller.UserController"/>
<%
final String PROGRAM_CODE = "4A7849864C3B8D4067BC853C67BCFA572B7DFE68BBC773372BC14393B1F6C5B8";

UserBean currentUser = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session) || !permissionCon.hasProgramPermission(session, PROGRAM_CODE)) {
	response.sendRedirect("permissionError.jsp");
	return;
}
UserBean user = null;
String id = request.getParameter("id");

if (id != null) {
	user = userCon.getUser(id);
}
if (user == null) {
	response.sendRedirect("accessError.jsp");
	return;
}
String name = user.getName();
String email = Optional.ofNullable(user.getEmail()).orElse("");
String mobilePhone = Optional.ofNullable(user.getMobilePhone()).orElse("");
String extPhone = Optional.ofNullable(user.getExtensionPhone()).orElse("");
String address = Optional.ofNullable(user.getAddress1()).orElse("") + " " +
	Optional.ofNullable(user.getAddress2()).orElse("");
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
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title><%=name%>님의 프로필</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/2.9.2/umd/popper.min.js"
	integrity="sha512-2rNj2KJ+D8s1ceNasTIex6z4HWyOnEYLVC3FigGOmyQCZc2eBXKgOxQmo3oKLHyfcj53uz4QMsRCWNbLd32Q1g=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<link rel="stylesheet" href="css/bootcss/bootstrap.css" />
<link rel="stylesheet" href="css/common/common.css" />
<link rel="stylesheet" href="css/main/header.css" />
<link rel="stylesheet" href="css/main/index.css" />
<link rel="stylesheet" href="css/main/aside.css" />
<link rel="stylesheet" href="css/profile.css" />
<script src="js/bootjs/bootstrap.js"></script>
<script src="js/main/index.js"></script>
<script src="js/main/profile.js"></script>
</head>
<body style="background-color: #d0d0d0;">
	<!-- 여기까지 -->
	<%@include file="indexHeader.jsp"%>
	<!-- indexHeader -->

	<!--  본문  -->
	<div class="content-wrap">
		<%@include file="indexSide.jsp"%>
		<!-- indexSide -->
		<div class="container">
			<div class="main-body">

				<!-- Breadcrumb -->
				<div class="right-align">
					<span class="menu-name">사용자 > <%=name%>님의 프로필</span>
				</div>
				<!-- /Breadcrumb -->

				<!-- 프로필 카드 -->
				<div class="row gutters-sm">
					<div class="col-md-4 mb-3">
						<div class="card">
							<div class="card-body">
								<div class="d-flex flex-column align-items-center text-center">
									<img src="https://bootdey.com/img/Content/avatar/avatar7.png"
										alt="Admin" class="rounded-circle" width="150">
									<div class="mt-4">
										<h4><%=name%></h4>
										<p class="text-secondary mb-1">중요한 것은</p>
										<p class="text-muted font-size-sm">꺾이지 않는 마음!</p>
										<button class="btn btn-primary" id="search-button">주소록</button>
										<button class="btn btn-outline-primary message-btn" data-id="<%=id%>">쪽지 </button>
									</div>
								</div>
							</div>
						</div>
						<!-- 아래카드 -->
						<div class="card mt-3">
							<ul class="list-group list-group-flush">
								<li
									class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
									<h6 class="mb-0">
										<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
											viewBox="0 0 24 24" fill="none" stroke="currentColor"
											stroke-width="2" stroke-linecap="round"
											stroke-linejoin="round"
											class="feather feather-globe mr-2 icon-inline">
											<circle cx="12" cy="12" r="10"></circle>
											<line x1="2" y1="12" x2="22" y2="12"></line>
											<path
												d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path></svg>
										Website
									</h6> <span class="text-secondary">https://bootdey.com</span>
								</li>
								<li
									class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
									<h6 class="mb-0">
										<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
											viewBox="0 0 24 24" fill="none" stroke="currentColor"
											stroke-width="2" stroke-linecap="round"
											stroke-linejoin="round"
											class="feather feather-github mr-2 icon-inline">
											<path
												d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22"></path></svg>
										Github
									</h6> <span class="text-secondary">bootdey</span>
								</li>
								<li
									class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
									<h6 class="mb-0">
										<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
											viewBox="0 0 24 24" fill="none" stroke="currentColor"
											stroke-width="2" stroke-linecap="round"
											stroke-linejoin="round"
											class="feather feather-twitter mr-2 icon-inline text-info">
											<path
												d="M23 3a10.9 10.9 0 0 1-3.14 1.53 4.48 4.48 0 0 0-7.86 3v1A10.66 10.66 0 0 1 3 4s-4 9 5 13a11.64 11.64 0 0 1-7 2c9 5 20 0 20-11.5a4.5 4.5 0 0 0-.08-.83A7.72 7.72 0 0 0 23 3z"></path></svg>
										Twitter
									</h6> <span class="text-secondary">@bootdey</span>
								</li>
								<li
									class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
									<h6 class="mb-0">
										<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
											viewBox="0 0 24 24" fill="none" stroke="currentColor"
											stroke-width="2" stroke-linecap="round"
											stroke-linejoin="round"
											class="feather feather-instagram mr-2 icon-inline text-danger">
											<rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect>
											<path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
											<line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line></svg>
										Instagram
									</h6> <span class="text-secondary">bootdey</span>
								</li>
								<li
									class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
									<h6 class="mb-0">
										<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
											viewBox="0 0 24 24" fill="none" stroke="currentColor"
											stroke-width="2" stroke-linecap="round"
											stroke-linejoin="round"
											class="feather feather-facebook mr-2 icon-inline text-primary">
											<path
												d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path></svg>
										Facebook
									</h6> <span class="text-secondary">bootdey</span>
								</li>
							</ul>
						</div>
					</div>

					<div class="col-md-8">
						<div class="card mb-3">
							<div class="card-body">
								<div class="row">
									<div class="col-sm-3">
										<h6 class="mb-0">이름</h6>
									</div>
									<div class="col-sm-9 text-secondary"><%=name%></div>
								</div>
								<hr>
								<div class="row">
									<div class="col-sm-3">
										<h6 class="mb-0">이메일</h6>
									</div>
									<div class="col-sm-9 text-secondary"><%=email%></div>
								</div>
								<hr>
								<div class="row">
									<div class="col-sm-3">
										<h6 class="mb-0">내선 전화</h6>
									</div>
									<div class="col-sm-9 text-secondary"><%=extPhone%></div>
								</div>
								<hr>
								<div class="row">
									<div class="col-sm-3">
										<h6 class="mb-0">개인 전화</h6>
									</div>
									<div class="col-sm-9 text-secondary"><%=mobilePhone%></div>
								</div>
								<hr>
								<div class="row">
									<div class="col-sm-3">
										<h6 class="mb-0">주소</h6>
									</div>
									<div class="col-sm-9 text-secondary"><%=address%></div>
								</div>
								<hr>
								<div class="row">
									<div class="col-sm-12">
										<%
										if (currentUser.getId().equals(user.getId())) {
										%>
											<a class="btn btn-info" href="passwordCheck.jsp">Edit</a>
										<%
										}
										%>
									</div>
								</div>
							</div>
						</div>

						<div class="col-sm-12">
							<div class="card">
								<div class="card-body">
									<form>
										<div class="header">
											<div class="date">
												<p class="work-title" style="font-size: 24px">이번 달 근무 현황</p>
												<input type="month" id="monthPicker" min="<%=minDate%>" value="<%=current%>">
											</div>
										</div>
									</form>

									<table class="table table-bordered">
										<tr>
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
											<th colspan="5">통계</th>

										</tr>
										<tr>
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
										<tr>
											<%
											final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
											final HashMap<LocalDate, Integer> map = new HashMap<>();
											
											int normalCount = 0;
											int lateCount = 0;
											int leaveCount = 0;
											double vacationCount = 0f;
											
											Vector<ViewWorkBean> works = currentUser.getId().equals(id) ?
													activityCon.getWorkViews(id, current.toString()) : new Vector<ViewWorkBean>();
											
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
													String colorStyle = "";

													if (work.getStartedAt() == null && work.getEndedAt() == null) {
														status = 0;
													}
													switch (status) {
														case 0:
															// 결근인데, 주말에는 체크를 하지 않아야 함.
															if (!isSunday && !isSaturday) {
																colorStyle = " background-color: #E51A2E;";
															}
															break;
														case 1:
															// 근무 중인 상태
														case 2:
															// 정상 퇴근
															colorStyle = " background-color: greenyellow;";
															++normalCount;
															break;
														case 3:
															// 조퇴
															colorStyle = " background-color: skyblue;";
															++leaveCount;
															break;
														case 4:
															// 지각
															if (!(isSunday || isSaturday)) {
																colorStyle = " background-color: yellow;";
																++lateCount;
															}
															break;
														case 5:
															// 반차
															if (!(isSunday || isSaturday)) {
																colorStyle = " background-color: #dbdbdb;";
																vacationCount += 0.5f;
															}
															break;
														case 6:
															// 연차
															vacationCount += 1.0f;
															colorStyle = " background-color: #dbdbdb;";
															break;
													}
													
											%>
												<td style="height: 50px;<%=colorStyle%>"></td>
											<%
												} else {
											%>
												<td style="height: 50px;"></td>
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
														
														if (work.getStartedAt() == null && work.getEndedAt() == null) {
															status = 0;
														}
														switch (status) {
															case 0:
																// 결근인데, 주말에는 체크를 하지 않아야 함.
																if (!isSunday && !isSaturday) {
																	colorStyle = " background-color: #E51A2E;";
																}
																break;
															case 1:
																// 근무 중인 상태
															case 2:
																// 정상 퇴근
																colorStyle = " background-color: greenyellow;";
																++normalCount;
																break;
															case 3:
																// 조퇴
																colorStyle = " background-color: skyblue;";
																++leaveCount;
																break;
															case 4:
																// 지각
																if (!(isSunday || isSaturday)) {
																	colorStyle = " background-color: yellow;";
																	++lateCount;
																}
																break;
															case 5:
																// 반차
																if (!(isSunday || isSaturday)) {
																	colorStyle = " background-color: #dbdbdb;";
																	vacationCount += 0.5f;
																}
																break;
															case 6:
																// 연차
																vacationCount += 1.0f;
																colorStyle = " background-color: #dbdbdb;";
																break;
													}
											%>
													<td style="height: 50px;<%=colorStyle%>"></td>
											<%
													} else {
											%>
														<td style="height: 50px;"></td>
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
									</table>
									<div class="work-title-color">
										<div class="box-wrapper">
											<div class="box1" id="box"></div>
											<p>정상 근무</p>
										</div>
										<div class="box-wrapper">
											<div class="box2" id="box"></div>
											<p>지각</p>
										</div>
										<div class="box-wrapper">
											<div class="box3" id="box"></div>
											<p>조퇴</p>
										</div>
										<div class="box-wrapper">
											<div class="box4" id="box"></div>
											<p>결근</p>
										</div>
										<div class="box-wrapper">
											<div class="box5" id="box"></div>
											<p>연차</p>
										</div>
									</div>
									<form name="readFrm">
										<input type="hidden" name="id" value="<%=id%>">
										<input type="hidden" name="date" value="<%=current%>">
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>
</body>
</html>
