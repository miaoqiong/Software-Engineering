<%@page import="java.util.LinkedHashMap"%>
<%@include file='protect.jsp'%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

        <title>Basic App Usage Report App Category Result</title>
    </head>
    <body>
        <div class="container">
            <div class="row"> <h1><b>Basic App Usage Report By App Category</b> </h1></div>
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
            <table class="table table-hover">
                <tr>
                    <td>App Category</td>
                    <td>Usage Time (s)</td>
                    <td>Percentage (%)</td>

                </tr>
                <%
                    LinkedHashMap<String, Double> result = (LinkedHashMap<String, Double>) request.getAttribute("result");
                    ArrayList<Double> percentage = (ArrayList<Double>) request.getAttribute("percentage");

                    int pos = 0;
                    for (Map.Entry<String, Double> entry : result.entrySet()) {
                        String key = entry.getKey();
                        double l = entry.getValue();
                        
                        
                        long usagetime = Math.round(l);
                        
                        
                        double a = percentage.get(pos);
                        //DecimalFormat f = new DecimalFormat("0.00");
                        long value = Math.round(a);
                        pos++;
                %>
                <tr>
                    <td><%=key%></td>
                    <td><%=usagetime%></td>
                    <td><%=value%></td>  
                </tr>
                <%
                    }
                %>
            </table>
       
        </div>
          
    </body>
</html>
