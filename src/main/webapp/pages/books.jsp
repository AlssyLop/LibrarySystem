<%@page import="java.util.List"%>
<%@page import="model.bookModel"%>
<%@page import="model.authorModel"%>
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
            <h1 class="header-title">Libros</h1>
            <div class="search-bar">
                <form action="${pageContext.request.contextPath}/books" method="GET" style="display:flex; gap:10px;">
                    <input type="text" name="query" class="form-control" placeholder="Buscar por título o ISBN..." value="${query}" style="width: 250px;">
                    <button type="submit" class="btn btn-primary"><i data-lucide="search"></i> Buscar</button>
                </form>
                <button class="btn btn-primary" onclick="openModal('modalCreateBook')">
                    <i data-lucide="plus"></i> Nuevo
                </button>
            </div>
        </div>

        <div class="data-table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Título</th>
                        <th>ISBN</th>
                        <th>Año</th>
                        <th>ID Autor</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<bookModel> books = (List<bookModel>) request.getAttribute("books");
                        if (books != null && !books.isEmpty()) {
                            for (bookModel b : books) {
                    %>
                    <tr>
                        <td><%= b.getIdBook() %></td>
                        <td><%= b.getTitle() %></td>
                        <td><%= b.getIsbn() %></td>
                        <td><%= b.getYear() %></td>
                        <td><%= b.getIdAuthor() %></td>
                        <td>
                            <div class="actions-col">
                                <button class="btn-icon amber" onclick="openBookEditModal(<%= b.getIdBook() %>, '<%= b.getTitle().replace("'", "\\'") %>', '<%= b.getIsbn() %>', <%= b.getYear() %>, <%= b.getIdAuthor() %>)">
                                    <i data-lucide="edit-2"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="6" style="text-align: center;">No hay registros para mostrar</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            
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
                   <a href="${pageContext.request.contextPath}/books?page=1&query=<%=q%>" class="page-link">1</a>
                   <% if(startPage > 2) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                <% } %>
                
                <%
                       for (int i = startPage; i <= endPage; i++) {
                %>
                   <a href="${pageContext.request.contextPath}/books?page=<%=i%>&query=<%=q%>" class="page-link <%= (i == currentPage) ? "active" : "" %>"><%=i%></a>
                <% 
                       }
                       if (endPage < totalPages) {
                %>
                   <% if(endPage < totalPages - 1) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                   <a href="${pageContext.request.contextPath}/books?page=<%=totalPages%>&query=<%=q%>" class="page-link"><%=totalPages%></a>
                <%
                       }
                   } 
                %>
            </div>
        </div>
    </main>

    <!-- Modals -->
    <!-- Create Book -->
    <div class="modal-overlay" id="modalCreateBook">
        <div class="modal">
            <div class="modal-header">
                <h2>Registrar Libro</h2>
                <button class="btn-icon close-modal"><i data-lucide="x"></i></button>
            </div>
            <form id="formCreateBook" action="${pageContext.request.contextPath}/books" method="POST">
                <input type="hidden" name="action" value="registerAjax">
                <div class="form-group">
                    <label>Título</label>
                    <input type="text" name="title" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>ISBN</label>
                    <input type="text" name="isbn" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Año de Publicación</label>
                    <input type="number" name="year" class="form-control" required>
                </div>
                <div class="form-group autocomplete-container">
                    <label>Autor</label>
                    <input type="hidden" name="idAuthor" id="createBookAuthorId" required>
                    <input type="text" class="form-control autocomplete-input" data-endpoint="${pageContext.request.contextPath}/authors?action=apiSearch" data-target="createBookAuthorId" placeholder="Buscar autor..." required autocomplete="off">
                    <div class="autocomplete-suggestions"></div>
                </div>
                <div style="text-align: right; margin-top:20px;">
                    <button type="submit" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Edit Book -->
    <div class="modal-overlay" id="modalEditBook">
        <div class="modal">
            <div class="modal-header">
                <h2>Actualizar Libro</h2>
                <button class="btn-icon close-modal"><i data-lucide="x"></i></button>
            </div>
            <form id="formEditBook" action="${pageContext.request.contextPath}/books" method="POST">
                <input type="hidden" name="action" value="updateAjax">
                <input type="hidden" name="idBook" id="editBookId">
                <div class="form-group">
                    <label>Título</label>
                    <input type="text" name="title" id="editBookTitle" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>ISBN</label>
                    <input type="text" name="isbn" id="editBookIsbn" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Año de Publicación</label>
                    <input type="number" name="year" id="editBookYear" class="form-control" required>
                </div>
                <div class="form-group autocomplete-container">
                    <label>Autor</label>
                    <input type="hidden" name="idAuthor" id="editBookAuthorId" required>
                    <input type="text" class="form-control autocomplete-input" id="editBookAuthorText" data-endpoint="${pageContext.request.contextPath}/authors?action=apiSearch" data-target="editBookAuthorId" placeholder="Buscar autor..." required autocomplete="off">
                    <div class="autocomplete-suggestions"></div>
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
