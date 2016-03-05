<%@include file='protect.jsp'%>
<%@page import="entity.User"%>
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
        <title>Main Page</title>

        <style type="text/css">
            .container {
                margin-right: auto;
                margin-left: auto;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1><b>Welcome! <%= user.getFullName()%></b></h1>

            <div class="row">
                <div class="col-lg-3">
                    <img  src="image/app.png" alt="Generic placeholder image" width="128" height="128">
                    <h2>App Usage</h2>
                    <p>A user can see basic app usage stats for any given duration.</p>
                    <p><a class="btn btn-default" href="basicAppUsageReportMain.jsp" role="button">Go to Report &raquo;</a></p>
                </div>
                <div class="col-lg-3">
                    <img src="image/top.png" alt="Generic placeholder image" width="128" height="128">
                    <h2>Top K</h2>
                    <p>A user can see the top-k users/apps for any given duration.</p>
                    <p><a class="btn btn-default" href="topkAppUsageReportMain.jsp" role="button">Go to Report &raquo;</a></p>
                </div>
                <div class="col-lg-3">
                    <img src="image/phone.png" alt="Generic placeholder image" width="128" height="128">
                    <h2>Phone Usage</h2>
                    <p>A user can see a smartphone overuse index (based on smartphone usage time, gaming time, and frequency of checking smartphones) for themselves for self-feedback and potential behaviour changes.</p>
                    <p><a class="btn btn-default" href="smartphoneOveruseReport.jsp" role="button">Go to Report &raquo;</a></p>
                </div>
            </div>


            <a href ="logout.jsp">Logout</a>
        </div>
    </body>
</html>
