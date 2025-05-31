<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setCharacterEncoding("utf-8");
    String pageTitleParam = request.getParameter("pageTitle");
    request.setAttribute("pageTitle", pageTitleParam);

    String cardBox = request.getParameter("cardBox");
    request.setAttribute("cardBox", cardBox);
%>
<html>
<head>
    <meta charset="UTF-8">
    <title>마이페이지</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/slidebar.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mypage.css" />
    <script src="${pageContext.request.contextPath}/js/slidebar.js"></script>
    <script src="${pageContext.request.contextPath}/js/mypage.js"></script>
    <c:if test="${not empty param.extraCss}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}${param.extraCss}" />
    </c:if>
    <c:if test="${not empty param.extraJs}">
        <script src="${pageContext.request.contextPath}${param.extraJs}"></script>
    </c:if>
</head>
<body>
<div class="wrapper">
    <div class="container">
        <%
            double winRate = (double) request.getAttribute("winRate");
        %>
        <div class="flip-outer">
            <div class="flip-inner">
                <div class="overlay-text front">
                    <div class="vertical-text">${loginUser.nickname}</div>
                    <div class="vertical-text">
                        ${info.totalCount}전 <br> ${info.winCount}승 <br> ${info.loseCount}패
                    </div>
                </div>
                <div class="back">
                </div>
            </div>
        </div>


    </div>

    <jsp:include page="../slidebar.jsp" />
</div>

<c:if test="${not empty param.extraModal}">
    <jsp:include page="${param.extraModal}" />
</c:if>


<!-- JS 변수 전달 -->
<script>
    const winRate = '<%= winRate %>';
</script>
</body>
</html>