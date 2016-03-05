<%@include file='protect.jsp'%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
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

        <title>Basic App Usage Report</title>
        <script type="text/javascript" src="https://code.jquery.com/jquery-1.10.2.js"></script>

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
            <%            session.removeAttribute("basicApp");
                session.setAttribute("basicApp", "filter");
            %>
            <div class="row"> <h1><b>Basic App Usage Report</b> </h1></div>
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
            <h3 class="sub">Breakdown by usage time category and demographics</h3>
            <form action="ProcessBasicAppUsage">
                <table  class="table">
                    <tr>
                    <label for="ex2">Please enter a start date:</label> 
                    <input type="date" name="startdate"/>
                    <br/>
                    </tr>
                    <tr>
                    <label for="ex2">Please enter an end date:</label>  
                    <input type="date" name="enddate"/>
                    <br/><br/>
                    </tr>
                    <label for="ex2">Please choose your filter(s) <i>[in order]</i>:</label>
                    <br/><br/>
                    <select name="fc" class="go">
                        <option value="">--Select--</option>
                        <option value="year">Year</option>
                        <option value="gender">Gender</option>
                        <option value="school">School</option>
                        <option value="cca">CCA</option>
                    </select>

                    <select name="sc" class="go">
                        <option value="">--Select--</option>
                        <option value="year">Year</option>
                        <option value="gender">Gender</option>
                        <option value="school">School</option>
                        <option value="cca">CCA</option>
                    </select>

                    <select name="tc" class="go">
                        <option value="">--Select--</option>
                        <option value="year">Year</option>
                        <option value="gender">Gender</option>
                        <option value="school">School</option>
                        <option value="cca">CCA</option>
                    </select>
                    <select name="lc" class="go">
                        <option value="">--Select--</option>
                        <option value="year">Year</option>
                        <option value="gender">Gender</option>
                        <option value="school">School</option>
                        <option value="cca">CCA</option>
                    </select>
                    <script type="text/javascript">
                        $(".go").change(function () {
                            var selVal = [];
                            $(".go").each(function () {
                                selVal.push(this.value);
                            });

                            $(this).siblings(".go").find("option").removeAttr("disabled").filter(function () {
                                var a = $(this).parent("select").val();
                                return (($.inArray(this.value, selVal) > -1) && (this.value != a))
                            }).attr("disabled", "disabled");
                        });

                        $(".go").eq(0).trigger('change');
                    </script>
                    <br />
                </table>
                <input type="submit" value="View report" class="btn btn-info"/>
            </form>
            <%
                String errorMsg = (String) request.getAttribute("errorMsg");
                if (errorMsg != null) {
                    out.println("<b><font style='color: red'>" + errorMsg + "</font></b>");
                }
            %>
          
        </div>
  
    </body>
</html>
