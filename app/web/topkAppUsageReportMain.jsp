<%@include file='protect.jsp' %>
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
        <title> Top K Main Page</title>

    </head>
    <body>

        <div class="container">
            <div class="row"> <h1><b>Top-k App Usage Report</b> </h1></div>
            <nav class="navbar navbar-default">
                <div class="container-fluid">

                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        <ul class="nav navbar-nav">
                            <li role="presentation"><a href="display.jsp">Home</a></li>
                            <li role="presentation"><a href="basicAppUsageReportMain.jsp">Basic App Usage Report</a></li>
                            <li role="presentation"><a href="smartphoneOveruseReport.jsp">Smart Phone Overuse</a></li>
                             <li role="presentation"><a href="display.jsp">Back</a></li>
                            <li role="presentation"><a href="logout.jsp">Logout</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
            <div class="list-group">
                <a href ="topkAppUsageReport.jsp" class="list-group-item"> Top-k most used apps (given a school) </a>


                <a href ="topkAppUsageReportStudent.jsp" class="list-group-item"> Top-k students with most app usage (given an app category) </a>


                <a href ="topkAppUsageReportSchool.jsp" class="list-group-item"> Top-k schools with most app usage (given an app category) </a>


            </div>
       
        </div>
    </body>
</html>
