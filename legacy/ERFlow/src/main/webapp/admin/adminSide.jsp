<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="admin-wrap">
    <div class="aside-bar">
        <div class="logo-font">

        </div>
        <hr>
        <aside class="sidebar">
            <!--  사이드 바  -->
            <nav class="sidebar-nav">
                <ul class="sidebar-ul">
                    <li class="sidebar-mainmenu">
                        <span class="menu-title">사원 관리</span>
                        <ul class="nav-flyout">
                            <li>
                                <a href="/ERFlow/admin/user/userList.jsp">사원 리스트</a>
                            </li>
                            <li>
                                <a href="/ERFlow/admin/user/userRegister.jsp">사원 추가</a>
                            </li>
                        </ul>
                    </li>
                    <li class="sidebar-mainmenu">
                        <span class="menu-title">권한 관리</span>
                        <ul class="nav-flyout">
                            <li>
                                <a href="/ERFlow/admin/permission/jobDeptList.jsp">직급 및 부서 리스트</a>
                            </li>
                            <li>
                                <a href="/ERFlow/admin/permission/programList.jsp">프로그램 리스트</a>
                            </li>
                        </ul>
                    </li>
                    <li class="sidebar-mainmenu">
                        <span class="menu-title">문서 관리</span>
                        <ul class="nav-flyout">
                            <li>
                                <a href="/ERFlow/admin/document/documentFormList.jsp">문서 양식 리스트</a>
                            </li>
                        </ul>
                    </li>
                    <li class="sidebar-mainmenu">
                        <a href="/ERFlow/admin/board/adminBoardList.jsp"><span class="menu-title">게시판 관리</span></a>
                    </li>
                </ul>
            </nav>
        </aside>
    </div>