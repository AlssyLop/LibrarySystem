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
            <h1 class="header-title">Dashboard</h1>
        </div>

        <div class="dashboard-grid">
            <div class="stat-card">
                <div class="stat-icon">
                    <i data-lucide="users"></i>
                </div>
                <div class="stat-details">
                    <h3>Total Usuarios</h3>
                    <p>${totalUsers}</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i data-lucide="feather"></i>
                </div>
                <div class="stat-details">
                    <h3>Autores Registrados</h3>
                    <p>${totalAuthors}</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i data-lucide="book-open"></i>
                </div>
                <div class="stat-details">
                    <h3>Libros en Catálogo</h3>
                    <p>${totalBooks}</p>
                </div>
            </div>
            
            <div class="stat-card" style="border-color: var(--accent-amber);">
                <div class="stat-icon">
                    <i data-lucide="bookmark"></i>
                </div>
                <div class="stat-details">
                    <h3 style="color: var(--accent-amber);">Préstamos Activos</h3>
                    <p>${activeLoans}</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i data-lucide="history"></i>
                </div>
                <div class="stat-details">
                    <h3>Historial Préstamos</h3>
                    <p>${totalLoans}</p>
                </div>
            </div>
        </div>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>
