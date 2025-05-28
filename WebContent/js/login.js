document.addEventListener('DOMContentLoaded', function () {
    const loginBtn = document.getElementById('loginBtn');
    const joinBtn = document.getElementById('joinBtn');

    document.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            loginBtn.click();
        }
    });


   // joinBtn 클릭 시 /join 페이지로 이동
   joinBtn.addEventListener('click', function () {
       const path = typeof contextPath !== 'undefined' ? contextPath : '';
       window.location.href = `${path}/join`;
   });

    loginBtn.addEventListener('click', function () {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        if (!username || !password) {
            alert("아이디와 비밀번호를 모두 입력하세요.");
            return;
        }

        // ✅ JSP에서 전역 변수로 넘긴 contextPath 사용
        const path = typeof contextPath !== 'undefined' ? contextPath : '';

        fetch(`${path}/login`, {
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
                // alert("로그인 성공!");
                window.location.href = `${path}/lobby`;
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