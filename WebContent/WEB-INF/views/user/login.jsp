<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/login.css">
    <script>
        const contextPath = '<%= request.getContextPath() %>';
    </script>
    <script src="<%= request.getContextPath() %>/js/login.js"></script>
</head>
<body>
<h1>로그인</h1>
<form id="loginForm" action="/login" method="post">
    <div>
        <input type="text" id="username" name="username" placeholder="아이디">
    </div>
    <div>
        <input type="password" id="pw" name="pw" placeholder="비밀번호">
    </div>
    <div>
        <input type="button" id="loginBtn" value="로그인">
    </div>
</form>
</body>