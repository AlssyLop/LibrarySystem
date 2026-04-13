<%-- 
    Document   : registerAuthor
    Created on : Apr 12, 2026, 8:52:07 PM
    Author     : LenovoV14G4-AMN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>
<head><title>Registrar Autor</title></head>
<body>
    <h2>Formulario de Registro de Autor</h2>
    <form action="${pageContext.request.contextPath}/AuthorServlet?action=register" method="post">
        Nombre: <input type="text" name="name"><br>
        Nacionalidad: <input type="text" name="nationality"><br>
        <input type="submit" value="Registrar">
    </form>
</body>
</html>
