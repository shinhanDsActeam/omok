<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>

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

<!-- 페이징 -->
<c:if test="${paging.totalPages > 1}">
    <div class="pagination">
        <!-- 이전 페이지 링크 -->
        <c:if test="${paging.currentpage > 1}">
            <a href="lobby?page=${paging.currentpage - 1}" class="page-link">« 이전</a>
        </c:if>
        <c:forEach var="i" begin="${paging.startPage}" end="${paging.endPage}">
            <a href="lobby?page=${i}" class="page-link ${i == paging.currentpage ? 'active' : ''}">
                ${i}
            </a>
        </c:forEach>
        <!-- 다음 페이지 링크 -->
        <c:if test="${paging.currentpage < paging.totalPages}">
            <a href="lobby?page=${paging.currentpage + 1}" class="page-link">다음 »</a>
        </c:if>
    </div>
</c:if>
<style>
    .pagination {
        margin-top: 20px;
        text-align: center;
    }
    .pagination a {
        margin: 0 5px;
        padding: 5px 10px;
        background-color: #eee;
        border: 1px solid #ccc;
        text-decoration: none;
        color: #333;
    }
    .pagination .current {
        margin: 0 5px;
        padding: 5px 10px;
        background-color: #333;
        color: #fff;
        border: 1px solid #333;
    }
</style>