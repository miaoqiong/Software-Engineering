<%@page import="dao.UserDAO"%>
<%@include file='protect.jsp' %>
<%@page import="java.io.OutputStream"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="entity.UserAppUsage"%>
<%@page import="dao.AppUsageDAO"%>
<%@page import="java.util.Date"%>
<%@page import="utilities.DateFormatter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
        <title>Basic App Usage Report Result</title>


    </head>
    <body>
        <div class="container">

            <div class="row"> <h1><b>Basic App Usage Report Result</b> </h1></div>
            <nav class="navbar navbar-default">
                <div class="container-fluid">

                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        <ul class="nav navbar-nav">
                            <li role="presentation"><a href="display.jsp">Home</a></li>
                            <li role="presentation"><a href="topkAppUsageReportMain.jsp">Top K Usage Report</a></li>
                            <li role="presentation"><a href="smartphoneOveruseReport.jsp">Smart Phone Overuse</a></li>
                             <li role="presentation"><a href="basicAppUsageReportMain.jsp">Back</a></li>
                            <li role="presentation"><a href="logout.jsp">Logout</a></li>
                        </ul>
                    </div>
                </div>
            </nav>

            <%        Integer filter = (Integer) request.getAttribute("filter");
                String order = (String) request.getAttribute("order");
            %>

            <%
                if (filter == 0) {
                    int[][] result = (int[][]) request.getAttribute("result");
                    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
                    if (result != null) {
                        for (int i = 0; i < result.length; i++) {
            %>
            <table class="table table-hover"> 
                <tr>
                    <%
                        for (int j = 0; j < result[i].length; j++) {
                            int val = result[i][j];

                            if (j == 1) {
                    %><td><%out.print("Intense Users: " + val + "(" + calculatePercentage(val, totalUsers) + "%) ");%></td><%
                        }
                        if (j == 2) {
                    %><td><%out.print("Normal Users: " + val + "(" + calculatePercentage(val, totalUsers) + "%) ");%></td><%
                        }
                        if (j == 3) {
                    %><td><%out.print("Mild Users: " + val + "(" + calculatePercentage(val, totalUsers) + "%) ");%></td><%
                        }
                    %>
                </tr>
            </table>
            <%
                }
            %>

            <%
                        }
                    }
                }
                //int filter = Integer.parseInt(filterInput);
                if (filter == 1) {
                    int[][] listing = (int[][]) request.getAttribute("result");
                    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
                    if (listing != null) {

                        UserDAO userDAO = new UserDAO();
                        HashMap<String, String[]> map = userDAO.retrieveFilters();

                        String[] filter1 = map.get(order); //gender
            %><ul class="list-group"><%
                for (int i = 0; i < listing.length; i++) {
                %><li class="list-group-item"><%
                    out.print(filter1[i] + ": " + listing[i][0] + " (" + calculatePercentage(listing[i][0], totalUsers) + "%) --->\t ");
                    for (int j = 0; j < listing[i].length; j++) {
                        if (j != 0) {
                            int val = listing[i][j];

                            if (j == 1) {
                                out.print("Intense Users: " + val + "(" + calculatePercentage(val, totalUsers) + "%) ");
                            }
                            if (j == 2) {
                                out.print("Normal Users: " + val + "(" + calculatePercentage(val, totalUsers) + "%) ");
                            }
                            if (j == 3) {
                                out.print("Mild Users: " + val + "(" + calculatePercentage(val, totalUsers) + "%) ");
                            }
                        }

                    }
                    %></li><br/><%
                        }
                    %></ul><%
                            }
                        }

                        if (filter == 2) {
                            int[][][] listing = (int[][][]) request.getAttribute("result");
                            Integer totalUsers = (Integer) request.getAttribute("totalUsers");
                            UserDAO userDAO = new UserDAO();
                            HashMap<String, String[]> map = userDAO.retrieveFilters();

                            String[] str = order.split(",");

                            String filter1 = str[0].trim(); //gender
                            String filter2 = str[1].trim(); //year
                %><ul class="list-group"><%
                    for (int j = 0; j < listing.length; j++) {

                        String[] fm = map.get(filter1);
                %><li class="list-group-item"><%
                    out.println(fm[j] + ":" + listing[j][0][0] + "(" + calculatePercentage(listing[j][0][0], totalUsers) + "%): ");
                    %><ul class="list-group"><%
                        for (int k = 0; k < listing[j].length; k++) {
                            String[] fl = map.get(filter2);
                            if (k != 0) {
                        %><li class="list-group-item"><%
                            for (int i = 0; i < listing[j][k].length; i++) {
                                if (i == 0) {
                                }
                                int value = listing[j][k][i];
                                if (i == 0) {
                                    out.print(fl[k - 1] + ":" + value + "(" + calculatePercentage(value, totalUsers) + "%): ");
                                } else {

                                    if (i == 1) {
                                        out.print("Intense User: " + value + "(" + calculatePercentage(value, totalUsers) + "%) ");
                                    }
                                    if (i == 2) {
                                        out.print("Normal User: " + value + "(" + calculatePercentage(value, totalUsers) + "%) ");
                                    }
                                    if (i == 3) {
                                        out.print("Mild User: " + value + "(" + calculatePercentage(value, totalUsers) + "%) ");
                                    }

                                }
                            }
                            %></li><br/><%
                                    }
                                }
                            %></ul></li><br/><%
                                }
                        %></ul><%
                            }

                            if (filter == 3) {
                                int[][][][] testList = (int[][][][]) request.getAttribute("result");
                                Integer totalUsers = (Integer) request.getAttribute("totalUsers");
                                UserDAO userDAO = new UserDAO();
                                HashMap<String, String[]> map = userDAO.retrieveFilters();

                                String[] str = order.split(",");

                                String filter1 = str[0].trim(); //gender
                                String filter2 = str[1].trim(); //year
                                String filter3 = str[2].trim();

                %><ul class="list-group"><%                            for (int i = 0; i < testList.length; i++) {
                        if (i != 0) {

                            String[] f1 = map.get(filter1);
                            int val3 = testList[i][0][0][0];
                %><li class="list-group-item"><%
                    out.println(f1[i - 1] + ":" + val3 + "(" + calculatePercentage(val3, totalUsers) + "%) : ");
                    %><ul class="list-group"><%
                        for (int j = 0; j < testList[i].length; j++) {
                            if (j != 0) {

                                String[] f2 = map.get(filter2);
                                int val2 = testList[i][j][0][0];
                        %><li class="list-group-item"><%
                            out.println(f2[j - 1] + ":" + val2 + "(" + calculatePercentage(val2, totalUsers) + "%) : ");
                            %><ul class="list-group"><%
                                for (int k = 0; k < testList[i][j].length; k++) {
                                    if (k != 0) {

                                        String[] f3 = map.get(filter3);
                                        int val1 = testList[i][j][k][0];
                                %><li class="list-group-item"><%
                                    out.print(f3[k - 1] + ":" + val1 + "(" + calculatePercentage(val1, totalUsers) + "%) : ");

                                    for (int m = 0; m < testList[i][j][k].length; m++) {
                                        int value = testList[i][j][k][m];
                                        if (m == 1) {
                                            out.print("Intense user: " + value + "(" + calculatePercentage(value, totalUsers) + "%) ");
                                        }
                                        if (m == 2) {
                                            out.print("Normal user: " + value + "(" + calculatePercentage(value, totalUsers) + "%) ");
                                        }
                                        if (m == 3) {
                                            out.print("Mild user: " + value + "(" + calculatePercentage(value, totalUsers) + "%) ");
                                        }
                                    }

                                    %></li><%                                            }

                                        }
                                    %></ul><%
                            %></li><br/><%                                    }

                                }
                            %></ul><%
                    %></li><br/><%                            }
                        }
                    %></ul><%
                        }

                        if (filter == 4) {
                            int[][][][][] result = (int[][][][][]) request.getAttribute("result");
                            Integer totalUsers = (Integer) request.getAttribute("totalUsers");
                            UserDAO userDAO = new UserDAO();
                            HashMap<String, String[]> map = userDAO.retrieveFilters();

                            String[] str = order.split(",");

                            String filter1 = str[0].trim(); //gender
                            String filter2 = str[1].trim(); //year
                            String filter3 = str[2].trim();
                            String filter4 = str[3].trim();
                %><ul class="list-group"><%
                    for (int i = 0; i < result.length; i++) {
                        if (i != 0) {
                %><li class="list-group-item"><%
                    String[] f1 = map.get(filter1);
                    int val1 = result[i][0][0][0][0];

                    out.println(f1[i - 1] + ":" + val1 + "(" + calculatePercentage(val1, totalUsers) + "%) : ");
                    %><ul class="list-group"><%
                        for (int j = 0; j < result[i].length; j++) {
                            if (j != 0) {

                                String[] f2 = map.get(filter2);
                                int val2 = result[i][j][0][0][0];
                        %><li class="list-group-item"><%
                            out.println(f2[j - 1] + ":" + val2 + "(" + calculatePercentage(val2, totalUsers) + "%) : ");
                            %><ul class="list-group"><%
                                for (int k = 0; k < result[i][j].length; k++) {
                                    if (k != 0) {
                                %><li class="list-group-item"><%
                                    String[] f3 = map.get(filter3);
                                    int val3 = result[i][j][k][0][0];

                                    out.println(f3[k - 1] + ":" + val3 + "(" + calculatePercentage(val3, totalUsers) + "%) : ");
                                    %><ul class="list-group"><%
                                        for (int m = 0; m < result[i][j][k].length; m++) {
                                            if (m != 0) {

                                                String[] f4 = map.get(filter4);
                                                int val4 = result[i][j][k][m][0];
                                        %><li class="list-group-item"><%
                                            out.println(f4[m - 1] + ":" + val4 + "(" + calculatePercentage(val4, totalUsers) + "%) : ");

                                            for (int n = 0; n < result[i][j][k][m].length; n++) {
                                                int value = result[i][j][k][m][n];
                                                if (n == 1) {
                                                    out.println("Intense user: " + value + "(" + calculatePercentage(value, totalUsers) + "%) : ");
                                                }
                                                if (n == 2) {
                                                    out.println("Normal user: " + value + "(" + calculatePercentage(value, totalUsers) + "%) : ");
                                                }
                                                if (n == 3) {
                                                    out.println("Mild user: " + value + "(" + calculatePercentage(value, totalUsers) + "%) : ");
                                                }
                                            }
                                            %></li><br/><%
                                                    }

                                                }
                                            %></ul><%
                                    %></li><br/><%                                        }

                                        }
                                    %></ul><%
                            %></li><br/><%                                }

                                }
                            %></ul><%
                    %></li><br/><%                        }

                        }
                    %></ul><%
                        }

                %>
              
        </div>
        
    </body>
</html>

<%!
    public static long calculatePercentage(int value, int totalUsers) {
        double percentage = ((double) value / totalUsers) * 100;
        return Math.round(percentage);
    }
%>