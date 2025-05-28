document.addEventListener('DOMContentLoaded', () => {
    const profileBtn = document.getElementById('profileBtn');
    const slidebar = document.getElementById('slidebar');
    const closeBtn = document.getElementById('closeBtn');

    profileBtn.addEventListener('click', (e) => {
        console.log('pb');
        e.stopPropagation();
        slidebar.classList.add('open');
    });

    closeBtn.addEventListener('click', () => {
        console.log('cb');

        slidebar.classList.remove('open');
    });

    slidebar.addEventListener('click', (e) => {
        e.stopPropagation(); 
    });

    document.addEventListener('click', () => {
        if (slidebar.classList.contains('open')) {
            slidebar.classList.remove('open');
        }
    });

});