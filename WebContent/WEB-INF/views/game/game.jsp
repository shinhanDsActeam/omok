<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>오목 게임</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/game.css">
</head>
<body>
<div class="mountain-bg">
    <div class="mountain"></div>
    <div class="mountain"></div>
    <div class="mountain"></div>
</div>

<div class="trees">
    <div class="tree"></div>
    <div class="tree"></div>
    <div class="tree"></div>
    <div class="tree"></div>
</div>

<div class="container">
    <h1 id="game-title">5~ 빈틈없이</h1>
    <div class="board-container">
        <div id="board"></div>
    </div>
    <div id="game-info">
        <div id="status-message">게임을 시작합니다!</div>
        <div id="turn-indicator" class="black">흑돌 차례입니다</div>
    </div>
    <button id="restart-btn">게임 재시작</button>
</div>

<div class="win-overlay">
    <div class="win-content">
        <div class="win-message"></div>
        <div class="martial-message"></div>
        <button id="close-win">확인</button>
    </div>
</div>

<div class="intro-overlay">
    <div class="intro-title">5 ~ 빈틈없이</div>
    <div class="intro-text">무림 최고수의 지혜와 기법으로 오행의 비전을 완성하라!</div>
    <button class="start-btn">게임 시작</button>
</div>
<script src="<%= request.getContextPath() %>/js/game.js"></script>
<%--<script src="script.js"></script>--%>
</body>
</html>