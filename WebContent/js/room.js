document.addEventListener('DOMContentLoaded', function() {
    // 방 만들기 모달 관련 요소
    const createRoomBtn = document.getElementById('createRoomBtn');
    const createRoomModal = document.getElementById('createRoomModal');
    const closeModalBtn = document.querySelector('.close');

    // 방 만들기 버튼 클릭 시 모달 표시
    createRoomBtn.addEventListener('click', function() {
        createRoomModal.style.display = 'block';
    });

    // 모달 닫기 버튼
    closeModalBtn.addEventListener('click', function() {
        createRoomModal.style.display = 'none';
    });

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(event) {
        if (event.target == createRoomModal) {
            createRoomModal.style.display = 'none';
        }
    });

    // 방 목록 자동 갱신 (5초마다)
    function refreshRoomList() {
        // AJAX를 사용하여 방 목록 갱신 (JSP 조각을 가져와서 업데이트)
        const xhr = new XMLHttpRequest();
        xhr.open('GET', 'getRoomList', true);
        xhr.onload = function() {
            if (xhr.status === 200) {
                const roomListBody = document.querySelector('.room-list tbody');
                if (roomListBody) {
                    roomListBody.innerHTML = xhr.responseText;
                }
            }
        };
        xhr.onerror = function() {
            console.error('방 목록 갱신 요청 실패');
        };
        xhr.send();
    }
});