<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
    <link href="/css/main.css" rel="stylesheet"/>
</head>
<body>
<h2>Hello <c:out value="${user.email}" escapeXml="true"/>!</h2>
<!-- Agarra el objeto "user" y llama el getter de email, predice que este se llama getEmail() -->
</body>
</html>