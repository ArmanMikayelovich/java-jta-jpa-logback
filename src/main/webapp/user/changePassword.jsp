<%--
  Created by IntelliJ IDEA.
  User: armanar
  Date: 1/15/2020
  Time: 3:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<style>
    error {
        color: red;
    }
</style>
<head>
    <title>Change password</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/user/change-password" method="post">
    <br/>
    <label>
        Old password
        <input type="password" name="password">
        <error><%= request.getAttribute("password") != null ? request.getAttribute("password") : "" %>
        </error>
    </label>
    <br/>
    <label>
        new password
        <input type="password" name="newPassword">
        <error><%= request.getAttribute("newPassword") != null ? request.getAttribute("newPassword") : "" %>
        </error>
    </label>
    <br/>
    <input type="submit" value="Change password">
</form>
</body>
</html>
