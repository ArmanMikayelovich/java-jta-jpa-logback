<%--
  Created by IntelliJ IDEA.
  User: armanar
  Date: 1/15/2020
  Time: 3:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome</title>
</head>
<body>
<% if (request.getSession() != null && request.getSession().getAttribute("username") != null) {%>
<h2>Welcome <%=request.getSession().getAttribute("username")%>
</h2>
<%} else {%>
<h2><a href="login.jsp">Login</a> or <a href="registration.jsp">register</a></h2>
<%}%>
</body>
</html>
