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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css" />
    <c:if test="${not empty param.extraCss}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}${param.extraCss}" />
    </c:if>
</head>
<body>
<div class="wrapper">
    <jsp:include page="sidebar.jspf" />

    <div class="container">
        <div class="card-box">
            <jsp:include page="${cardBox}" />
        </div>
    </div>
</div>
</body>
</html>