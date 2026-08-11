<!-- .jsp -->
<%@page contentType="text/html; charset=UTF-8"%>
<!--  사이드 바  -->
      <section class="app">
        <aside class="sidebar">
          <nav class="sidebar-nav">
            <ul class="sidebar-ul">
              <li class="sidebar-mainmenu">
                <span class="menu-title">문서 관리</span>
                <ul class="nav-flyout">
                  <li>
                    <a href="/ERFlow/document/documentRegister.jsp">기안 작성</a>
                  </li>
                  <li>
                    <a href="/ERFlow/document/documentList.jsp">문서 리스트</a>
                  </li>
                </ul>
              </li>
              <li class="sidebar-mainmenu">
                <span class="menu-title">전자결재</span>
                <ul class="nav-flyout">
                  <li>
                    <a href="/ERFlow/proposal/proposalList.jsp">결재 리스트</a>
                    <a href="/ERFlow/proposal/proposalRouteList.jsp">결재라인 관리</a>
                  </li>
                </ul>
              </li>
              <li class="sidebar-mainmenu">
                <span class="menu-title">생산관리</span>
                <ul class="nav-flyout">
                  <li>
                    <a href="/ERFlow/unit/unitList.jsp">생산 설비 관리</a>
                  </li>
                  <li>
                    <a href="/ERFlow/process/processList.jsp">공정 관리</a>
                  </li>
                </ul>
              </li>
              <li class="sidebar-mainmenu">
                <span class="menu-title">구매</span>
                <ul class="nav-flyout">
                  <li>
                    <a href="/ERFlow/company/companyList.jsp?flag=1">협력업체 관리</a>
                  </li>
                  <li>
                    <a href="/ERFlow/product/ingredientProduct.jsp">원재료 관리</a>
                  </li>
                  <li>
                    <a href="/ERFlow/product/processedProduct.jsp">가공품 관리</a>
                  </li>
                  <li>
                    <a href="/ERFlow/task/purchaseTask.jsp">발주 관리</a>
                  </li>
                  <li>
                    <a href="/ERFlow/bound/inbound.jsp">입고 제품 관리</a>
                  </li>
                </ul>
              </li>
              <li class="sidebar-mainmenu">
                <span class="menu-title">영업</span>
                <ul class="nav-flyout">
                  <li>
                    <a href="/ERFlow/company/companyList.jsp?flag=0">협력업체 관리</a>
                  </li>
                  <li>
                    <a href="/ERFlow/task/sellTask.jsp">수주 관리</a>
                  </li>
                  <li>
                    <a href="/ERFlow/bound/outbound.jsp">출고 제품 관리</a>
                  </li>
                  <li>
                    <a href="/ERFlow/product/productedProduct.jsp">완제품 재고 관리</a>
                  </li>
                </ul>
              </li>
              <li class="sidebar-mainmenu">
                <span class="menu-title">근태 관리</span>
                <ul class="nav-flyout">
                  <li>
                    <a href="/ERFlow/work/work.jsp">근태 확인</a>
                  </li>
                </ul>
              </li>
              <li class="sidebar-mainmenu">
                <a href="/ERFlow/post/boardList.jsp"><span class="menu-title">게시판</span></a>
              </li>
            </ul>
          </nav> 
        </aside>
      </section>
      <!-- 사이드바 마무리 -->