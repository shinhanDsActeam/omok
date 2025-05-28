document.addEventListener('DOMContentLoaded', () => {
    const overlayText = document.querySelector('.overlay-text');
    const text = overlayText.textContent;
    overlayText.textContent = ''; // 기존 내용 지움

    text.split('').forEach(char => {
        const span = document.createElement('span');
        span.textContent = char;
        overlayText.appendChild(span);
    });

    const letters = document.querySelectorAll('.overlay-text span');
    letters.forEach((span, index) => {
        span.style.animationDelay = `${index * 0.1 + 0.8}s`;
    });

});