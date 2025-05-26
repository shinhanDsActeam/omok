document.addEventListener('DOMContentLoaded', function() {
    // 방 만들기 모달 관련 요소
    const createRoomBtn = document.getElementById('createRoomBtn');
    const createRoomModal = document.getElementById('createRoomModal');
    const closeModalBtn = document.querySelector('.close');



    window.onload = function() {
        refreshRoomList();
        setInterval(refreshRoomList, 3000);  // 3초마다 리스트 갱신
    };

    let currentPage = 1;

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
    function refreshRoomList(page = 1) {
        // AJAX를 사용하여 방 목록 갱신 (JSP 조각을 가져와서 업데이트)
        const xhr = new XMLHttpRequest();
        xhr.open('GET', `getRoomList?page=${page}`, true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                const parser = new DOMParser();
                const doc = parser.parseFromString(xhr.responseText, 'text/html');

                const newTbody = doc.querySelector('tbody');
                const roomListTable = document.querySelector('.room-list');
                const oldTbody = roomListTable.querySelector('tbody');

                if (newTbody && oldTbody) {
                    oldTbody.replaceWith(newTbody);
                    attachPaginationListeners(); // 새 링크 바인딩
                }
            } else {
                console.error('방 목록 불러오기 실패:', xhr.status);
            }
        };
        xhr.onerror = function () {
            console.error('AJAX 요청 실패');
        };
        xhr.send();
    }

    // 페이지 링크 클릭 이벤트 바인딩
    function attachPaginationListeners() {
        const pageLinks = document.querySelectorAll('.pagination-link');
        pageLinks.forEach(link => {
            link.addEventListener('click', function (event) {
                event.preventDefault();
                const page = parseInt(this.dataset.page);
                if (!isNaN(page)) {
                    currentPage = page;
                    refreshRoomList(currentPage);
                }
            });
        });
    }
});