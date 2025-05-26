<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<h2>랭킹</h2>
<c:choose>
    <c:when test="${empty rankingList}">
        <div class="empty-message">
            아직 데이터가 없습니다 🐣<br>
            첫 게임을 플레이하여 채워주세요!
        </div>
    </c:when>
    <c:otherwise>
        <table class="record-table">
            <thead>
            <tr>
                <th>순위</th>
                <th>닉네임</th>
                <th>전적</th>
                <th>승률</th>
                <th>총점</th>
            </tr>
            </thead>
            <tbody>
            <c:set var="maxRows" value="10" />
            <c:set var="actualSize" value="${fn:length(rankingList)}" />

            <c:forEach var="info" items="${rankingList}">
                <tr style="${info.totalCount == 0 ? 'opacity: 0.2;' : ''}">
                    <td>${info.rank}</td>
                    <td><strong>${info.nickname}</strong></td>
                    <td>
                        <span style="color: gray;">${info.totalCount}전 / </span>
                        <span style="color: black;">${info.winCount}승</span>
                        <span style="color: gray;"> / ${info.drawCount}무 / </span>
                        <span style="color: gray;">${info.loseCount}패</span>
                    </td>
                    <td>${info.winRate}</td>
                    <td>${info.score}</td>
                </tr>
            </c:forEach>

            <!-- 부족한 줄 채우기 -->
            <c:forEach var="i" begin="1" end="${maxRows - actualSize}">
                <tr class="empty-row">
                    <td colspan="5">&nbsp;</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <div class="pagination-wrapper">
            <c:choose>
                <c:when test="${currentPage > 1}">
                    <a href="?page=${currentPage - 1}" class="page-nav-btn glow-hover">&lt;</a>
                </c:when>
                <c:otherwise>
                    <span class="page-nav-btn disabled-btn">&lt;</span>
                </c:otherwise>
            </c:choose>

            <div class="page-center">
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <a href="?page=${i}" class="page-link ${i == currentPage ? 'active' : ''}">${i}</a>
                </c:forEach>
            </div>

            <c:choose>
                <c:when test="${currentPage < totalPages}">
                    <a href="?page=${currentPage + 1}" class="page-nav-btn glow-hover">&gt;</a>
                </c:when>
                <c:otherwise>
                    <span class="page-nav-btn disabled-btn">&gt;</span>
                </c:otherwise>
            </c:choose>
        </div>
    </c:otherwise>
</c:choose>