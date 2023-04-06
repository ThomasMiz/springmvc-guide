<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
    <link href="/css/main.css" rel="stylesheet"/>
</head>
<body>
<h1>Welcome!</h1>
<c:if test="${not empty user}">
    <h2>Nice to see you, <c:out value="${user.email}" escapeXml="true"/></h2>
    <p>Don't forget you're user id <c:out value="${user.id}" escapeXml="true"/> ;)</p>
</c:if>
<c:url var="registerUrl" value="/register"/>
<p>Register <a href="<c:out value="${registerUrl}" escapeXml="true"/>">here</a>.</p>
</body>
</html>