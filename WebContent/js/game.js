let userInteracted = false;

document.addEventListener('click', () => {
    userInteracted = true;
});

document.addEventListener('DOMContentLoaded', function () {
    const boardSize = 15;
    const board = document.getElementById('board');
    const statusMessageGame = document.querySelector('#game-info #status-message');
    const statusMessageIntro = document.querySelector('.intro-overlay #status-message');
    const turnIndicator = document.getElementById('turn-indicator');
//    const restartBtn = document.getElementById('restart-btn');
    const surrenderBtn = document.getElementById('surrender-btn');
    const winOverlay = document.querySelector('.win-overlay');
    const winMessage = document.querySelector('.win-message');
    const martialMessage = document.querySelector('.martial-message');
    const closeWinBtn = document.getElementById('close-win');
    const introOverlay = document.querySelector('.intro-overlay');
    const introStartBtn = document.getElementById('intro-start-btn');
    const boardStartBtn = document.getElementById('start-btn');
    const boardContainer = document.querySelector('.board-container');
    const roomId = new URLSearchParams(window.location.search).get('roomId');
    // const nickname = '임시 닉네임';
    const contextPath = location.pathname.split('/')[1] ? '/' + location.pathname.split('/')[1] : '';
    const isHostBool = (isHost === true || isHost === "true");

    let gameBoard = Array(boardSize).fill().map(() => Array(boardSize).fill(null));
    let currentPlayer = 'black';
    let gameEnded = false;
    let myTurn = false;

    let turnTimer = null;
    let remainingTime = 30;

    const socket = new WebSocket("ws://" + location.host + location.pathname.replace(/\/[^\/]*$/, '') + "/ws/omok/" + roomId);
    const chatSocket = new WebSocket(`ws://${location.host}${contextPath}/ws/chat/${roomId}`);

    socket.onopen = () => {
        console.log("✅ WebSocket 연결됨");
        socket.send(JSON.stringify({ type: "syncRequest", roomId }));
    }
    chatSocket.onopen =() => console.log('chatSocket 연결완')

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
//        console.log("📨 메시지:", data);

        if (data.type === "startGame") {
            startGameAnimation();
            if (isHost) {
                currentPlayer = 'black';
                myTurn = true;
                updateGameInfo();
            } else {
                myTurn = false;
            }
            return;
        }

        if (data.type === "turnChange") {
            currentPlayer = data.currentPlayer;
            myTurn = data.currentPlayer === (isHost ? 'black' : 'white');
            updateGameInfo();
            return;
        }

        if (data.type === "timeoutAlert") {
            const msgBox = document.createElement('div');
            msgBox.className = 'timeout-alert';
            msgBox.textContent = !myTurn ? "🎯 이제 당신의 차례입니다!" : "⏰ 시간 초과! 상대 턴으로 넘어갑니다!";
            document.body.appendChild(msgBox);
            setTimeout(() => document.body.removeChild(msgBox), 3000);
            return;
        }

        if (data.type === "timerUpdate") {
            const { currentPlayer, time } = data;
            if (!currentPlayer || typeof time !== "number") return;

            const targetClass = currentPlayer === 'black'
                ? '.black-player .time-limit'
                : '.white-player .time-limit';
            const el = document.querySelector(targetClass);
            if (el) el.textContent = `${time}초`;
        }

        if (data.type === "userJoined") {
            if (data.hostNickname) {
                document.getElementById("host-nickname").textContent = data.hostNickname;
            }
            if (data.guestNickname) {
                document.getElementById("guest-nickname").textContent = data.guestNickname;
            }

            if (isHostBool) {
                if (!data.ready) {
                    if (statusMessageIntro) statusMessageIntro.textContent = '⏳ 참가자 기다리는 중...';
                    if (introStartBtn) introStartBtn.style.display = 'none';
                } else {
                    if (statusMessageIntro) statusMessageIntro.textContent = '🎯 게임 시작 가능!';
                    if (introStartBtn) introStartBtn.style.display = 'inline-block';
                }
            }
            return;
        }

//        if (data.type === "startGame") {
//            startGameAnimation();
//            return;
//        }

        if (data.type === "rematchRequest") {
            location.reload();
            return;
        }

        if (data.type === "syncBoard") {
            gameBoard = Array(boardSize).fill().map(() => Array(boardSize).fill(null));
            initializeBoard();

            data.stones.forEach(({ row, col, stone }) => {
                const cell = board.querySelector(`[data-row='${row}'][data-col='${col}']`);
                placeStone(cell, stone);
                gameBoard[row][col] = stone;
            });
            updateGameInfo();
            return;
        }

        if (data.type === "surrender") {
            gameEnded = true;
            winMessage.textContent = `${data.nickname} 님이 기권하셨습니다.`;
            martialMessage.textContent = '승리는 당신의 것!';
            winOverlay.style.opacity = '1';
            winOverlay.style.pointerEvents = 'auto';
            document.getElementById("game-end-buttons").style.display = 'flex';
            return;
        }

        if (data.type === 'stonePlaced') {
            const { row, col, stone } = data;
            if (gameBoard[row][col] === null && !gameEnded) {
                const cell = board.querySelector(`[data-row='${row}'][data-col='${col}']`);
                placeStone(cell, stone);
                gameBoard[row][col] = stone;

                stopTurnTimer();

                if (data.gameOver) {
                    gameEnded = true;
                    winMessage.textContent = `${data.winnerNickname} 승리!`;
                    martialMessage.textContent = '천하무적 승리의 순간!';
                    winOverlay.style.opacity = '1';
                    winOverlay.style.pointerEvents = 'auto';
                    document.getElementById("game-end-buttons").style.display = 'flex';
                } else {
                    socket.send(JSON.stringify({
                        type: "turnChange",
                        roomId,
                        currentPlayer: currentPlayer === 'black' ? 'white' : 'black'
                    }));
                }
            }
        }

        if (data.type === "opponentLeft") {
            alert("👋 상대방이 조용히 방을 떠났습니다.\n⚔️ 승부는 다음 기회에 이어가세요.");
            window.location.href = `${contextPath}/leaveRoom?roomId=${roomId}`;
            return;
        }
    };

    // 추가 CSS 삽입 (1초 알림용)
    const style = document.createElement('style');
    style.textContent = `.timeout-alert {
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background-color: rgba(0, 0, 0, 0.8);
        color: white;
        padding: 20px 30px;
        border-radius: 10px;
        font-size: 18px;
        z-index: 9999;
        animation: fadeOut 1s ease-in-out forwards;
    }
    @keyframes fadeOut {
        0% { opacity: 1; }
        100% { opacity: 0; }
    }`;
    document.head.appendChild(style);

    if (introStartBtn) {
        introStartBtn.addEventListener('click', () => {
            if (isHost) {
                socket.send(JSON.stringify({ type: "startGame", roomId }));
            }
        });
    }

    if (boardStartBtn) {
        boardStartBtn.addEventListener('click', () => {
            if (isHost) {
                socket.send(JSON.stringify({ type: "startGame", roomId }));
            }
        });
    }

    function startGameAnimation() {
        introOverlay.style.opacity = '0';
        setTimeout(() => {
            introOverlay.style.display = 'none';
            boardContainer.style.animation = 'board-drop 1.5s forwards';
            setTimeout(() => {
                board.classList.add('show');
                createDustEffect();
            }, 100);
        }, 1000);
    }

    window.requestRematch = function () {
        gameBoard = Array(boardSize).fill().map(() => Array(boardSize).fill(null));
        currentPlayer = 'black';
        gameEnded = false;

        initializeBoard(); // ✅ 오목판 초기화
        if (statusMessageGame) statusMessageGame.textContent = '재대결 start!';
        winOverlay.style.opacity = '0';
        winOverlay.style.pointerEvents = 'none';

        socket.send(JSON.stringify({ type: "rematchRequest", roomId }));
    }

    function startTurnTimer() {
        stopTurnTimer();
        remainingTime = 30;
        updateTimerUI();

        // 첫 broadcast로 초기값 전달
        socket.send(JSON.stringify({
            type: "timerUpdate",
            roomId,
            currentPlayer,
            time: remainingTime
        }));

        turnTimer = setInterval(() => {
            remainingTime--;
            updateTimerUI();

            // 실시간 broadcast
            socket.send(JSON.stringify({
                type: "timerUpdate",
                roomId,
                currentPlayer,
                time: remainingTime
            }));

            if (remainingTime <= 0) {
                handleTimeout();
            }
        }, 1000);
    }

    function stopTurnTimer() {
        if (turnTimer) {
            clearInterval(turnTimer);
            turnTimer = null;
        }
    }

    function updateTimerUI() {
        const target = currentPlayer === 'black'
            ? document.querySelector('.black-player .time-limit')
            : document.querySelector('.white-player .time-limit');
        if (target) target.textContent = `${remainingTime}초`;
    }

    function handleTimeout() {
        stopTurnTimer();
        socket.send(JSON.stringify({ type: "timeoutAlert", roomId }));
        socket.send(JSON.stringify({
            type: "turnChange",
            roomId,
            currentPlayer: currentPlayer === 'black' ? 'white' : 'black'
        }));
    }

    //채팅 메시지 수신처리
    chatSocket.onmessage = function(event) {
        const data = JSON.parse(event.data);
        const chatLog = document.getElementById('chat-log');
        const msg = document.createElement('div');
        msg.classList.add('chat-message');
        msg.innerHTML = `<span class="chat-nickname">${data.sender}</span>: <span class="chat-text">${data.message}</span>`;
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

    window.leaveRoom = function () {
        socket.send(JSON.stringify({
            type: "leaveRoom",
            nickname,
            roomId
        }));
        window.location.href = `${contextPath}/leaveRoom?roomId=${roomId}`;
    };

    function createDustEffect() {
        const container = boardContainer;
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
        if (gameEnded || !myTurn) return;

        const cell = event.currentTarget;
        const row = parseInt(cell.dataset.row);
        const col = parseInt(cell.dataset.col);

        fetch(`/omok/game?roomId=${roomId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `row=${row}&col=${col}`
        })
        .then(res => res.ok ? res.json() : Promise.reject('서버 오류'))
        .then(data => {
            if (data.success) {
                socket.send(JSON.stringify({
                    type: "stonePlaced",
                    row, col,
                    stone: currentPlayer,
                    gameOver: data.gameOver,
                    winnerNickname: nickname,
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
        // ✅ 사용자 상호작용 이후에만 사운드 재생
        const sound = document.getElementById('stone-sound');
        if (userInteracted && sound) {
            sound.currentTime = 0;
            sound.play().catch(e => console.warn("사운드 재생 실패", e));
        }
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
        ], { duration: 600, easing: 'ease-out' });

        const flash = document.createElement('div');
        flash.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;pointer-events:none;z-index:100;animation:flash 0.2s';
        document.body.appendChild(flash);
        setTimeout(() => document.body.removeChild(flash), 200);
        setTimeout(() => cell.removeChild(effect), 600);
    }

    function updateGameInfo() {
        turnIndicator.textContent = `${currentPlayer === 'black' ? '흑돌' : '백돌'} 차례입니다`;
        turnIndicator.className = currentPlayer;
        if (myTurn) startTurnTimer();
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
        if (statusMessageGame) statusMessageGame.textContent = '게임을 재시작합니다!';
        winOverlay.style.opacity = '0';
        winOverlay.style.pointerEvents = 'none';
    }

//    if (restartBtn) restartBtn.addEventListener('click', restartGame);

    // game.js 내부
     if (surrenderBtn) {
        surrenderBtn.addEventListener('click', () => {
            if (!gameEnded && confirm("정말 기권하시겠습니까?")) {
                socket.send(JSON.stringify({
                    type: "surrender",
                    nickname,
                    roomId
                }));
                window.location.href = `${contextPath}/leaveRoom?roomId=${roomId}`;
            }
        });
    }

    if (closeWinBtn) {
        closeWinBtn.addEventListener('click', () => {
            winOverlay.style.opacity = '0';
            winOverlay.style.pointerEvents = 'none';
        });
    }

    window.addEventListener("beforeunload", () => {
        fetch("/omok/game", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "action=restart"
        });

        if (socket.readyState === WebSocket.OPEN) {
            socket.close();
        }
    });

    initializeBoard();

});
