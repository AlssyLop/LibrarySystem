<%-- 
    Document   : registerLoan
    Created on : Apr 12, 2026, 8:52:31 PM
    Author     : LenovoV14G4-AMN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>
<head><title>Registrar Préstamo</title></head>
<body>
    <h2>Formulario de Registro de Préstamo</h2>
    <form action="${pageContext.request.contextPath}/LoanServlet?action=register" method="post">
        Fecha Préstamo (YYYY-MM-DD): <input type="text" name="loanDate"><br>
        Fecha Devolución (YYYY-MM-DD): <input type="text" name="returnDate"><br>
        ID Usuario: <input type="text" name="idUser"><br>
        ID Libro: <input type="text" name="idBook"><br>
        <input type="submit" value="Registrar">
    </form>
</body>
</html>
