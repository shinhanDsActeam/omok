<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<h1>최근 전적</h1>
<c:choose>
    <c:when test="${empty historyList}">
        <div class="empty-message">
            아직 치른 승부가 없습니다.<br>
            첫 대국에 나서 역사를 기록해주세요.
        </div>
    </c:when>
    <c:otherwise>
        <table class="record-table">
            <thead>
            <tr>
                <th>결과</th>
                <th>결전 상대</th>
                <th>결전 일시</th>
            </tr>
            </thead>
            <tbody>
            <c:set var="maxRows" value="${pageSize}" />
            <c:set var="actualSize" value="${fn:length(historyList)}" />

            <c:forEach var="match" items="${historyList}">
                <tr class="${fn:toUpperCase(fn:trim(match.result)) == 'WIN' ? 'win-row' :
                                                fn:toUpperCase(fn:trim(match.result)) == 'LOSE' ? 'lose-row' : 'draw-row'}">
                    <td><strong>${fn:toUpperCase(fn:trim(match.result)) == 'WIN' ? '승' :
                            fn:toUpperCase(fn:trim(match.result)) == 'LOSE' ? '패' : '무'}
                    </strong></td>
                    <td>${match.opponent}</td>
                    <td>${match.matchDate}</td>
                </tr>
            </c:forEach>

            <!-- 부족한 줄 채우기 -->
            <c:forEach var="i" begin="1" end="${maxRows - actualSize}">
                <tr class="empty-row">
                    <td colspan="3">&nbsp;</td>
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