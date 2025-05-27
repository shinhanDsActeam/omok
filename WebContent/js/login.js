document.addEventListener('DOMContentLoaded', function () {
    const loginBtn = document.getElementById('loginBtn');

    loginBtn.addEventListener('click', function () {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        if (!username || !password) {
            alert("아이디와 비밀번호를 모두 입력하세요.");
            return;
        }

        // ✅ contextPath가 정의되지 않았다면 기본값 사용
        const contextPath = window.contextPath || '';

        fetch(`${contextPath}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
        })
        .then(response => {
            if (!response.ok) throw new Error("서버 응답 오류");
            return response.json();
        })
        .then(data => {
            if (data.success) {
                alert("로그인 성공!");

                // ✅ WebSocket 연결이나 추가 확인 로직 넣고 싶다면 여기서 처리 가능

                // ✅ 로그인 후 이동
                window.location.href = `${contextPath}/lobby`;
            } else {
                alert("아이디 또는 비밀번호가 잘못되었습니다.");
            }
        })
        .catch(err => {
            console.error("로그인 요청 실패:", err);
            alert("서버 오류가 발생했습니다.");
        });
    });
});
