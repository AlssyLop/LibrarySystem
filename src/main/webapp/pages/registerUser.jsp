<%-- 
    Document   : registerUser
    Created on : Apr 12, 2026, 8:51:53 PM
    Author     : LenovoV14G4-AMN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>
<head>
    <meta charset="UTF-8">
    <title>Registrar Usuario</title>
</head>
<body>
    <h2>Formulario de Registro de Usuario</h2>
    <form action="${pageContext.request.contextPath}/UserServlet?action=register" method="post">
        Nombre: <input type="text" name="name"><br>
        Email: <input type="text" name="email"><br>
        Teléfono: <input type="text" name="phone"><br>
        <input type="submit" value="Registrar">
    </form>
</body>
</html>
