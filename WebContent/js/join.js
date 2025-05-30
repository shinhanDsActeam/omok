document.addEventListener('DOMContentLoaded', function() {
  const checkIdBtn = document.getElementById('checkIdBtn');
  const checkNicknameBtn = document.getElementById('checkNicknameBtn');
  const passwordInput = document.getElementById('password');
  const confirmPasswordInput = document.getElementById('confirmPassword');
  const usernameInput = document.getElementById('username');
  const nicknameInput = document.getElementById('nickname');
  const submitBtn = document.querySelector('input[type="submit"]');
  const passwordMessage = document.getElementById('passwordMessage');

  function checkFormValid() {
    const idChecked = checkIdBtn.classList.contains('checked');
    const nicknameChecked = checkNicknameBtn.classList.contains('checked');
    const pwMatch = passwordInput.value && (passwordInput.value === confirmPasswordInput.value);

    if (idChecked && nicknameChecked && pwMatch) {
      submitBtn.disabled = false;
      submitBtn.classList.remove('disabled');
    } else {
      submitBtn.disabled = true;
      submitBtn.classList.add('disabled');
    }
  }

  function updatePasswordIcon() {
    if (passwordInput.value && passwordInput.value === confirmPasswordInput.value) {
      passwordMessage.textContent = 'O';
      passwordMessage.style.color = 'green';
    } else {
      passwordMessage.textContent = '✗';
      passwordMessage.style.color = 'red';
    }

    passwordMessage.style.paddingLeft = '15px';
    passwordMessage.style.fontSize = '25px';
  }
// 페이지 로드시 초기 상태 체크
  checkFormValid();
  updatePasswordIcon();

// 버튼 초기 클래스 설정
  [checkIdBtn, checkNicknameBtn].forEach((btn) => {
    if (!btn) return;
    if (btn.value === "중복체크") {
      btn.classList.add("checking");
    } else if (btn.value === "확인완료") {
      btn.classList.add("checked");
    }
    checkFormValid();
  });

  // 비밀번호 입력 시마다 체크
  [passwordInput, confirmPasswordInput].forEach(input => {
    input.addEventListener('input', ()=>{
      checkFormValid();
      updatePasswordIcon();
    });
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
            checkFormValid();
          }
        })
        .catch(err => {
          alert("서버 오류가 발생했습니다.");
          console.error(err);
        });
    });
  }

  // username 입력값이 바뀌면 중복확인 버튼 상태 초기화
  usernameInput.addEventListener('input', function() {
    checkIdBtn.value = "중복확인";
    checkIdBtn.classList.remove("checked");
    checkIdBtn.classList.add("checking");
    checkFormValid();
  });

  // username 입력값이 바뀌면 중복확인 버튼 상태 초기화
  nicknameInput.addEventListener('input', function() {
    checkNicknameBtn.value = "중복확인";
    checkNicknameBtn.classList.remove("checked");
    checkNicknameBtn.classList.add("checking");
    checkFormValid();
  });

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
          checkFormValid();
        }
      })
      .catch(err => {
        alert("서버 오류가 발생했습니다.");
        console.error(err);
      });
  });
});
