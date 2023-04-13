<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <link href="<c:url value="/css/main.css"/>" rel="stylesheet"/>
</head>
<body>
<h1><spring:message code="index.welcome"/></h1>
<c:url var="registerUrl" value="/register"/>
<p>Register <a href="<c:out value="${registerUrl}" escapeXml="true"/>">here</a>.</p>
<p>Here's a random number: ${randnum}</p>
</body>
</html>