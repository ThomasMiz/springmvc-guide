<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <link href="<c:url value="/css/main.css"/>" rel="stylesheet"/>
</head>
<body>
<h2><spring:message code="profile.greeting" arguments="${user.email}"/></h2>
</body>
</html>