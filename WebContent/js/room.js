document.addEventListener('DOMContentLoaded', function() {
    // 방 만들기 모달 관련 요소
    const createRoomBtn = document.getElementById('createRoomBtn');
    const createRoomModal = document.getElementById('createRoomModal');
    const closeModalBtn = document.querySelector('.close');

    let currentPage = 1;

    // URL에서 현재 페이지 파라미터 읽기
    const urlParams = new URLSearchParams(window.location.search);
    const pageParam = urlParams.get('page');
    if (pageParam) {
        currentPage = parseInt(pageParam);
    }

    // 방 만들기 버튼 클릭 시 모달 표시
    if (createRoomBtn) {
        createRoomBtn.addEventListener('click', function() {
            createRoomModal.style.display = 'block';
        });
    }

    // 모달 닫기 버튼
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', function() {
            createRoomModal.style.display = 'none';
        });
    }

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(event) {
        if (event.target == createRoomModal) {
            createRoomModal.style.display = 'none';
        }
    });

    // 초기 삭제 버튼 이벤트 바인딩
    attachDeleteListeners();
    attachPaginationListeners();

    // 페이지 로드 시 자동 갱신 시작 (현재 페이지 유지)
    refreshRoomList();
    setInterval(refreshRoomList, 3000);  // 3초마다 리스트 갱신

    // 방 목록 자동 갱신
    function refreshRoomList(page = currentPage) {
        console.log('방 목록 갱신 시작 - 페이지:', page);

        const xhr = new XMLHttpRequest();
        // URL을 lobby로 통일
        xhr.open('GET', `lobby?page=${page}`, true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                console.log('방 목록 갱신 성공');

                const parser = new DOMParser();
                const doc = parser.parseFromString(xhr.responseText, 'text/html');

                // tbody 업데이트
                const newTbody = doc.querySelector('tbody');
                const roomListTable = document.querySelector('.room-list');

                if (roomListTable) {
                    const oldTbody = roomListTable.querySelector('tbody');

                    if (newTbody && oldTbody) {
                        oldTbody.replaceWith(newTbody);
                        console.log('테이블 업데이트 완료');

                        // 이벤트 리스너 재바인딩
                        attachDeleteListeners();
                    }
                }

                // 페이징 정보도 업데이트
                const newPagination = doc.querySelector('.pagination-wrapper');
                const oldPagination = document.querySelector('.pagination-wrapper');

                if (newPagination && oldPagination) {
                    oldPagination.replaceWith(newPagination);
                    attachPaginationListeners();
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
        const pageLinks = document.querySelectorAll('.page-link, .page-nav-btn');
        console.log('페이지 링크 바인딩:', pageLinks.length + '개');

        pageLinks.forEach(link => {
            // 기존 이벤트 리스너 제거 후 새로 추가
            const newLink = link.cloneNode(true);
            link.parentNode.replaceChild(newLink, link);

            newLink.addEventListener('click', function (event) {
                event.preventDefault();
                const href = this.getAttribute('href');
                if (href && href.includes('page=')) {
                    const page = parseInt(href.split('page=')[1]);
                    if (!isNaN(page)) {
                        currentPage = page;
                        console.log('페이지 변경:', currentPage);

                        // URL 변경 (브라우저 히스토리에 추가)
                        const newUrl = `${window.location.pathname}?page=${page}`;
                        window.history.pushState({page: page}, '', newUrl);

                        // 방 목록 갱신
                        refreshRoomList(currentPage);
                    }
                }
            });
        });
    }

    // 브라우저 뒤로가기/앞으로가기 처리
    window.addEventListener('popstate', function(event) {
        if (event.state && event.state.page) {
            currentPage = event.state.page;
        } else {
            // URL에서 페이지 파라미터 다시 읽기
            const urlParams = new URLSearchParams(window.location.search);
            const pageParam = urlParams.get('page');
            currentPage = pageParam ? parseInt(pageParam) : 1;
        }
        refreshRoomList(currentPage);
    });

    // 삭제 버튼 클릭 이벤트 바인딩
    function attachDeleteListeners() {
        const deleteButtons = document.querySelectorAll('.delete-btn');
        console.log('삭제 버튼 바인딩:', deleteButtons.length + '개 발견');

        deleteButtons.forEach((button, index) => {
            console.log('삭제 버튼', index + 1, ':', button, 'roomId:', button.dataset.roomId);

            // 기존 이벤트 리스너 제거 후 새로 추가
            const newButton = button.cloneNode(true);
            button.parentNode.replaceChild(newButton, button);

            newButton.addEventListener('click', function(event) {
                event.preventDefault();
                event.stopPropagation();

                const roomId = this.dataset.roomId;
                console.log('삭제 버튼 클릭됨! 방 ID:', roomId);

                // 삭제 확인
                if (confirm(`방 ${roomId}을(를) 정말 삭제하시겠습니까?`)) {
                    deleteRoom(roomId);
                } else {
                    console.log('삭제 취소됨');
                }
            });
        });
    }

    // 방 삭제 함수
    function deleteRoom(roomId) {
        console.log('방 삭제 요청:', roomId);

        const xhr = new XMLHttpRequest();
        xhr.open('POST', 'deleteRoom', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

        xhr.onload = function() {
            console.log('삭제 응답 상태:', xhr.status);
            console.log('삭제 응답 내용:', xhr.responseText);

            if (xhr.status === 200) {
                console.log('방 삭제 성공');
                alert('방이 삭제되었습니다.');
                // 방 목록 새로고침 (현재 페이지 유지)
                refreshRoomList(currentPage);
            } else {
                console.error('방 삭제 실패:', xhr.status, xhr.responseText);
                alert('방 삭제에 실패했습니다.');
            }
        };

        xhr.onerror = function() {
            console.error('방 삭제 요청 실패');
            alert('서버 연결에 실패했습니다.');
        };

        xhr.send(`roomId=${roomId}`);
    }
});