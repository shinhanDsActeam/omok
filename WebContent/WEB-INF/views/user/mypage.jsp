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
        <div class="overlay-text">
            ${loginUser.nickname} <br>
            ${info.totalCount}전 / ${info.winCount}승 / ${info.drawCount}무 / ${info.loseCount}패
        </div>
    </div>

    <jsp:include page="../slidebar.jsp" />
</div>

<c:if test="${not empty param.extraModal}">
    <jsp:include page="${param.extraModal}" />
</c:if>
</body>
</html>