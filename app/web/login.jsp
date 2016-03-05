<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Login</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">


        <style type="text/css">
            .container {
                width:300px; 
                margin:0 auto;
            }
            #test{
                margin: 0;
                margin-top: 13.5cm;
                background-image: url('image/image.png');
                background-size: 1920px 1080px;
                background-repeat:no-repeat;
                display: compact;
                font: 13px/18px "Helvetica Neue", Helvetica, Arial, sans-serif;
            }
            #font{
                color: black;
                font: bold;
            }
            html { 
                background: url('image/image.png') no-repeat center center fixed; 
                -webkit-background-size: cover;
                -moz-background-size: cover;
                -o-background-size: cover;
                background-size: cover;
            }
        </style>
    </head>
    <%
        String errorMsgs = (String) request.getAttribute("error");
        if (errorMsgs == null) {
            errorMsgs = "";
        }
    %>
    <body id="test">
        <div class="container">
            <form name =" login_form" action ="authenticate.jsp" method="post" class="form-signin">
                <h1 class="form-signin-heading" id="font"></h1>
                <label for="inputUsername" class="sr-only">EmailID</label>
                <input type="text" name ="emailID" class="form-control" placeholder="Username" required autofocus>
                <label for="inputPassword" class="sr-only">Password</label>
                <input type="password" name ="password" class="form-control" placeholder="Password" required>

                <button class="btn btn-lg btn-primary btn-block" type="submit" name="Submit" value ="Submit">Sign in</button>
            </form>
        </div>

        <p class="container"><font color="red"><%=errorMsgs%> </font></p>
    </body>
</html>
