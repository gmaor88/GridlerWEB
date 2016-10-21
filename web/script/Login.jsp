<%--suppress ALL --%>
<%--
  Created by IntelliJ IDEA.
  User: Maor Gershkovitch
  Date: 10/21/2016
  Time: 12:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<%@page import="Utils.Constants, Utils.SessionUtils" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Login</title>
    <script src="script/jquery-3.1.1.min.js"></script>
    <link rel="stylesheet" href="scripts/bootstrap.min.js">
    <link rel="stylesheet" type="text/css" href="css/Login.css">
</head>
<body>
<div class="login">
    <% String usernameFromSession = SessionUtils.getUsername(request);%>
    <% String usernameFromParameter = request.getParameter(Constants.USERNAME) != null ? request.getParameter(Constants.USERNAME) : "";%>
    <% if (usernameFromSession == null) {%>
    <form method="GET" action="Login">
        <div class="login">
            <div class="login-screen">
                <div class="app-title">
                    <h1>Gridler</h1>
                </div>
                <div class="login-form">
                    <div class="control-group">
                        <input type="text" class="login-field" value="<%=usernameFromParameter%>" placeholder="username" id="login-name" name="<%=Constants.USERNAME%>">
                        <label class="login-field-icon fui-user" for="login-name"></label>
                        <input type="checkbox" checked id="IsHuman" name="IsHuman"> Human Player
                    </div>
                    <input type="submit" value="login" class="btn btn-primary btn-large btn-block">
                    <!--errorMsg below will later be filed with script and ajax to show a msg when a name is already exist-->
                    <p class="errorMsg" href="#" name="errorMsg"></p>
                </div>
            </div>
        </div>
    </form>
    <% Object errorMessage = request.getAttribute(Constants.USER_NAME_ERROR);%>
    <% if (errorMessage != null) {%>
    <span class="errorMsg"><%=errorMessage%></span>
    <% } } %>
</div>
</body>
</html>
