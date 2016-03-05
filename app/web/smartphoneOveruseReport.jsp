<%@include file='protect.jsp' %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

        <title>Smartphone Overuse Report</title>
        <style>
            .sub{
                color: grey;
            }.button{
                margin-left: 250px;
            }
        </style>
    </head>
    <body>
        <div class="container">

            <div class="row"> <h1><b>Smartphone Overuse Report</b> </h1></div>
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

            <form action="ProcessSmartphoneOveruse">
                <table class="table">
                    <tr>
                        <td>Please enter a start date:</td>
                        <td><input type="date" name="startdate"/></td>
                    </tr>
                    <tr>
                        <td>Please enter an end date:</td>
                        <td><input type="date" name="enddate"/></td>
                    </tr>
                </table><br />
                <input type="submit" value="View report" class="btn btn-info"/>
            </form>
            <%                String error = (String) session.getAttribute("error");
                if (error != null) {
                    out.println("<p><font style='color: red'>" + error + "</font></p>");
                    session.removeAttribute("error");
                }
            %>
        </div>

    </body>
</html>

