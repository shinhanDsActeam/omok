<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>

<h1>5~ 빈틈없이</h1>

<div class="title-wrapper">
    <h2 class="section-title">참여 가능한 방</h2>
    <div class="right-button">
        <button class="button" id="createRoomBtn">방 만들기</button>
    </div>
</div>

<table class="room-list">
    <thead>
    <tr>
        <th>삭제</th>
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
                <td colspan="5">현재 생성된 방이 없습니다.</td>
            </tr>
        </c:when>
        <c:otherwise>
            <c:forEach var="room" items="${roomList}">
                <tr>
                    <td>
                        <c:if test="${room.status != '게임중'}">
                            <button class="delete-btn" data-room-id="${room.id}" title="방 삭제">
                            </button>
                        </c:if>
                    </td>
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
<div class="pagination-wrapper">
    <c:if test="${paging.totalPages > 1}">
        <!-- 이전 페이지 링크 -->
        <c:choose>
            <c:when test="${paging.currentpage > 1}">
                <a href="lobby?page=${paging.currentpage - 1}" class="page-nav-btn glow-hover">&lt;</a>
            </c:when>
            <c:otherwise>
                <span class="page-nav-btn disabled-btn">&lt;</span>
            </c:otherwise>
        </c:choose>
        <div class="page-center">
            <c:forEach var="i" begin="${paging.startPage}" end="${paging.endPage}">
                <a href="lobby?page=${i}" class="page-link ${i == paging.currentpage ? 'active' : ''}">
                    ${i}
                </a>
            </c:forEach>
        </div>
        <!-- 다음 페이지 링크 -->
        <c:choose>
            <c:when test="${paging.currentpage < paging.totalPages}">
                <a href="lobby?page=${paging.currentpage + 1}" class="page-nav-btn glow-hover">></a>
            </c:when>
            <c:otherwise>
                <span class="page-nav-btn disabled-btn">&gt;</span>
            </c:otherwise>
        </c:choose>
    </c:if>
</div>