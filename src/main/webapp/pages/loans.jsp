<%@page import="java.util.List" %>
<%@page import="model.LoanModel" %>
<%@page import="model.UserModel" %>
<%@page import="model.BookModel" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="shared/head.jsp" />
</head>
<body>
    <jsp:include page="shared/sidebar.jsp" />

    <main class="main-content">
        <div class="header-container">
            <h1 class="header-title">Préstamos</h1>
            <div class="search-bar">
                <button class="btn btn-primary js-open-modal" data-target="modalCreateLoan">
                    <i data-lucide="plus"></i> Nuevo Préstamo
                </button>
            </div>
        </div>

        <div class="tabs">
            <% String currentTab = (String) request.getAttribute("activeTab"); %>
            <div class="tab <%= "actives".equals(currentTab) ? "active" : "" %>" data-target="tab-actives">Activos</div>
            <div class="tab <%= "history".equals(currentTab) ? "active" : "" %>" data-target="tab-history">Historial</div>
        </div>

        <!-- Tab Activos -->
        <div id="tab-actives" class="tab-content <%= "actives".equals(currentTab) ? "active" : "" %>">
            <form action="${pageContext.request.contextPath}/loans" method="GET" style="display:flex; gap:10px; margin-bottom: 20px;">
                <input type="hidden" name="tab" value="actives">
                <div class="autocomplete-container" style="flex:1;">
                    <input type="hidden" name="idUserSearch" id="filterLoanUserId" value="${idUserSearch}">
                    <input type="text" class="form-control autocomplete-input"
                        data-endpoint="/users?action=apiSearch"
                        data-target="filterLoanUserId" placeholder="Buscar por usuario..." autocomplete="off">
                    <div class="autocomplete-suggestions"></div>
                </div>
                <input type="date" name="dateFilter" class="form-control" value="${dateFilter}" style="flex:1;">
                <button type="submit" class="btn btn-primary"><i data-lucide="filter"></i> Filtrar</button>
                <a href="${pageContext.request.contextPath}/loans?tab=actives" class="btn btn-danger"><i data-lucide="x"></i> Limpiar</a>
            </form>

            <div class="data-table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID Préstamo</th>
                            <th>Fecha Préstamo</th>
                            <th>Usuario</th>
                            <th>Libro</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<LoanModel> activeLoans = (List<LoanModel>) request.getAttribute("activeLoans");
                            if (activeLoans != null && !activeLoans.isEmpty()) {
                                for (LoanModel l : activeLoans) {
                        %>
                        <tr>
                            <td><%= l.getIdLoan() %></td>
                            <td><%= l.getLoanDate() %></td>
                            <td><%= l.getUserName() != null ? l.getUserName() : "ID: " + l.getIdUser() %></td>
                            <td><%= l.getBookTitle() != null ? l.getBookTitle() : "ID: " + l.getIdBook() %></td>
                            <td><span class="badge badge-warning">Pendiente</span></td>
                            <td>
                                <div class="actions-col">
                                    <button class="btn-icon success js-return-loan" data-id="<%= l.getIdLoan() %>" title="Devolver Libro">
                                        <i data-lucide="check-circle"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                        <% } } else { %>
                        <tr>
                            <td colspan="6" style="text-align: center;">No hay préstamos activos</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <!-- Paginación Activos -->
            <div class="pagination">
                <%
                    Integer activeCurrent = (Integer) request.getAttribute("activeCurrentPage");
                    Integer activeTotal = (Integer) request.getAttribute("activeTotalPages");
                    Integer historyCurrentForLink = (Integer) request.getAttribute("historyCurrentPage");
                    if (activeCurrent != null && activeTotal != null && activeTotal > 1) {
                        int maxVis = 5;
                        int startA = Math.max(1, activeCurrent - maxVis / 2);
                        int endA = Math.min(activeTotal, startA + maxVis - 1);
                        if (endA - startA < maxVis - 1) {
                            startA = Math.max(1, endA - maxVis + 1);
                        }
                        if (startA > 1) {
                %>
                <a href="${pageContext.request.contextPath}/loans?tab=actives&pageActive=1&pageHistory=<%=historyCurrentForLink%>" class="page-link">1</a>
                <% if (startA > 2) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                <% } %>
                <%
                        for (int i = startA; i <= endA; i++) {
                %>
                <a href="${pageContext.request.contextPath}/loans?tab=actives&pageActive=<%=i%>&pageHistory=<%=historyCurrentForLink%>" class="page-link <%= (i == activeCurrent) ? "active" : "" %>"><%=i%></a>
                <%
                        }
                        if (endA < activeTotal) {
                %>
                <% if (endA < activeTotal - 1) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                <a href="${pageContext.request.contextPath}/loans?tab=actives&pageActive=<%=activeTotal%>&pageHistory=<%=historyCurrentForLink%>" class="page-link"><%=activeTotal%></a>
                <%
                        }
                    }
                %>
            </div>
        </div>

        <!-- Tab Historial -->
        <div id="tab-history" class="tab-content <%= "history".equals(currentTab) ? "active" : "" %>">
            <form action="${pageContext.request.contextPath}/loans" method="GET" style="display:flex; gap:10px; margin-bottom: 20px;">
                <input type="hidden" name="tab" value="history">
                <div class="autocomplete-container" style="flex:1;">
                    <input type="hidden" name="idUserSearchHist" id="filterHistUserId" value="${idUserSearchHist}">
                    <input type="text" class="form-control autocomplete-input"
                        data-endpoint="/users?action=apiSearch"
                        data-target="filterHistUserId" placeholder="Buscar por usuario..." autocomplete="off">
                    <div class="autocomplete-suggestions"></div>
                </div>
                <input type="date" name="dateFilterHist" class="form-control" value="${dateFilterHist}" style="flex:1;">
                <button type="submit" class="btn btn-primary"><i data-lucide="filter"></i> Filtrar</button>
                <a href="${pageContext.request.contextPath}/loans?tab=history" class="btn btn-danger"><i data-lucide="x"></i> Limpiar</a>
            </form>

            <div class="data-table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID Préstamo</th>
                            <th>Fecha Préstamo</th>
                            <th>Fecha Devolución</th>
                            <th>Usuario</th>
                            <th>Libro</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<LoanModel> allLoans = (List<LoanModel>) request.getAttribute("allLoans");
                            if (allLoans != null && !allLoans.isEmpty()) {
                                for (LoanModel l : allLoans) {
                        %>
                        <tr>
                            <td><%= l.getIdLoan() %></td>
                            <td><%= l.getLoanDate() %></td>
                            <td><%= (l.getReturnDate() != null) ? l.getReturnDate() : "-" %></td>
                            <td><%= l.getUserName() != null ? l.getUserName() : "ID: " + l.getIdUser() %></td>
                            <td><%= l.getBookTitle() != null ? l.getBookTitle() : "ID: " + l.getIdBook() %></td>
                            <td>
                                <% if (l.isReturned()) { %>
                                    <span class="badge badge-success">Devuelto</span>
                                <% } else { %>
                                    <span class="badge badge-warning">Pendiente</span>
                                <% } %>
                            </td>
                        </tr>
                        <% } } else { %>
                        <tr>
                            <td colspan="6" style="text-align: center;">No hay historial de préstamos</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <!-- Paginación Historial -->
            <div class="pagination" style="margin-bottom: 30px;">
                <%
                    Integer historyCurrent = (Integer) request.getAttribute("historyCurrentPage");
                    Integer historyTotal = (Integer) request.getAttribute("historyTotalPages");
                    Integer activeCurrentForLink = (Integer) request.getAttribute("activeCurrentPage");
                    if (historyCurrent != null && historyTotal != null && historyTotal > 1) {
                        int maxVisH = 5;
                        int startH = Math.max(1, historyCurrent - maxVisH / 2);
                        int endH = Math.min(historyTotal, startH + maxVisH - 1);
                        if (endH - startH < maxVisH - 1) {
                            startH = Math.max(1, endH - maxVisH + 1);
                        }
                        if (startH > 1) {
                %>
                <a href="${pageContext.request.contextPath}/loans?tab=history&pageActive=<%=activeCurrentForLink%>&pageHistory=1" class="page-link">1</a>
                <% if (startH > 2) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                <% } %>
                <%
                        for (int i = startH; i <= endH; i++) {
                %>
                <a href="${pageContext.request.contextPath}/loans?tab=history&pageActive=<%=activeCurrentForLink%>&pageHistory=<%=i%>" class="page-link <%= (i == historyCurrent) ? "active" : "" %>"><%=i%></a>
                <%
                        }
                        if (endH < historyTotal) {
                %>
                <% if (endH < historyTotal - 1) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                <a href="${pageContext.request.contextPath}/loans?tab=history&pageActive=<%=activeCurrentForLink%>&pageHistory=<%=historyTotal%>" class="page-link"><%=historyTotal%></a>
                <%
                        }
                    }
                %>
            </div>
        </div>
    </main>

    <!-- Modals -->
    <!-- Create Loan -->
    <div class="modal-overlay" id="modalCreateLoan">
        <div class="modal">
            <div class="modal-header">
                <h2>Registrar Préstamo</h2>
                <button class="btn-icon close-modal"><i data-lucide="x"></i></button>
            </div>
            <form id="formCreateLoan" action="${pageContext.request.contextPath}/loans" method="POST">
                <input type="hidden" name="action" value="registerAjax">

                <div class="form-group autocomplete-container">
                    <label>Usuario</label>
                    <input type="hidden" name="idUser" id="createLoanUserId" required>
                    <input type="text" class="form-control autocomplete-input"
                        data-endpoint="/users?action=apiSearch"
                        data-target="createLoanUserId" placeholder="Buscar usuario..." required autocomplete="off">
                    <div class="autocomplete-suggestions"></div>
                </div>

                <div class="form-group autocomplete-container">
                    <label>Libro</label>
                    <input type="hidden" name="idBook" id="createLoanBookId" required>
                    <input type="text" class="form-control autocomplete-input"
                        data-endpoint="/books?action=apiSearch"
                        data-target="createLoanBookId" placeholder="Buscar libro..." required autocomplete="off">
                    <div class="autocomplete-suggestions"></div>
                </div>

                <div style="text-align: right; margin-top:20px;">
                    <button type="submit" class="btn btn-primary">Registrar Préstamo</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Return Loan -->
    <div class="modal-overlay" id="modalReturnLoan">
        <div class="modal">
            <div class="modal-header">
                <h2>Devolver Libro</h2>
                <button class="btn-icon close-modal"><i data-lucide="x"></i></button>
            </div>
            <form id="formReturnLoan" action="${pageContext.request.contextPath}/loans" method="POST">
                <input type="hidden" name="action" value="return">
                <input type="hidden" name="idLoan" id="returnLoanId">
                <p style="margin-bottom: 20px;">¿Confirmas que el usuario ha devuelto este libro?</p>
                <div style="text-align: right;">
                    <button type="submit" class="btn btn-primary">Confirmar Devolución</button>
                </div>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>
