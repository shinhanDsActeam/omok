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
</head>
<body>
<div class="login-wrapper">
    <h1>로그인</h1>
    <form id="loginForm" action="/login" method="post" class="container">
        <div>
            <input type="text" id="username" name="username" placeholder="아이디"><br>
            <input type="password" id="password" name="password" placeholder="비밀번호"><br>
            <input type="button" id="joinBtn" value="회원가입">
            <input type="button" id="loginBtn" value="로그인">
        </div>
    </form>
</div>
<script src="<%= request.getContextPath() %>/js/login.js"></script>
</body>
</html>