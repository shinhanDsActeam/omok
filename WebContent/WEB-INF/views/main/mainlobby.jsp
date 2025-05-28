<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/mainlobby.css">

<div class="main-section">
    <img src="<%= request.getContextPath() %>/image/mainlobby.gif" alt="배경 애니메이션" class="bg-video">


    <button class="main-button" onclick="location.href='<%= request.getContextPath() %>/lobby'">게임 시작</button>
 </div>