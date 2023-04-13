<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/css/style.css"/>"/>
</head>
<body>
<h2>Register new user</h2>
<c:url var="registerUrl" value="/register"/>
<form:form modelAttribute="form" action="${registerUrl}" method="post">
    <div>
        <!-- Que los errores de email se representen con un tag <p> con la clase css "error" -->
        <form:errors path="email" cssClass="error" element="p"/>
        <form:label path="email"> <!-- Estos se pueden reemplazar con un label normal en este caso -->
            Email: <form:input path="email" placeholder="Email"/>
        </form:label>
    </div>
    <div>
        <form:errors path="password" cssClass="error" element="p"/>
        <form:label path="password">
            Password: <form:input path="password" type="password" placeholder="Password"/>
        </form:label>
    </div>
    <div>
        <form:errors path="repeatPassword" cssClass="error" element="p"/>
        <form:label path="repeatPassword">
            Repeat password: <form:input path="repeatPassword" type="password" placeholder="Repeat password"/>
        </form:label>
    </div>
    <div>
        <input type="submit" value="Register">
    </div>
</form:form>
</body>
</html>