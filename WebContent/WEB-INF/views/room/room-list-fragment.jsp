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

<!-- 페이지 네비게이션 -->
<c:if test="${paging.totalPages > 1}">
    <div class="pagination">
        <!-- 이전 페이지 링크 -->
        <c:if test="${paging.currentpage > 1}">
            <a href="lobby?page=${paging.currentpage - 1}" class="page-link">« 이전</a>
        </c:if>

        <!-- 페이지 번호 목록 -->
        <c:forEach var="i" begin="${paging.startPage}" end="${paging.endPage}">
            <c:choose>
                <c:when test="${i == paging.currentpage}">
                    <span class="current">${i}</span>
                </c:when>
                <c:otherwise>
                    <a href="lobby?page=${i}">${i}</a>
                </c:otherwise>
            </c:choose>
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