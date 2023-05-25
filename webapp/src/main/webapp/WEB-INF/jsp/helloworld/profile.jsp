<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <link href="<c:url value="/css/main.css"/>" rel="stylesheet"/>
</head>
<body>
<h2><spring:message code="profile.greeting" arguments="${user.email}"/></h2>

<h3><spring:message code="profile.issuelist"/></h3>
<ul>
    <c:forEach var="issue" items="${user.reportedIssues}">
        <li>
            <c:out value="${issue.title}" escapeXml="true"/>
        </li>
    </c:forEach>
</ul>
</body>
</html>