document.addEventListener('DOMContentLoaded', function() {
    const checkIdBtn = document.getElementById('checkIdBtn');
    const checkNicknameBtn = document.getElementById('checkNicknameBtn');
    const passwordInput = document.getElementById('pw');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    // 아이디 중복 체크
    checkIdBtn.addEventListener('click', function() {
        const username = document.getElementById('username').value;
        if (!username) {
            alert("아이디를 입력하세요.");
            return;
        }

        fetch(`${contextPath}/check-username?username=${encodeURIComponent(username)}`)
            .then(response => response.json())
            .then(data => {
                if (data.duplicate) {
                    alert("이미 사용 중인 아이디입니다.");
                } else {
                    alert("사용 가능한 아이디입니다.");
                }
            })
            .catch(err => {
                alert("서버 오류가 발생했습니다.");
                console.error(err);
            });
    });

    // 닉네임 중복 체크
    checkNicknameBtn.addEventListener('click', function() {
        const nickname = document.getElementById('nickname').value;
        if (!nickname) {
            alert("닉네임을 입력하세요.");
            return;
        }

        fetch(`${contextPath}/check-nickname?nickname=${encodeURIComponent(nickname)}`)
            .then(response => response.json())
            .then(data => {
                if (data.duplicate) {
                    alert("이미 사용 중인 닉네임입니다.");
                } else {
                    alert("사용 가능한 닉네임입니다.");
                }
            })
            .catch(err => {
                alert("서버 오류가 발생했습니다.");
                console.error(err);
            });
    });

    // 비밀번호 확인 체크
    confirmPasswordInput.addEventListener('input', function() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        const messageEl = document.getElementById('passwordMessage');

        if (password !== confirmPassword) {
            messageEl.textContent = "비밀번호가 일치하지 않습니다.";
            messageEl.style.color = "red";
        } else {
            messageEl.textContent = "비밀번호가 일치합니다.";
            messageEl.style.color = "green";
        }
    });
});
