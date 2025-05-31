<%-- 홈화면 공통 레이아웃--%>
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
    <title><c:out value="${pageTitle}" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/slidebar.css" />
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
        <div class="card-box">
            <jsp:include page="${cardBox}" />
        </div>
    </div>

    <jsp:include page="slidebar.jsp" />
</div>

<c:if test="${not empty param.extraModal}">
    <jsp:include page="${param.extraModal}" />
</c:if>
</body>
</html>