<%@include file='protect.jsp'%>
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
        <title>Basic App Usage Report Main Page</title>
    </head>
    <body>
        <div class="container">

            <div class="row"> <h1><b>Basic App Usage Report</b> </h1></div>
            <nav class="navbar navbar-default">
                <div class="container-fluid">

                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        <ul class="nav navbar-nav">
                            <li role="presentation"><a href="display.jsp">Home</a></li>
                            <li role="presentation"><a href="topkAppUsageReportMain.jsp">Top K Usage Report</a></li>
                            <li role="presentation"><a href="smartphoneOveruseReport.jsp">Smart Phone Overuse</a></li>
                            <li role="presentation"><a href="logout.jsp">Logout</a></li>
                        </ul>
                    </div>
                </div>
            </nav>

            <div class="list-group">
                <a href="basicAppUsageReportDemo.jsp" class="list-group-item">
                    Basic App Usage Report
                </a>
                <a href="basicAppCategory.jsp" class="list-group-item">
                    Basic App Usage Report - Breakdown by app category
                </a>
                <a href="basicAppUsageDiurnal.jsp" class="list-group-item">
                    Basic App Usage Report - Diurnal pattern of app usage time
                </a>
            </div>
     
        </div>
    </body>
</html>
