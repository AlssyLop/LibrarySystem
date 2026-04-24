<%@page import="java.util.List"%>
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
            <h1 class="header-title">Autores</h1>
            <div class="search-bar">
                <form action="/authors" method="GET" style="display:flex; gap:10px;">
                    <input type="text" name="query" class="form-control" placeholder="Buscar autor..." value="${query}">
                    <button type="submit" class="btn btn-primary"><i data-lucide="search"></i> Buscar</button>
                </form>
                <button class="btn btn-primary" onclick="openModal('modalCreateAuthor')">
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
                        <th>Nacionalidad</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<authorModel> authors = (List<authorModel>) request.getAttribute("authors");
                        if (authors != null && !authors.isEmpty()) {
                            for (authorModel a : authors) {
                    %>
                    <tr>
                        <td><%= a.getIdAuthor() %></td>
                        <td><%= a.getName() %></td>
                        <td><%= a.getNationality() %></td>
                        <td>
                            <div class="actions-col">
                                <button class="btn-icon amber" onclick="openAuthorEditModal(<%= a.getIdAuthor() %>, '<%= a.getName() %>', '<%= a.getNationality() %>')">
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
                        <td colspan="4" style="text-align: center;">No hay registros para mostrar</td>
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
                   <a href="/authors?page=1&query=<%=q%>" class="page-link">1</a>
                   <% if(startPage > 2) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                <% } %>
                
                <%
                       for (int i = startPage; i <= endPage; i++) {
                %>
                   <a href="/authors?page=<%=i%>&query=<%=q%>" class="page-link <%= (i == currentPage) ? "active" : "" %>"><%=i%></a>
                <% 
                       }
                       if (endPage < totalPages) {
                %>
                   <% if(endPage < totalPages - 1) { %><span class="page-link" style="border:none;background:transparent;">...</span><% } %>
                   <a href="/authors?page=<%=totalPages%>&query=<%=q%>" class="page-link"><%=totalPages%></a>
                <%
                       }
                   } 
                %>
            </div>
    </main>

    <!-- Modals -->
    <!-- Create Author -->
    <div class="modal-overlay" id="modalCreateAuthor">
        <div class="modal">
            <div class="modal-header">
                <h2>Registrar Autor</h2>
                <button class="btn-icon close-modal"><i data-lucide="x"></i></button>
            </div>
            <form action="/authors" method="POST">
                <input type="hidden" name="action" value="register">
                <div class="form-group">
                    <label>Nombre Completo</label>
                    <input type="text" name="name" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Nacionalidad</label>
                    <div class="combo-box-container">
                        <input type="text" name="nationality" class="form-control" autocomplete="off" required>
                        <ul class="dropdown-list"></ul>
                    </div>
                </div>
                <div style="text-align: right; margin-top:20px;">
                    <button type="submit" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Edit Author -->
    <div class="modal-overlay" id="modalEditAuthor">
        <div class="modal">
            <div class="modal-header">
                <h2>Actualizar Autor</h2>
                <button class="btn-icon close-modal"><i data-lucide="x"></i></button>
            </div>
            <form action="/authors" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="idAuthor" id="editAuthorId">
                <div class="form-group">
                    <label>Nombre Completo</label>
                    <input type="text" name="name" id="editAuthorName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Nacionalidad</label>
                    <div class="combo-box-container">
                        <input type="text" name="nationality" id="editAuthorNationality" class="form-control" autocomplete="off" required>
                        <ul class="dropdown-list"></ul>
                    </div>
                </div>
                <div style="text-align: right; margin-top:20px;">
                    <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        const nationalitiesData = [
            <%
                java.util.List<String> nationalities = (java.util.List<String>) request.getAttribute("nationalities");
                if (nationalities != null) {
                    for (int i = 0; i < nationalities.size(); i++) {
                        out.print("\"" + nationalities.get(i).replace("\"", "\\\"") + "\"");
                        if (i < nationalities.size() - 1) out.print(",");
                    }
                }
            %>
        ];

        document.addEventListener('DOMContentLoaded', function() {
            function setupCombobox(container) {
                const input = container.querySelector('input[name="nationality"]');
                const list = container.querySelector('.dropdown-list');
                
                function renderList(query = '') {
                    list.innerHTML = '';
                    const lowerQuery = query.toLowerCase();
                    const filtered = nationalitiesData.filter(n => n.toLowerCase().includes(lowerQuery));
                    
                    filtered.forEach(n => {
                        const li = document.createElement('li');
                        li.textContent = n;
                        li.addEventListener('click', function() {
                            input.value = n;
                            list.classList.remove('active');
                        });
                        list.appendChild(li);
                    });
                }
                
                input.addEventListener('focus', function() {
                    renderList(input.value);
                    list.classList.add('active');
                });
                
                input.addEventListener('input', function() {
                    renderList(input.value);
                    list.classList.add('active');
                });
                
                // Hide list when clicking outside
                document.addEventListener('click', function(e) {
                    if (!container.contains(e.target)) {
                        list.classList.remove('active');
                    }
                });
                // Form validation on submit
                const form = input.closest('form');
                if(form) {
                    form.addEventListener('submit', function(e) {
                        const val = input.value.trim();
                        const exists = nationalitiesData.find(n => n.toLowerCase() === val.toLowerCase());
                        if(!exists) {
                            input.value = '';
                            e.preventDefault();
                            alert('Por favor, selecciona una nacionalidad válida del listado.');
                        } else {
                            input.value = exists; // ensure exact casing
                        }
                    });
                }
            }
            
            document.querySelectorAll('.combo-box-container').forEach(setupCombobox);
        });
    </script>
    <script src="/assets/js/app.js"></script>
</body>
</html>
