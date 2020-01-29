<%@ page import="com.energizeglobal.internship.model.User" %>
<%@ page import="com.energizeglobal.internship.service.UserService" %>
<%@ page import="javax.ejb.EJB" %>
<%@ page import="com.energizeglobal.internship.service.UserServiceWithJTA" %>
<%@ page import="javax.inject.Inject" %>
<%@ page import="javax.annotation.Resource" %>
<%@ page import="java.util.Properties" %>
<%@ page import="javax.naming.Context" %>
<%@ page import="javax.naming.InitialContext" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome <%= session.getAttribute("username")%>
    </title>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

</head>
<body>
<%!UserService userService = UserService.getInstance(); %>

<% User user = userService.findByUsername((String) session.getAttribute("username"));%>
<table>
    <tr>
        <th>Username</th>
        <th>Birthday</th>
        <th>Email</th>
        <th>Country</th>
        <th>Is admin</th>
    </tr>
    <tr>
        <td><%=user.getUsername()%>
        </td>
        <td><%=user.getBirthday().toString()%>
        </td>
        <td><%=user.getEmail()%>
        </td>
        <td><%=user.getCountry()%>
        </td>
        <td><%= Boolean.valueOf(user.isAdmin()).toString() %>
        </td>
    </tr>

</table>
<% if (userService.isAdmin((String) session.getAttribute("username"))) {%>
<a href="../admin/adminPage.jsp">Admin panel</a>
<% } %>
<form action="${pageContext.request.contextPath}/user/changeUser.jsp" method="post">
    <input type="hidden" name="username" value="<%=(String)session.getAttribute("username")%>"/>
    <input type="submit" value="Change info"/>
</form>
<form action="${pageContext.request.contextPath}/user/delete" method="post">
    <input type="hidden" name="username" value="<%=(String)session.getAttribute("username")%>"/>
    <input type="submit" value="Delete"/>
</form>
<br/>
<a href="${pageContext.request.contextPath}/user/changePassword.jsp">Change password</a>
<a href="${pageContext.request.contextPath}/logout">Logout</a>

</body>
</html>
