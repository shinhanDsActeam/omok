document.addEventListener('DOMContentLoaded', function() {
    const checkIdBtn = document.getElementById('checkIdBtn');
    const checkNicknameBtn = document.getElementById('checkNicknameBtn');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');





// 버튼 초기 클래스 설정
  [checkIdBtn, checkNicknameBtn].forEach((btn) => {
    if (!btn) return;
    if (btn.value === "중복체크") {
      btn.classList.add("checking");
    } else if (btn.value === "확인완료") {
      btn.classList.add("checked");
    }
  });

  // 아이디 중복 체크
  if (checkIdBtn) {
    checkIdBtn.addEventListener("click", function () {
      const username = document.getElementById("username").value;
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
            checkIdBtn.value = "확인완료";
            checkIdBtn.classList.remove("checking");
            checkIdBtn.classList.add("checked");
          }
        })
        .catch(err => {
          alert("서버 오류가 발생했습니다.");
          console.error(err);
        });
    });
  }


    // 닉네임 중복 체크
    checkNicknameBtn.addEventListener('click', function () {
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
            checkNicknameBtn.value = "확인완료";
            checkNicknameBtn.classList.remove("checking");
            checkNicknameBtn.classList.add("checked");
          }
        })
        .catch(err => {
          alert("서버 오류가 발생했습니다.");
          console.error(err);
        });
    });
});
