<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/join.css">
</head>
<body>
<h1>회원가입</h1>
<form action="<%= request.getContextPath() %>/join" method="post" class="container">
    <div>
        <input type="text" id="username" name="username" placeholder="아이디">
        <input type="button" id="checkIdBtn" value="중복체크"><br>
        <input type="password" id="password" name="password" placeholder="비밀번호"><br>
        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="비밀번호 확인">
        <span id="passwordMessage" class="passwordMessage"></span><br>
        <input type="text" id="nickname" name="nickname" placeholder="닉네임">
        <input type="button" id="checkNicknameBtn" value="중복체크"><br>
    </div>
    <div>
        <input type="submit" value="가입하기">
    </div>
</form>
<script>
      const contextPath = '<%= request.getContextPath() %>';
</script>
<script src="<%= request.getContextPath() %>/js/join.js"></script>
</body>
</html>
