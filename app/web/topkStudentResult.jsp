<%@include file='protect.jsp'%>
<%@page import="entity.TopkUsageResult"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

        <title>Top K Usage Report Result</title>
    </head>
    <body>
        <div class="container">
            <div class="row"> <h1><b>Top-k students with most app usage Result</b> </h1></div>
            <nav class="navbar navbar-default">
                <div class="container-fluid">

                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        <ul class="nav navbar-nav">
                            <li role="presentation"><a href="display.jsp">Home</a></li>
                            <li role="presentation"><a href="basicAppUsageReportMain.jsp">Basic App Usage Report</a></li>
                            <li role="presentation"><a href="smartphoneOveruseReport.jsp">Smart Phone Overuse</a></li>
                            <li role="presentation"><a href="topkAppUsageReportMain.jsp">Back</a></li>
                            <li role="presentation"><a href="logout.jsp">Logout</a></li>

                        </ul>
                    </div>
                </div>
            </nav>
            <%    LinkedHashMap<Integer, TopkUsageResult> result = (LinkedHashMap<Integer, TopkUsageResult>) request.getAttribute("result");

                if (result.size() != 0) {
            %>
            <table class="table table-hover">
                <tr>
                    <td><b>Rank</b></td>
                    <td><b>Mac-address & Student Name</b></td>
                    <td><b>Total Time</b></td>
                </tr>
                <%
                    for (Map.Entry<Integer, TopkUsageResult> entry : result.entrySet()) {
                        int key = entry.getKey();
                        ArrayList<String> names = entry.getValue().getResultName();
                        String namelist = "";
                        for (int i = 0; i < names.size(); i++) {
                            namelist = namelist + " " + names.get(i) + "  ";
                        }
                        long usagetime = entry.getValue().getUsageTime();
                %>
                <tr>
                    <td>
                        <%=key%>
                    </td>
                    <td>
                        <%=namelist%>
                    </td>
                    <td>
                        <%=usagetime%>
                    </td>
                </tr>
                <%
                    }
                } else {
                %>
            </table>
            <%
            %>
            <h3>No record found.</h3>
            <%            }
            %>

        </div>
    </body>
</html>
