<%@include file='protect.jsp' %>
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


        <title>Smartphone Overuse Result</title>
    </head>
    <body>
        <div class="container">
            <div class="row"> <h1><b>Smartphone Overuse Result</b> </h1></div>
            <nav class="navbar navbar-default">
                <div class="container-fluid">

                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        <ul class="nav navbar-nav">
                            <li role="presentation"><a href="display.jsp">Home</a></li>
                            <li role="presentation"><a href="basicAppUsageReportMain.jsp">Basic App Usage Report</a></li>
                            <li role="presentation"><a href="topkAppUsageReportMain.jsp">Top K Usage Report</a></li>
                            <li role="presentation"><a href="display.jsp">Back</a></li>
                            <li role="presentation"><a href="logout.jsp">Logout</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
            <br /><table border="1" class="table">
                <tr>
                    <th>Metrics</th>
                    <th>Numerical Result</th>
                    <th>Index</th>
                </tr>
                <%                
                    ArrayList< String> results = (ArrayList<String>) session.getAttribute("results");
                    session.removeAttribute("results");
                    ArrayList<String> numResults = (ArrayList<String>) session.getAttribute("numResults");
                    session.removeAttribute("numResults");
                %>


                <tr>
                    <td>Average daily smartphone usage duration</td>
                    <td><%=numResults.get(0) + " seconds"%></td>
                    <td><%=results.get(0)%></td>
                </tr>
                <tr>
                    <td>Average daily gaming duration</td>
                    <td><%=numResults.get(1) + " seconds"%></td>
                    <td><%=results.get(1)%></td>
                </tr>
                <tr>
                    <td>Smartphone access frequency</td>
                    <td><%=numResults.get(2)%></td>
                    <td><%=results.get(2)%></td>
                </tr>
                <tr>
                    <td>Overall</td>
                    <%
                        String overall = "";
                        int numSevere = 0;
                        int numLight = 0;
                        int bug = 0;
                        for (String result : results) {
                            if (result.equals("Severe")) {
                                numSevere++;
                            } else if (result.equals("Light")) {
                                numLight++;
                            } else if (result.equals("BUG")) {
                                bug++;
                            }
                        }

                        if (bug != 0) {
                            overall = "BUG OCCUR!";
                        } else if (numSevere > 0) {
                            overall = "Overuse";
                        } else if (numLight == 3) {
                            overall = "Normal";
                        } else {
                            overall = "ToBeCautious";
                        }
                    %>
                    <td></td>
                    <td><%=overall%></td>
                </tr>
            </table>
        </div>
    </body>
</html>
