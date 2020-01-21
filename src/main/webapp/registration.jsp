<%--
  Created by IntelliJ IDEA.
  User: armanar
  Date: 1/13/2020
  Time: 11:56 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<style>
    error {
        color: red;
    }
</style>
<head>
    <title>Registration page</title>
</head>
<body>
<h2>Welcome</h2>
<% System.out.println("as");%>
<form method="post" action="${pageContext.request.contextPath}/register">
    <a>Username</a>
    <label>
        <input type="text" name="username">
    </label>
    <error> <%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %></error>
    <br/>
    <a>password</a>
    <label>
        <input type="password" name="password">
    </label>
    <error> <%= request.getAttribute("password") != null ? request.getAttribute("password") : "" %></error>
    <br/>
    <a>birthday</a>
    <label>
        <input type="date" name="birthday">
    </label>
    <error> <%= request.getAttribute("birthday") != null ? request.getAttribute("birthday") : "" %></error>
    <br/>
    <a>email</a>
    <label>
        <input type="email" name="email">
    </label>
    <error> <%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %></error>
    <br/>
    <a>country</a>
    <label>
        <input type="text" name="country">
    </label>
    <error> <%= request.getAttribute("country") != null ? request.getAttribute("country") : "" %></error>
    <br/>
    <input type="submit" name="Send">
</form>
</body>
</html>
