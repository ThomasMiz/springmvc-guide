<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
  <link href="/css/main.css" rel="stylesheet"/>
</head>
<body>
<h2>Hello <c:out value="${user.email}" escapeXml="true"/>'s profile!</h2>
</body>
</html>