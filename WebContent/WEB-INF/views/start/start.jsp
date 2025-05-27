<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>5~ 의 전략</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Arial', sans-serif;
            background: linear-gradient(135deg, #2c1810, #4a2c17, #6d4423);
            height: 100vh;
            overflow: hidden;
            position: relative;
        }

        .overlay {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(45deg, rgba(0,0,0,0.3), rgba(139,69,19,0.2));
            z-index: 1;
        }

        .container {
            position: relative;
            z-index: 2;
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }

        .title {
            font-size: 5rem;
            font-weight: bold;
            color: #FFD700;
            text-shadow: 3px 3px 6px rgba(0,0,0,0.8);
            margin-bottom: 2rem;
            opacity: 0;
            transform: translateY(-50px) scale(0.8);
            animation: titleAppear 2s ease-out forwards;
            text-align: center;
            letter-spacing: 0.1em;
        }

        .subtitle {
            font-size: 1.5rem;
            color: #FFF8DC;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.7);
            margin-bottom: 3rem;
            opacity: 0;
            animation: subtitleAppear 2s ease-out 1s forwards;
            text-align: center;
        }

        .button-container {
            display: flex;
            gap: 2rem;
            opacity: 0;
            animation: buttonsAppear 1.5s ease-out 2.5s forwards;
        }

        .game-button {
            padding: 1rem 2.5rem;
            font-size: 1.3rem;
            font-weight: bold;
            border: 3px solid #FFD700;
            background: linear-gradient(45deg, #8B4513, #A0522D);
            color: #FFD700;
            border-radius: 10px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(0,0,0,0.5);
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        .game-button:hover {
            background: linear-gradient(45deg, #A0522D, #CD853F);
            color: #FFF;
            border-color: #FFF;
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(0,0,0,0.7);
        }

        .game-button:active {
            transform: translateY(-1px);
        }

        .smoke {
            position: absolute;
            width: 4px;
            height: 4px;
            background: rgba(255, 255, 255, 0.8);
            border-radius: 50%;
            opacity: 0;
            animation: smoke 8s infinite linear;
        }

        .fire-effect {
            position: absolute;
            width: 6px;
            height: 6px;
            background: radial-gradient(circle, #ff6b35, #f7931e, #ffff00);
            border-radius: 50%;
            opacity: 0;
            animation: fire 4s infinite linear;
        }

        @keyframes titleAppear {
            0% {
                opacity: 0;
                transform: translateY(-50px) scale(0.8);
            }
            50% {
                opacity: 0.7;
                transform: translateY(-20px) scale(0.9);
            }
            100% {
                opacity: 1;
                transform: translateY(0) scale(1);
            }
        }

        @keyframes subtitleAppear {
            0% {
                opacity: 0;
                transform: translateY(20px);
            }
            100% {
                opacity: 1;
                transform: translateY(0);
            }
        }

        @keyframes buttonsAppear {
            0% {
                opacity: 0;
                transform: translateY(30px);
            }
            100% {
                opacity: 1;
                transform: translateY(0);
            }
        }

        @keyframes smoke {
            0% {
                transform: translateY(100vh) translateX(0) scale(0.5);
                opacity: 0;
            }
            10% {
                opacity: 0.8;
            }
            50% {
                opacity: 0.6;
                transform: translateY(50vh) translateX(20px) scale(1);
            }
            100% {
                transform: translateY(-10vh) translateX(50px) scale(1.5);
                opacity: 0;
            }
        }

        @keyframes fire {
            0% {
                transform: translateY(100vh) scale(0.3);
                opacity: 0;
            }
            20% {
                opacity: 1;
            }
            80% {
                opacity: 0.8;
            }
            100% {
                transform: translateY(-20vh) scale(0);
                opacity: 0;
            }
        }

        .particles {
            position: absolute;
            width: 100%;
            height: 100%;
            overflow: hidden;
            z-index: 1;
        }

        .particle {
            position: absolute;
            width: 3px;
            height: 3px;
            background: #FFD700;
            border-radius: 50%;
            opacity: 0.7;
            animation: float 6s infinite linear;
        }

        @keyframes float {
            0% {
                transform: translateY(100vh) rotate(0deg);
                opacity: 0.7;
            }
            100% {
                transform: translateY(-10px) rotate(360deg);
                opacity: 0;
            }
        }

        /* 반응형 디자인 */
        @media (max-width: 768px) {
            .title {
                font-size: 3rem;
            }

            .subtitle {
                font-size: 1.2rem;
            }

            .button-container {
                flex-direction: column;
                gap: 1rem;
            }

            .game-button {
                padding: 0.8rem 2rem;
                font-size: 1.1rem;
            }

            .yuubi-character {
                width: 100px;
            }
        }
    </style>
</head>
<body>
    <div class="overlay"></div>

    <!-- 파티클 효과 -->
    <div class="particles" id="particles"></div>

    <!-- 연기 효과 -->
    <div class="particles" id="smoke-container"></div>

    <!-- 불꽃 효과 -->
    <div class="particles" id="fire-container"></div>

    <div class="container">
        <h1 class="title">5~ 의 전략</h1>
        <p class="subtitle">오목 전략의 게임</p>

        <div class="button-container">
            <a href="../omok/join" class="game-button">게임 참여</a>
            <a href="../omok/login" class="game-button">로그인</a>
        </div>
    </div>



    <script>
        // 연기 효과 생성
        function createSmoke() {
            const smokeContainer = document.getElementById('smoke-container');

            for (let i = 0; i < 8; i++) {
                setTimeout(() => {
                    const smoke = document.createElement('div');
                    smoke.className = 'smoke';
                    smoke.style.left = Math.random() * 100 + '%';
                    smoke.style.animationDelay = Math.random() * 3 + 's';
                    smoke.style.animationDuration = (Math.random() * 4 + 4) + 's';
                    smokeContainer.appendChild(smoke);

                    setTimeout(() => {
                        if (smoke.parentNode) {
                            smoke.parentNode.removeChild(smoke);
                        }
                    }, 8000);
                }, i * 500);
            }
        }

        // 불꽃 효과 생성
        function createFire() {
            const fireContainer = document.getElementById('fire-container');

            for (let i = 0; i < 12; i++) {
                setTimeout(() => {
                    const fire = document.createElement('div');
                    fire.className = 'fire-effect';
                    fire.style.left = Math.random() * 100 + '%';
                    fire.style.animationDelay = Math.random() * 2 + 's';
                    fire.style.animationDuration = (Math.random() * 2 + 3) + 's';
                    fireContainer.appendChild(fire);

                    setTimeout(() => {
                        if (fire.parentNode) {
                            fire.parentNode.removeChild(fire);
                        }
                    }, 4000);
                }, i * 200);
            }
        }
        function createParticles() {
            const particlesContainer = document.getElementById('particles');

            for (let i = 0; i < 20; i++) {
                setTimeout(() => {
                    const particle = document.createElement('div');
                    particle.className = 'particle';
                    particle.style.left = Math.random() * 100 + '%';
                    particle.style.animationDelay = Math.random() * 6 + 's';
                    particle.style.animationDuration = (Math.random() * 3 + 3) + 's';
                    particlesContainer.appendChild(particle);

                    // 파티클 제거
                    setTimeout(() => {
                        if (particle.parentNode) {
                            particle.parentNode.removeChild(particle);
                        }
                    }, 6000);
                }, i * 300);
            }
        }

        // 페이지 로드 시 모든 효과 생성
        window.addEventListener('load', () => {
            createParticles();
            createSmoke();
            createFire();

            // 주기적으로 효과들 생성
            setInterval(createParticles, 3000);
            setInterval(createSmoke, 6000);
            setInterval(createFire, 4000);
        });

        // 버튼 클릭 효과음 (선택사항)
        document.querySelectorAll('.game-button').forEach(button => {
            button.addEventListener('mouseenter', () => {
                button.style.textShadow = '0 0 10px #FFD700';
            });

            button.addEventListener('mouseleave', () => {
                button.style.textShadow = 'none';
            });
        });
    </script>
</body>
</html>