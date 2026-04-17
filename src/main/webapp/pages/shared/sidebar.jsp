<%@page contentType="text/html" pageEncoding="UTF-8"%>
<aside class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <i data-lucide="library"></i>
        <span>LSE</span>
    </div>
    <ul class="sidebar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/dashboard" class="${pageContext.request.requestURI.endsWith('dashboard.jsp') ? 'active' : ''}">
                <i data-lucide="layout-dashboard"></i> Dashboard
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/users" class="${pageContext.request.requestURI.endsWith('users.jsp') ? 'active' : ''}">
                <i data-lucide="users"></i> Usuarios
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/authors" class="${pageContext.request.requestURI.endsWith('authors.jsp') ? 'active' : ''}">
                <i data-lucide="feather"></i> Autores
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/books" class="${pageContext.request.requestURI.endsWith('books.jsp') ? 'active' : ''}">
                <i data-lucide="book-open"></i> Libros
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/loans" class="${pageContext.request.requestURI.endsWith('loans.jsp') ? 'active' : ''}">
                <i data-lucide="bookmark"></i> Préstamos
            </a>
        </li>
    </ul>

    <div class="sidebar-footer">
        <button id="theme-toggle" class="btn-theme-toggle">
            <i data-lucide="moon" id="theme-icon"></i>
            <span id="theme-text">Modo Oscuro</span>
        </button>
    </div>
</aside>
