<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/css/style.css"/>"/>
</head>
<body>
<h2>Register new user</h2>
<form action="<c:url value="/register"/>" method="post">
    <div>
        <c:if test="${email_error}">
            <p class="error">Invalid email</p>
        </c:if>
        <label>Email: <input type="text" placeholder="Email" name="email" value="${email}"></label>
    </div>
    <div>
        <c:if test="${password_error}">
            <p class="error">Invalid password</p>
        </c:if>
        <label>Password: <input type="password" placeholder="Password" name="password" value="${password}"></label>
    </div>
    <div>
        <c:if test="${repeatPassword_error}">
            <p class="error">Passwords don't match</p>
        </c:if>
        <label>Repeat password: <input type="password" placeholder="Repeat password" name="repeat_password" value="${repeatPassword}"></label>
    </div>
    <div>
        <input type="submit" value="Register">
    </div>
</form>
</body>
</html>