<%@include file='protect.jsp'%>
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

        <title>Basic App Usage Diurnal Result</title>
    </head>
    <body>
        <div class="container">
            <div class="row"> <h1><b>Basic App Usage Diurnal Result</b> </h1></div>
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
            <%            LinkedHashMap<Integer, Integer> result = (LinkedHashMap<Integer, Integer>) request.getAttribute("result");
            %>
            <table class="table table-hover">
                <tr>
                    <td>Time Period</td>
                    <td>Total Usage Time(s)</td>
                </tr>
                <%
                    for (Map.Entry<Integer, Integer> entry : result.entrySet()) { %>
                <tr>
                    <%
                        int key = entry.getKey();
                        int value = entry.getValue();
                        int beforeKey = key - 1;
                        String lowerLimit = "" + beforeKey + ":00";
                        String upperLimit = "" + key + ":00";

                        if (upperLimit.equals("24:00")) {
                            upperLimit = "00:00";
                        }

                        if (beforeKey < 10) {
                            lowerLimit = "0" + lowerLimit;
                        }
                        if (key < 10) {
                            upperLimit = "0" + upperLimit;
                        }

                        String time = lowerLimit + "-" + upperLimit;

                    %>
                    <td><%out.println(time);%></td>
                    <td><%out.println(value);%></td>
                </tr>
                <%}%>
            </table>
   
        </div>
           
    </body>
</html>
