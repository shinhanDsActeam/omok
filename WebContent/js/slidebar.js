document.addEventListener('DOMContentLoaded', () => {
    const profileBtn = document.getElementById('profileBtn');
    const slidebar = document.getElementById('slidebar');
    const closeBtn = document.getElementById('closeBtn');

    profileBtn.addEventListener('click', () => {
        console.log('pb');
        slidebar.classList.add('open');
    });

    closeBtn.addEventListener('click', () => {
        console.log('cb');

        slidebar.classList.remove('open');
    });
});