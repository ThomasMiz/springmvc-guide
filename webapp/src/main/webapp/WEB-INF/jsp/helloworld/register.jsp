<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<body>
<h2>Register new user</h2>
<form action="<c:url value="/register"/>" method="post">
    <div>
        <label>Email: <input type="text" name="email"></label>
    </div>
    <div>
        <label>Password: <input type="password" placeholder="Password" name="password"></label>
    </div>
    <div>
        <label>Repeat password: <input type="password" placeholder="Password" name="repeat_password"></label>
    </div>
    <div>
        <input type="submit" value="Register">
    </div>
</form>
</body>
</html>