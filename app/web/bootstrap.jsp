<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bootstrap JSON</title>
    </head>
    <body>
        <form action="json/bootstrap"  method="post" enctype="multipart/form-data">
            File:
            <input type="file" name="bootstrap-file" /><br />
            <input type='text' name='token' />
            <!-- substitute the above value with a valid token -->
            <input type="submit" value="Bootstrap" />
        </form>
    </body>
</html>
