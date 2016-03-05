
<%@page import="java.util.ArrayList"%>
<%@page import="entity.User"%>
<%@page import="entity.Admin"%>
<%@page import="dao.UserDAO"%>
<!DOCTYPE html>

    <%
        String emailID = request.getParameter("emailID");
        String password = request.getParameter("password");

        if (emailID.toLowerCase().equals("admin") && password.equals("password")) {
            Admin admin = new Admin(emailID, password);
            response.sendRedirect("admin");
            session.setAttribute("admin", admin);
            return;
        }
        boolean DEBUG = true;
        if (DEBUG) {
            if (emailID.equals("user") && password.equals("user")) {
                //Admin admin = new Admin (userName, password);
                User u = new User("user", "user", "user", password, "user", "user");
                response.sendRedirect("display.jsp");
                session.setAttribute("user", u);
                return;
            }
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.retrieveSingleUser(emailID, password);

        if (user != null) {
            session.setAttribute("user", user);
            response.sendRedirect("display.jsp");

        } else {
            String errorMsg = "Invalid username/password";
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            request.setAttribute("error", errorMsg);
            rd.forward(request, response);
        }
    %>  

