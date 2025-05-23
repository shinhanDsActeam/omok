<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>5~ 빈틈없이 : 로비</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/room.css">
    <script src="<%= request.getContextPath() %>/js/room.js"></script>
</head>
<body>
    <div class="wrapper">
        <%@ include file="../sidebar.jspf" %>

        <div class="container">
            <div class="card-box">
                <h1>5~ 빈틈없이</h1>

                <div class="button-container">
                    <button class="button" id="createRoomBtn">방 만들기</button>
                </div>

                <h2>참여 가능한 방</h2>
                <table class="room-list">
                    <thead>
                        <tr>
                            <th>방 번호</th>
                            <th>방 이름</th>
                            <th>상태</th>
                            <th>액션</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty roomList}">
                                <tr>
                                    <td colspan="4">현재 생성된 방이 없습니다.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="room" items="${roomList}">
                                    <tr>
                                        <td>${room.id}</td>
                                        <td>${room.name}</td>
                                        <td>${room.status}</td>
                                        <td>
                                            <c:if test="${room.status == '대기중'}">
                                                <form action="joinRoom" method="post">
                                                    <input type="hidden" name="roomId" value="${room.id}">
                                                    <button type="submit" class="join-btn">참여</button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>


    <!-- 방 만들기 모달 -->
    <div id="createRoomModal" class="modal">
        <div class="modal-content">
            <span class="close">&times;</span>
            <h2>새 방 만들기</h2>
            <form action="createRoom" method="post">
                <div class="form-group">
                    <label for="roomName">방 이름:</label>
                    <input type="text" id="roomName" name="roomName" required>
                </div>
                <button type="submit" class="button">방 만들기</button>
            </form>
        </div>
    </div>
</body>
</html>
