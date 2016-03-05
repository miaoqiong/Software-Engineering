<%@page import="utilities.BootstrapUpload"%>
<%@page import="java.io.File"%>
<%@page import="entity.Admin"%>
<%@include file="protectAdmin.jsp" %>

<!DOCTYPE html>

<%    Admin admin1 = (Admin) session.getAttribute("admin");
    if (admin1 == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<%
    //get relative path so will work on all machines (to be tested)
//    String dir = request.getServletPath();
//    int index = dir.lastIndexOf('/');
//    dir = index != -1 ? dir.substring(0, index + 1) : "";
//    dir = application.getRealPath(dir + "uploads");
    String dir = System.getenv("OPENSHIFT_DATA_DIR") + "uploads";
    File directory = new File(dir);
%>
<html>
    <%@ page language="java" import="javazoom.upload.*,java.util.*" %>
    <%@ page errorPage="ExceptionHandler.jsp" %>

    <jsp:useBean id="upBean" scope="page" class="javazoom.upload.UploadBean" >
        <jsp:setProperty name="upBean" property="folderstore" value="<%=dir%>" />
    </jsp:useBean>

    <head>
        <title>Admin Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">


        <style TYPE="text/css">
            <!--
            .style1 {
                margin:auto;
                font-size: 12px;
                font-family: Verdana;

            }
            -->

            .center {
                margin: auto;
                width: 25%;
                border:3px solid #3366FF;
                padding: 10px;
            }
        </style>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    </head>
    <body>

        <h1 align ='center'>Admin Bootstrap</h1>
        <ul class="style1">
            <%

                BootstrapUpload.cleanAll();
                if (MultipartFormDataRequest.isMultipartFormData(request)) {
                    // Uses MultipartFormDataRequest to parse the HTTP request.
                    MultipartFormDataRequest mrequest = new MultipartFormDataRequest(request);
                    String todo = null;
                    if (mrequest != null) {
                        todo = mrequest.getParameter("todo");
                    }
                    if ((todo != null) && (todo.equalsIgnoreCase("upload"))) {
                        Hashtable files = mrequest.getFiles();
                        UploadFile file = (UploadFile) files.get("uploadfile");
                        if (file != null && file.getFileName() != null) {

                            if (file.getContentType().equals("application/octet-stream") || file.getContentType().equals("application/x-zip-compressed") || file.getContentType().equals("application/zip")) {

                                out.println("<li>Form field : uploadfile" + "<BR> Uploaded file : " + file.getFileName() + " (" + file.getFileSize() + " bytes)" + "<BR> Content Type : " + file.getContentType());
                                upBean.store(mrequest, "uploadfile");
                                response.sendRedirect("/adminDisplay");
                            } // Uses the bean now to store specified by jsp:setProperty at the top.
                            else {
                                out.println("<li>Invalid file type");
                            }
                        } else {
                            out.println("<li>No uploaded files");
                        }
                    } else {
                        out.println("<BR> todo=" + todo);
                    }
                }


            %>
        </ul>
        <div class="container">
            <form method="post" action="adminDisplay.jsp" name="upform" enctype="multipart/form-data">
                <table class="style1">
                    <tr>
                        <td ><b>Select a file to upload :</b></td>
                    </tr>
                    <tr>
                        <td >
                            <input type="file" name="uploadfile" accept = ".zip">
                        </td>
                    </tr>

                    <tr>
                        <td >
                            <input type="hidden" name="todo" value="upload">
                            <input type="submit" name="Submit" value="Bootstrap">
                            <input type="reset" name="Reset" value="Cancel">
                        </td>
                    </tr>
                    <tr> <td>&nbsp;</td></tr>
                    <tr>
                        <td >
                            <a href='updateDisplay.jsp'>Update</a>
                            <a href= "logout.jsp">Logout</a>
                        </td>
                    </tr>

                </table>
                <br>


            </form>
        </div>
        <div class="container">
            <%            ArrayList<Integer> totalrecord = (ArrayList<Integer>) request.getAttribute("totalNum");
                if (totalrecord != null) {
                    out.println("<table class='table table-hover' ><tr><td>");
                    out.println("Total number of app lookup : </td><td>" + totalrecord.get(1));
                    out.println("</td></tr><tr><td>");

                    out.println("Total number of app usage : </td><td>" + totalrecord.get(2));
                    out.println("</td></tr><tr><td>");

                    out.println("Total number of demographic : </td><td>" + totalrecord.get(0));
                    out.println("</td></tr></table><br>");
                }
                ArrayList<String> Errors = (ArrayList<String>) request.getAttribute("error");
                String z = (String) request.getAttribute("test");
                if (z != null) {
                    out.println(z);
                }

                if (Errors != null) {
                    out.println("<table class='table table-hover'><tr><th> Error Message </th></tr>");
                    for (int i = 0; i < Errors.size(); i++) {
                        String s = Errors.get(i);
                        if (!s.contains(" Line Number: 1,")) {

                            out.println("<tr><td>" + s + "</td></tr>");
                            out.println();
                        }

                    }
                    out.println("</table>");

                }


            %>
        </div>

    </body>
</html>