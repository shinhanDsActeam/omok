<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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