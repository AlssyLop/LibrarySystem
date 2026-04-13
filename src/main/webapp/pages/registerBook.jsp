<%-- 
    Document   : registerBook
    Created on : Apr 12, 2026, 8:52:18 PM
    Author     : LenovoV14G4-AMN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>
<head><title>Registrar Libro</title></head>
<body>
    <h2>Formulario de Registro de Libro</h2>
    <form action="${pageContext.request.contextPath}/BookServlet?action=register" method="post">
        Título: <input type="text" name="title"><br>
        ISBN: <input type="text" name="isbn"><br>
        Año: <input type="text" name="year"><br>
        ID Autor: <input type="text" name="idAuthor"><br>
        <input type="submit" value="Registrar">
    </form>
</body>
</html>
