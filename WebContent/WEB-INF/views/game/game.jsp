<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="main.java.domain.Member" %>
<%
    Member loginUser = (Member) session.getAttribute("loginUser");
    String loginId = loginUser != null ? loginUser.getUsername() : "";
    String nickname = loginUser != null ? loginUser.getNickname() : "";
    String roomId = request.getParameter("roomId");
    boolean isHost = "true".equals(request.getParameter("host"));
%>
<!DOCTYPE html>
<html>
<head>
    <title>오목 게임</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/game.css">
</head>
<body>

<!-- 배경 -->
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

<!-- 게임 전체 UI -->
<div class="game-wrapper">

    <!-- 좌측: 흑돌 유저 정보 -->
    <div class="player-info black-player">
        <h3>흑돌 (방장)</h3>
        <p id="host-nickname"><%= isHost ? nickname : "" %></p>
        <div class="time-limit">30초</div>
    </div>

    <!-- 중앙: 오목판 -->
    <div class="container">
        <h1 id="game-title">5~ 빈틈없이</h1>
        <div class="board-container">
            <div id="board"></div>
        </div>
        <div id="game-info">
            <div id="status-message">게임을 시작합니다!</div>
            <div id="turn-indicator" class="black">흑돌 차례입니다</div>
        </div>
        <!-- <%-- <button id="restart-btn">게임 재시작</button> --%> -->
        <button id="surrender-btn">기권하기</button>
        <button id="start-btn" style="display:none;">게임 시작하기</button>
    </div>

    <!-- 우측: 백돌 유저 정보 + 채팅 -->
    <div class="right-panel">
        <div class="player-info white-player">
            <h3>백돌 (참가자)</h3>
            <p id="guest-nickname"><%= !isHost ? nickname : "" %></p>
            <div class="time-limit">30초</div>
        </div>

        <div class="chat-box">
            <div class="chat-log" id="chat-log"></div>
            <div class="chat-input-area">
                <input type="text" id="chat-input" placeholder="메시지 입력..." />
                <button id="chat-send">전송</button>
            </div>
        </div>
    </div>
</div>

<!-- 승리 메시지 -->
<div class="win-overlay">
    <div class="win-content">
        <div class="win-message"></div>
        <div class="martial-message"></div>

        <div id="game-end-buttons" style="display:none;">
            <button id = "rematch" class="end-btn" onclick="requestRematch()">재대결</button>
            <button class="end-btn" onclick="leaveRoom()">방 떠나기</button>
        </div>
    </div>
</div>

<!-- 인트로 (방장 전용 메시지) -->
<div class="intro-overlay">
    <div class="intro-title">5 ~ 빈틈없이</div>
    <div class="intro-text">무림 최고수의 지혜와 기법으로 오행의 비전을 완성하라!</div>
    <% if (isHost) { %>
        <div id="status-message">⏳ 참가자 기다리는 중...</div>
        <button id="intro-start-btn" style="display:none;">게임 시작</button>
    <% } %>
</div>

<!-- 사용자 정보 및 JS 변수 전달 -->
<script>
    const loginId = '<%= loginId %>';
    const nickname = '<%= nickname %>';
    const roomId = '<%= roomId %>';
    const isHost = <%= isHost %>;
</script>
<!-- 돌 놓기 사운드 -->
<audio id="stone-sound" src="<%= request.getContextPath() %>/sounds/stone.wav" preload="auto"></audio>
<!-- 게임 종료 사운드 -->
<audio id="end-sound" src="<%= request.getContextPath() %>/sounds/game_end.wav" preload="auto"></audio>

<script src="<%= request.getContextPath() %>/js/game.js"></script>
</body>
</html>
