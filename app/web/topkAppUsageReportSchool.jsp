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
        <title>Top-k schools with most app usage</title>
    </head>
    <body>
        <div class="container">
            <div class="row"> <h1><b>Top-k schools with most app usage</b> </h1></div>
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
            <form action ="ProcessTopkSchool">
                <table class="table">
                    <tr>
                        <td>Please enter a start date:</td>
                        <td><input type="date" name="startdate" required/></td>
                    </tr>
                    <tr>
                        <td>Please enter an end date:</td>
                        <td><input type="date" name="enddate" required/></td>
                    </tr>

                    <tr>
                    <div class="col-xs-3">
                        <td><label for="ex2">Select K Value:</label></td>
                        <td>
                            <select name="kvalue" class="form-control">
                                <option value = "a">--Select--</option>
                                <%                                    for (int i = 1; i <= 10; i++) {
                                %>
                                <option value ="<%=i%>"><%=i%></option>
                                <%
                                    }
                                %>
                            </select>
                        </td>
                    </div>
                    </tr>
                    <tr><div class="col-xs-3">
                        <td><label for="ex2">Select App Category:</label></td>
                        <td>
                            <select name="appCategory" class="form-control">
                                <option value = "a">--Select--</option>

                                <option value ="books">Books</option>
                                <option value ="social">Social</option>
                                <option value ="education">Education</option>
                                <option value ="entertainment">Entertainment</option>
                                <option value ="information">Information</option>
                                <option value ="library">Library</option>
                                <option value ="local">Local</option>
                                <option value ="tools">Tools</option>
                                <option value ="fitness">Fitness</option>
                                <option value ="games">Games</option>
                                <option value ="others">Others</option>
                            </select>
                        </td>
                    </div>
                    </tr>

                </table>
                <input type="submit" value="View report" class="btn btn-info"/>
            </form>
            <%
                String errormessage = (String) request.getAttribute("errormessage");
                if (errormessage != null) {
                    out.println("<b><font style='color: red'>" + errormessage + "</font></b>");
                }
            %>
        </div>
    </body>
</html>
