<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/css/style.css"/>"/>
</head>
<body>
<h2>Login</h2>
<c:url var="loginUrl" value="/login"/>
<!--
    Para spring security, el formulario de login lo hacemos con un form html, no un form:form de spring. Este
    formulario no es manejado por nosotros pero interceptado por spring-security. Ver webapp.config.WebAuthConfig.java
-->
<form action="${loginUrl}" method="post">
    <div>
        <label>
            Email:
            <input type="text" name="email" placeholder="Email"/>
        </label>
    </div>
    <div>
        <label>
            Password:
            <input type="password" name="password" placeholder="Password"/>
        </label>
    </div>
    <div>
        <label>
            Remember:
            <input type="checkbox" name="rememberme"/>
        </label>
    </div>
    <div>
        <input type="submit" value="Log in"/>
    </div>
</form>
</body>
</html>