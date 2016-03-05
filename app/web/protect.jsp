<%@page import="entity.User"%>

<%
// check if user is authenticated
    User user = (User) session.getAttribute("user");

    if (user == null) {
        // not authenticated, force user to authenticate
        response.sendRedirect("login.jsp");
        return;
    }
%>