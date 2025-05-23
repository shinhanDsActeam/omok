document.addEventListener('DOMContentLoaded', function () {
    const boardSize = 15;
    const board = document.getElementById('board');
    const statusMessage = document.getElementById('status-message');
    const turnIndicator = document.getElementById('turn-indicator');
    const restartBtn = document.getElementById('restart-btn');
    const winOverlay = document.querySelector('.win-overlay');
    const winMessage = document.querySelector('.win-message');
    const martialMessage = document.querySelector('.martial-message');
    const closeWinBtn = document.getElementById('close-win');
    const introOverlay = document.querySelector('.intro-overlay');
    const introTitle = document.querySelector('.intro-title');
    const introText = document.querySelector('.intro-text');
    const startBtn = document.querySelector('.start-btn');
    const boardContainer = document.querySelector('.board-container');
    const roomId = new URLSearchParams(window.location.search).get('roomId');
    const nickname = '임시 닉네임';
    const contextPath = location.pathname.split('/')[1] ? '/' + location.pathname.split('/')[1] : '';

    let gameBoard = Array(boardSize).fill().map(() => Array(boardSize).fill(null));
    let currentPlayer = 'black';
    let gameEnded = false;

    // ✅ WebSocket 연결
    const socket = new WebSocket("ws://" + location.host + location.pathname.replace(/\/[^\/]*$/, '') + "/ws/omok");
    const chatSocket = new WebSocket(`ws://${location.host}${contextPath}/ws/chat/${roomId}`);


    socket.onopen = () => console.log("WebSocket 연결됨");
    chatSocket.onopen =() => console.log('chatSocket 연결완')

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);

        const row = data.row;
        const col = data.col;
        const stone = data.stone;

        if (gameBoard[row][col] === null && !gameEnded) {
            const cell = board.querySelector(`[data-row='${row}'][data-col='${col}']`);
            placeStone(cell, stone);
            gameBoard[row][col] = stone;

            if (data.gameOver) {
                gameEnded = true;
                winMessage.textContent = data.message;
                martialMessage.textContent = '천하무적 승리의 순간!';
                winOverlay.style.opacity = '1';
                winOverlay.style.pointerEvents = 'auto';
            } else {
                currentPlayer = stone === 'black' ? 'white' : 'black';
                updateGameInfo();
            }
        }
    };

    //채팅 메시지 수신처리
    chatSocket.onmessage = function(event) {
        const data = JSON.parse(event.data);
        const chatLog = document.getElementById('chat-log');
        const msg = document.createElement('div');
        msg.innerHTML = `<b>${data.sender}:</b> ${data.message}`;
        chatLog.appendChild(msg);
        chatLog.scrollTop = chatLog.scrollHeight;
    };

    document.getElementById('chat-send').onclick = function() {
        const input = document.getElementById('chat-input');
        if (input.value.trim()) {
            chatSocket.send(JSON.stringify({
                sender: nickname,
                message: input.value
            }));
            input.value = '';
        }
    };
    // 엔터 키로도 채팅 전송 가능하게 추가
    document.getElementById('chat-input').addEventListener('keydown', function (event) {
        if (event.key === 'Enter' || event.keyCode === 13) {
            const input = event.target;
            if (input.value.trim()) {
                chatSocket.send(JSON.stringify({
                    sender: nickname,
                    message: input.value
                }));
                input.value = '';
            }
        }
    });

    // 인트로 애니메이션
    setTimeout(() => introTitle.style.animation = 'title-appear 1.2s forwards', 500);
    setTimeout(() => {
        introText.style.transition = 'all 1s ease';
        introText.style.opacity = '1';
        introText.style.transform = 'translateY(0)';
    }, 1700);
    setTimeout(() => {
        startBtn.style.transition = 'all 1s ease';
        startBtn.style.opacity = '1';
        startBtn.style.transform = 'translateY(0)';
    }, 2500);

    startBtn.addEventListener('click', () => {
        introOverlay.style.opacity = '0';
        setTimeout(() => {
            introOverlay.style.display = 'none';
            boardContainer.style.animation = 'board-drop 1.5s forwards';
            setTimeout(() => {
                board.classList.add('show');
                createDustEffect();
            }, 100);
        }, 1000);
    });

    function createDustEffect() {
        const container = document.querySelector('.board-container');
        for (let i = 0; i < 30; i++) {
            const dust = document.createElement('div');
            dust.classList.add('dust');
            dust.style.left = `${Math.random() * container.offsetWidth}px`;
            dust.style.bottom = '0';
            dust.style.setProperty('--tx', `${(Math.random() - 0.5) * 200}px`);
            dust.style.setProperty('--ty', `${-Math.random() * 100 - 50}px`);
            dust.style.animation = `dust-spread ${Math.random() * 1 + 0.5}s forwards`;
            container.appendChild(dust);
            setTimeout(() => container.removeChild(dust), 1500);
        }
    }

    function initializeBoard() {
        board.innerHTML = '';
        for (let row = 0; row < boardSize; row++) {
            for (let col = 0; col < boardSize; col++) {
                const cell = document.createElement('div');
                cell.classList.add('cell');
                cell.dataset.row = row;
                cell.dataset.col = col;
                cell.addEventListener('click', handleCellClick);
                board.appendChild(cell);
            }
        }
        updateGameInfo();
    }

    function handleCellClick(event) {
        if (gameEnded) return;

        const cell = event.currentTarget;
        const row = parseInt(cell.dataset.row);
        const col = parseInt(cell.dataset.col);

        // 돌 유효성 및 승리 판정은 서버가 수행
        fetch('/omok/game', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `row=${row}&col=${col}`
        })
            .then(res => {
                if (!res.ok) throw new Error('서버 응답 오류');
                return res.json();
            })
            .then(data => {
                if (data.success) {
                    socket.send(JSON.stringify({
                        row: row,
                        col: col,
                        stone: data.stone,
                        gameOver: data.gameOver,
                        message: data.message
                    }));
                } else {
                    alert(data.message);
                }
            })
            .catch(err => {
                console.error(err);
                alert('서버 오류 발생');
            });
    }

    function placeStone(cell, player) {
        const stone = document.createElement('div');
        stone.classList.add('stone', player);
        cell.appendChild(stone);
        createEnergyEffect(cell, player);
    }

    function createEnergyEffect(cell, player) {
        const effect = document.createElement('div');
        effect.classList.add('energy-effect');
        effect.style.background = `radial-gradient(circle, ${player === 'black' ? 'rgba(0,0,0,0.6)' : 'rgba(255,255,255,0.6)'} 0%, transparent 70%)`;
        effect.style.width = effect.style.height = '30px';
        cell.appendChild(effect);

        effect.animate([
            { opacity: 0, transform: 'scale(1)' },
            { opacity: 0.7, transform: 'scale(2)' },
            { opacity: 0, transform: 'scale(3)' }
        ], {
            duration: 600,
            easing: 'ease-out'
        });

        const flash = document.createElement('div');
        flash.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;pointer-events:none;z-index:100;animation:flash 0.2s';
        document.body.appendChild(flash);
        setTimeout(() => document.body.removeChild(flash), 200);
        setTimeout(() => cell.removeChild(effect), 600);
    }

    function updateGameInfo() {
        turnIndicator.textContent = `${currentPlayer === 'black' ? '흑돌' : '백돌'} 차례입니다`;
        turnIndicator.className = currentPlayer;
    }

    function restartGame() {
        fetch("/omok/game", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "action=restart"
        });

        gameBoard = Array(boardSize).fill().map(() => Array(boardSize).fill(null));
        currentPlayer = 'black';
        gameEnded = false;
        initializeBoard();
        statusMessage.textContent = '게임을 재시작합니다!';
        winOverlay.style.opacity = '0';
        winOverlay.style.pointerEvents = 'none';
    }

    restartBtn.addEventListener('click', restartGame);
    closeWinBtn.addEventListener('click', () => {
        winOverlay.style.opacity = '0';
        winOverlay.style.pointerEvents = 'none';
    });

    window.addEventListener("beforeunload", () => {
        fetch("/omok/game", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "action=restart"
        });
    });

    initializeBoard();

});
