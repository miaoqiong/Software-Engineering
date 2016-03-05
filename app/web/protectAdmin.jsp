<%@page import="entity.Admin"%>
<%
    Admin admin;
    admin = (Admin)session.getAttribute("admin");
    
    if (admin == null) {
        // not authenticated, force user to authenticate
        response.sendRedirect("login.jsp");
        return;
    } 
    
    
    %>