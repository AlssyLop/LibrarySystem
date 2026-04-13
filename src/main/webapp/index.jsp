<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Redirige permanentemente al path configurado del servlet de registro
    response.sendRedirect(request.getContextPath() + "/registeruser");
%>
