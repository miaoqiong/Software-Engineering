<%@include file='protect.jsp' %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

        <title>Basic App Usage Report</title>
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

            <div class="row"> <h1><b>Diurnal Pattern Of App Usage Time</b> </h1></div>
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

            <form action="ProcessDiurnal">
                <table class="table">
                    <tr>            
                    <h3 class="sub">Please enter a date:</h3>
                    <input type="date" name="startdate" required/>
                    </tr>


                    <br>
                    <%    String errorMsg = (String) request.getAttribute("errorMsg");
                        if (errorMsg != null) {
                            out.println("<b><font style='color: red'>" + errorMsg + "</font></b>");
                        }
                    %>
                    <tr>
                    <h3 class="sub">Please choose report requirements:</h3>
                    <div class="col-xs-3">
                        <label for="ex2">Year Filter: </label>
                        <select name="year" class="form-control">
                            <option value="a">--Select Year--</option>
                            <option value="2011">2011</option>
                            <option value="2012">2012</option>
                            <option value="2013">2013</option>
                            <option value="2014">2014</option>
                            <option value="2015">2015</option>
                        </select>

                        <label for="ex2">Gender Filter: </label>
                        <select name="gender" class="form-control">
                            <option value="a">--Select Gender--</option>
                            <option value="m">Male</option>
                            <option value="f">Female</option>
                        </select>      

                        <label for="ex2">School Filter: </label>
                        <select name="school" class="form-control">
                            <option value="a">--Select School--</option>
                            <option value="business">Business</option>
                            <option value="accountancy">Accountancy</option>
                            <option value="sis">Information System</option>
                            <option value="economics">Economics</option>
                            <option value="law">Law</option>
                            <option value="socsc">Social Science</option>
                        </select> 
                        </tr>
                        <br>
                        </table>
                        <input type="submit" value="View report" class="btn btn-info"/>
                    </div>
                    </br>
            </form>
            
        </div>

    </body>
</html>
