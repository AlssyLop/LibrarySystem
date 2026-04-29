<%@page import="java.util.List"%>
<%@page import="model.UserModel"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="shared/head.jsp"/>
</head>
<body>
    <jsp:include page="shared/sidebar.jsp"/>

    <main class="main-content">
        <div class="header-container">
            <h1 class="header-title">Usuarios</h1>
            <div class="search-bar">
                <form action="${pageContext.request.contextPath}/users" method="GET" style="display:flex; gap:10px;">
                    <input type="text" name="query" class="form-control" placeholder="Buscar usuario..." value="${query}">
                    <button type="submit" class="btn btn-primary"><i data-lucide="search"></i> Buscar</button>
                </form>
                <button class="btn btn-primary js-open-modal" data-target="modalCreateUser">
                    <i data-lucide="plus"></i> Nuevo
                </button>
            </div>
        </div>

        <div class="data-table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Email</th>
                        <th>Teléfono</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<UserModel> users = (List<UserModel>) request.getAttribute("users");
                        if (users != null && !users.isEmpty()) {
                            for (UserModel u : users) {
                    %>
                    <tr>
                        <td><%= u.getIdUser() %></td>
                        <td><%= u.getName() %></td>
                        <td><%= u.getEmail() %></td>
                        <td><%= u.getPhone() %></td>
                        <td>
                            <div class="actions-col">
                                <button class="btn-icon amber js-edit-user" data-id="<%= u.getIdUser() %>" data-name="<%= u.getName() %>" data-email="<%= u.getEmail() %>" data-phone="<%= u.getPhone() %>">
                                    <i data-lucide="edit-2"></i>
                                </button>
                                <a href="${pageContext.request.contextPath}/users?action=delete&id=<%= u.getIdUser() %>" class="btn-icon danger btn-delete">
                                    <i data-lucide="trash-2"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="5" style="text-align: center;">No hay registros para mostrar</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
            
            <!-- Paginación -->
            <div class="pagination">
                <% 
                   Integer currentPage = (Integer) request.getAttribute("currentPage");
                   Integer totalPages = (Integer) request.getAttribute("totalPages");
                   String q = (String) request.getAttribute("query");
                   if (currentPage != null && totalPages != null && totalPages > 1) {
                       int maxVisiblePages = 5;
                       int startPage = Math.max(1, currentPage - maxVisiblePages / 2);
                       int endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
                       if (endPage - startPage < maxVisiblePages - 1) {
                           startPage = Math.max(1, endPage - maxVisiblePages + 1);
                       }
                       
                       if (startPage > 1) {
                %>
                   <a href="${pageContext.request.contextPath}/users?page=1&query=<%=q%>" class="page-link">1</a>
                   <% if(startPage > 2) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                <% } %>
                
                <%
                       for (int i = startPage; i <= endPage; i++) {
                %>
                   <a href="${pageContext.request.contextPath}/users?page=<%=i%>&query=<%=q%>" class="page-link <%= (i == currentPage) ? "active" : "" %>"><%=i%></a>
                <% 
                       }
                       
                       if (endPage < totalPages) {
                %>
                   <% if(endPage < totalPages - 1) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                   <a href="${pageContext.request.contextPath}/users?page=<%=totalPages%>&query=<%=q%>" class="page-link"><%=totalPages%></a>
                <%
                       }
                   } 
                %>
            </div>
    </main>

    <!-- Modals -->
    <!-- Create User -->
    <div class="modal-overlay" id="modalCreateUser">
        <div class="modal">
            <div class="modal-header">
                <h2>Registrar Usuario</h2>
                <button class="btn-icon close-modal"><i data-lucide="x"></i></button>
            </div>
            <form  id="formCreateUser" action="${pageContext.request.contextPath}/users" method="POST">
                <input type="hidden" name="action" value="register">
                <div class="form-group">
                    <label>Nombres</label>
                    <input type="text" name="name" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" class="form-control" pattern="^[a-zA-Z0-9._]{1,64}@(?=.{3,255}$)[a-zA-Z0-9_]+(?:\.[a-zA-Z0-9_]+)+$" title="Ingrese un correo válido" required>
                </div>
                <div class="form-group">
                    <label>Teléfono</label>
                    <input type="text" name="phone" class="form-control" pattern="^[0-9\-+]+$" title="Solo se permiten números, guiones y signo de +" required>
                </div>
                <div style="text-align: right; margin-top:20px;">
                    <button type="submit" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Edit User -->
    <div class="modal-overlay" id="modalEditUser">
        <div class="modal">
            <div class="modal-header">
                <h2>Actualizar Usuario</h2>
                <button class="btn-icon close-modal"><i data-lucide="x"></i></button>
            </div>
            <form id="formEditUser" action="${pageContext.request.contextPath}/users" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="idUser" id="editUserId">
                <div class="form-group">
                    <label>Nombres</label>
                    <input type="text" name="name" id="editUserName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" id="editUserEmail" class="form-control" pattern="^[a-zA-Z0-9._]{1,64}@(?=.{3,255}$)[a-zA-Z0-9_]+(?:\.[a-zA-Z0-9_]+)+$" title="Ingrese un correo válido" required>
                </div>
                <div class="form-group">
                    <label>Teléfono</label>
                    <input type="text" name="phone" id="editUserPhone" class="form-control" pattern="^[0-9\-+]+$" title="Solo se permiten números, guiones y signo de +" required>
                </div>
                <div style="text-align: right; margin-top:20px;">
                    <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                </div>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>
