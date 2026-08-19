# 화면 대조표

두 앱을 띄워 놓고 **어느 주소와 어느 주소를 견주는가**. `build_screen_map.py` 가 화면 목록에서 뽑는다 — 손으로 고치지 않는다.

| | |
|---|---|
| 레거시 | `http://localhost:19090/ERFlow` |
| 신규 | `http://localhost:18080` |

로그인은 양쪽 다 따로 한다. 세션이 다르므로 한쪽에서 로그인해도 다른 쪽은 모른다.

> **먼저 여는 쪽이 뒤에 여는 쪽을 오염시킨다.** 두 앱이 같은 스키마를 본다. 게시글 보기처럼 여는 것만으로 조회수가 오르는 화면은 순서가 결과를 바꾼다(D-033).

## 로그인

| 화면 | 레거시 | 신규 |
|---|---|---|
| login/change-password | `/ERFlow/login/changePassword.jsp` | `/login/change-password` |
| login/find-password | `/ERFlow/login/findPassword.jsp` | `/login/find-password` |
| login/login | `/ERFlow/login/login.jsp` | `/login` |
| login/password-error | `/ERFlow/login/passwordError.html` | `/login/password-error` |
| login/password-ok | `/ERFlow/login/passwordOk.html` | `/login/password-ok` |
| login/send-ok | `/ERFlow/login/sendOk.html` | — 이관 전(O-011) |

## 메인

| 화면 | 레거시 | 신규 |
|---|---|---|
| fragments/footer | `/ERFlow/footer.jsp` | — 메인 화면 안에 그려진다 |
| index | `/ERFlow/index.jsp` | `/index` |

## 프로필

| 화면 | 레거시 | 신규 |
|---|---|---|
| profile/password-check | `/ERFlow/passwordCheck.jsp` | `/profile/password-check` |
| profile/update | `/ERFlow/profileUpdate.jsp` | `/profile/update` |
| profile/view | `/ERFlow/profile.jsp` | `/profile` |

## 근태

| 화면 | 레거시 | 신규 |
|---|---|---|
| work/work | `/ERFlow/work/work.jsp` | `/work` |

## 게시판

| 화면 | 레거시 | 신규 |
|---|---|---|
| post/board-list | `/ERFlow/post/boardList.jsp` | `/post/board-list` |
| post/list | `/ERFlow/post/postList.jsp` | `/post/list` |
| post/register | `/ERFlow/post/postRegister.jsp` | `/post/register` |
| post/reply | `/ERFlow/post/postReply.jsp` | `/post/reply` |
| post/update | `/ERFlow/post/postUpdate.jsp` | `/post/update` |
| post/view | `/ERFlow/post/postView.jsp` | `/post/view` |

## 쪽지

| 화면 | 레거시 | 신규 |
|---|---|---|
| message/index | `/ERFlow/message/index.jsp` | `/message` |
| message/read | `/ERFlow/message/read.jsp` | `/message/read` |
| message/register | `/ERFlow/message/register.jsp` | `/message/register` |
| message/reply | `/ERFlow/message/reply.jsp` | `/message/reply` |

## 전자결재

| 화면 | 레거시 | 신규 |
|---|---|---|
| proposal/document | `/ERFlow/proposal/proposalDocument.jsp` | `/proposal/document` |
| proposal/list | `/ERFlow/proposal/proposalList.jsp` | `/proposal/list` |
| proposal/register | `/ERFlow/proposal/proposalRegister.jsp` | `/proposal/register` |
| proposal/route-list | `/ERFlow/proposal/proposalRouteList.jsp` | `/proposal/route-list` |
| proposal/route-register | `/ERFlow/proposal/proposalRouteRegister.jsp` | `/proposal/route-register` |
| proposal/route-update | `/ERFlow/proposal/proposalRouteUpdate.jsp` | `/proposal/route-update` |

## 문서

| 화면 | 레거시 | 신규 |
|---|---|---|
| document/list | `/ERFlow/document/documentList.jsp` | `/document/list` |
| document/register | `/ERFlow/document/documentRegister.jsp` | `/document/register` |

## 생산 설비

| 화면 | 레거시 | 신규 |
|---|---|---|
| unit/list | `/ERFlow/unit/unitList.jsp` | `/unit/list` |
| unit/register | `/ERFlow/unit/unitRegister.jsp` | `/unit/register` |
| unit/update | `/ERFlow/unit/unitUpdate.jsp` | `/unit/update` |

## 협력업체

| 화면 | 레거시 | 신규 |
|---|---|---|
| company/list (`flag=1`) | `/ERFlow/company/companyList.jsp?flag=1` | `/company/list?flag=1` |
| company/list (`flag=0`) | `/ERFlow/company/companyList.jsp?flag=0` | `/company/list?flag=0` |
| company/register (`flag=1`) | `/ERFlow/company/companyRegister.jsp?flag=1` | `/company/register?flag=1` |
| company/register (`flag=0`) | `/ERFlow/company/companyRegister.jsp?flag=0` | `/company/register?flag=0` |
| company/update (`flag=1`) | `/ERFlow/company/companyUpdate.jsp?flag=1` | `/company/update?flag=1` |
| company/update (`flag=0`) | `/ERFlow/company/companyUpdate.jsp?flag=0` | `/company/update?flag=0` |

## 제품

| 화면 | 레거시 | 신규 |
|---|---|---|
| product/ingredient-product | `/ERFlow/product/ingredientProduct.jsp` | `/product/ingredient-product` |
| product/processed-product | `/ERFlow/product/processedProduct.jsp` | `/product/processed-product` |
| product/producted-product | `/ERFlow/product/productedProduct.jsp` | `/product/producted-product` |
| product/register | `/ERFlow/product/productRegister.jsp` | `/product/register` |
| product/update | `/ERFlow/product/productUpdate.jsp` | `/product/update` |

## 공정

| 화면 | 레거시 | 신규 |
|---|---|---|
| process/list | `/ERFlow/process/processList.jsp` | `/process/list` |
| process/register | `/ERFlow/process/processRegister.jsp` | `/process/register` |
| process/update | `/ERFlow/process/processUpdate.jsp` | `/process/update` |

## 수·발주

| 화면 | 레거시 | 신규 |
|---|---|---|
| task/history-modal | `/ERFlow/task/createModal.jsp` | — 수·발주 목록에서 «이력» 을 누르면 뜬다 |
| task/purchase-task | `/ERFlow/task/purchaseTask.jsp` | `/task/purchase-task` |
| task/register (`flag=sell`) | `/ERFlow/task/taskRegister.jsp?flag=sell` | `/task/register?flag=sell` |
| task/register (`flag=purchase`) | `/ERFlow/task/taskRegister.jsp?flag=purchase` | `/task/register?flag=purchase` |
| task/sell-task | `/ERFlow/task/sellTask.jsp` | `/task/sell-task` |
| task/update (`flag=sell`) | `/ERFlow/task/taskUpdate.jsp?flag=sell` | `/task/update?flag=sell` |
| task/update (`flag=purchase`) | `/ERFlow/task/taskUpdate.jsp?flag=purchase` | `/task/update?flag=purchase` |

## 입·출고

| 화면 | 레거시 | 신규 |
|---|---|---|
| bound/inbound | `/ERFlow/bound/inbound.jsp` | `/bound/inbound` |
| bound/outbound | `/ERFlow/bound/outbound.jsp` | `/bound/outbound` |
| bound/register (`flag=inbound`) | `/ERFlow/bound/boundRegister.jsp?flag=inbound` | `/bound/register?flag=inbound` |
| bound/register (`flag=outbound`) | `/ERFlow/bound/boundRegister.jsp?flag=outbound` | `/bound/register?flag=outbound` |
| bound/update (`flag=inbound`) | `/ERFlow/bound/boundUpdate.jsp?flag=inbound` | `/bound/update?flag=inbound` |
| bound/update (`flag=outbound`) | `/ERFlow/bound/boundUpdate.jsp?flag=outbound` | `/bound/update?flag=outbound` |

## 찾기 팝업

| 화면 | 레거시 | 신규 |
|---|---|---|
| find/bank | `/ERFlow/findBank.jsp` | `/find/bank` |
| find/company | `/ERFlow/findCompany.jsp` | `/find/company` |
| find/document | `/ERFlow/findDocument.jsp` | `/find/document` |
| find/each-user | `/ERFlow/findEachUser.jsp` | `/find/each-user` |
| find/multi-product | `/ERFlow/findMultiProduct.jsp` | `/find/multi-product` |
| find/product | `/ERFlow/findProduct.jsp` | `/find/product` |
| find/proposal-route | `/ERFlow/findProposalRoute.jsp` | `/find/proposal-route` |
| find/user | `/ERFlow/findUser.jsp` | `/find/user` |
| find/work | `/ERFlow/findWork.jsp` | `/find/work` |

## 관리자

| 화면 | 레거시 | 신규 |
|---|---|---|
| admin/board/dept-update | `/ERFlow/admin/board/boardDeptUpdate.jsp` | `/admin/board/dept-update` |
| admin/board/job-update | `/ERFlow/admin/board/boardJobUpdate.jsp` | `/admin/board/job-update` |
| admin/board/list | `/ERFlow/admin/board/adminBoardList.jsp` | `/admin/board/list` |
| admin/board/register | `/ERFlow/admin/board/boardRegister.jsp` | `/admin/board/register` |
| admin/board/update | `/ERFlow/admin/board/boardUpdate.jsp` | `/admin/board/update` |
| admin/document/form-list | `/ERFlow/admin/document/documentFormList.jsp` | `/admin/document/form-list` |
| admin/document/form-register | `/ERFlow/admin/document/documentFormRegister.jsp` | `/admin/document/form-register` |
| admin/home | `/ERFlow/admin/admin.jsp` | `/admin` |
| admin/permission/dept-register | `/ERFlow/admin/permission/deptRegister.jsp` | `/admin/permission/dept-register` |
| admin/permission/dept-update | `/ERFlow/admin/permission/deptUpdate.jsp` | `/admin/permission/dept-update` |
| admin/permission/job-dept-list | `/ERFlow/admin/permission/jobDeptList.jsp` | `/admin/permission/job-dept-list` |
| admin/permission/job-register | `/ERFlow/admin/permission/jobRegister.jsp` | `/admin/permission/job-register` |
| admin/permission/job-update | `/ERFlow/admin/permission/jobUpdate.jsp` | `/admin/permission/job-update` |
| admin/permission/program-dept-update | `/ERFlow/admin/permission/programDeptUpdate.jsp` | `/admin/permission/program-dept-update` |
| admin/permission/program-job-update | `/ERFlow/admin/permission/programJobUpdate.jsp` | `/admin/permission/program-job-update` |
| admin/permission/program-list | `/ERFlow/admin/permission/programList.jsp` | `/admin/permission/program-list` |
| admin/user/address | `/ERFlow/admin/user/userAddress.jsp` | `/admin/user/address` |
| admin/user/list | `/ERFlow/admin/user/userList.jsp` | `/admin/user/list` |
| admin/user/register | `/ERFlow/admin/user/userRegister.jsp` | `/admin/user/register` |
| admin/user/update | `/ERFlow/admin/user/userUpdate.jsp` | `/admin/user/update` |
| fragments/admin-footer | `/ERFlow/admin/adminFooter.jsp` | — 관리자 화면 안에 그려진다 |
| fragments/admin-header | `/ERFlow/admin/adminHeader.jsp` | — 관리자 화면 안에 그려진다 |
| fragments/admin-side | `/ERFlow/admin/adminSide.jsp` | — 관리자 화면 안에 그려진다 |

## 안내 화면

| 화면 | 레거시 | 신규 |
|---|---|---|
| error/access-error | `/ERFlow/accessError.jsp` | `/access-error` |
| error/internal-server-error | `/ERFlow/internalServerError.jsp` | `/internal-server-error` |
| error/not-found-error | `/ERFlow/notFoundError.jsp` | `/not-found-error` |
| error/permission-error | `/ERFlow/permissionError.jsp` | `/permission-error` |

## 화면이 아닌 대조

| 대상 | 레거시 | 신규 |
|---|---|---|
| 달력 일정 조회 | `/ERFlow/calendar/view` | `/calendar/view` |
| 근무 현황 그래프 | `/ERFlow/admin/graph/view` | `/admin/graph/view` |

메인 화면의 달력과 관리자 대시보드의 그래프는 화면이 아니라 JSON 이다. 정합성 게이트가 아예 닿지 않으므로 눈으로 견주거나 시험으로 막는다.

## 견줄 때 알아둘 것

**파라미터가 있어야 열리는 화면이 있다.** 보기·수정 화면은 대개 `?id=` 를 요구하고, 없으면 레거시는 그 자리에서 죽는다(500). 목록 화면에서 눌러 들어가는 편이 확실하다.

**로그인하지 않고 열면 세 화면은 404 가 뜬다.** `passwordCheck`, `findProposalRoute`, 그리고 `profile` 의 날짜 오류 경로다. 레거시가 안내 화면을 한 칸 위에서 찾는다 — 신규는 제대로 보내므로 여기서만 다르다(D-087).

**한 화면이 파라미터로 갈리는 곳이 있다.** 위 표에 갈래마다 한 줄씩 적어 두었다. 파라미터를 빼고 열면 **어느 갈래에도 걸리지 않아 막힌다** — 없는 화면이 아니라 갈래를 못 고른 것이다(D-016).
