<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/css/style.css"/>"/>
</head>
<body>
<h2><spring:message code="register.title"/></h2>
<h4><spring:message code="register.subtitle"/></h4>
<c:url var="registerUrl" value="/register"/>
<form:form modelAttribute="registerForm" action="${registerUrl}" method="post">
    <div>
        <!-- Que los errores de email se representen con un tag <p> con la clase css "error" -->
        <form:errors path="email" cssClass="error" element="p"/>
        <form:label path="email"> <!-- Estos se pueden reemplazar con un label normal en este caso -->
            <spring:message code="registerForm.email"/>
            <spring:message var="emailHint" code="registerForm.email.hint"/>
            <form:input path="email" placeholder="${emailHint}"/>
        </form:label>
    </div>
    <div>
        <form:errors path="password" cssClass="error" element="p"/>
        <form:label path="password">
            <spring:message code="registerForm.password"/>
            <spring:message var="passwordHint" code="registerForm.password.hint"/>
            <form:input path="password" type="password" placeholder="${passwordHint}"/>
        </form:label>
    </div>
    <div>
        <form:errors path="repeatPassword" cssClass="error" element="p"/>
        <form:label path="repeatPassword">
            <spring:message code="registerForm.repeatPassword"/>
            <spring:message var="repeatPasswordHint" code="registerForm.repeatPassword.hint"/>
            <form:input path="repeatPassword" type="password" placeholder="${repeatPasswordHint}"/>
        </form:label>
    </div>
    <div>
        <spring:message var="submitText" code="registerForm.submit"/>
        <input type="submit" value="${submitText}">
    </div>
</form:form>
</body>
</html>