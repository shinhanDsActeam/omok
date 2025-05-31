document.addEventListener('DOMContentLoaded', () => {
    const back = document.querySelector('.back');
    const flipInner = document.querySelector('.flip-inner');
    function setUrl() {
        if (winRate >= 80) {
            back.style.backgroundImage = "url('/omok/image/horse4.png')";
        } else if (winRate >= 50) {
            back.style.backgroundImage = "url('/omok/image/horse3.png')";
        } else if (winRate >= 20) {
            back.style.backgroundImage = "url('/omok/image/horse2.png')";
        } else {
            back.style.backgroundImage = "url('/omok/image/horse1.png')";
        }
    }

    flipInner.addEventListener('mouseenter', setUrl);
});