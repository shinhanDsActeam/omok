<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<link href="https://fonts.googleapis.com/css2?family=Playwrite+DK+Loopet:wght@100..400&display=swap" rel="stylesheet">

<button id="profileBtn">
    <img class="img" src="${pageContext.request.contextPath}/image/profile.png" alt="프로필">
</button>

<div id="slidebar" class="slidebar">
    <button id="closeBtn" class="close-btn">✖</button>
    <div class="profile-header">
        <div id="profileImg">
            <img class="img" src="${pageContext.request.contextPath}/image/profile.png" alt="프로필">
        </div>
        <div class="profile-info">
            <strong>kwiyoon</strong>
        </div>
    </div>
    <ul class="menu-list">
        <li><a href="/omok/mypage">👤 나의 신상기록</a></li>  <%-- 마이페이지 --%>
        <li><a href="/omok/mypage">⚙️ 나의 전적</a></li>      <%-- 전적 --%>
        <li><a href="#">️✉️ 나의 서찰함</a></li>    <%-- 알림 --%>
        <hr>
        <li><a href="/omok/logout">🚪 귀가하기</a></li>       <%-- 로그아웃 --%>
    </ul>
</div>