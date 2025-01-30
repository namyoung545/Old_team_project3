// h1 요소 선택
const headerText = document.querySelectorAll('.headerText');
console.log("headerText:",headerText);

// 클릭 이벤트 추가
headerText.forEach((element) => {
    element.addEventListener('click', () => {
        // URL 이동
        window.location.href = '/'; // 이동할 URL 설정
    });
});

// 마우스커서 변환 이벤트 추가
headerText.forEach((element) => {
    element.addEventListener('mouseover', () => {
        // 마우스커서 모양 변경
        element.style.cursor = 'pointer';
    });
});

window.addEventListener('DOMContentLoaded', function() {
    const header = document.getElementById('header');
    const footer = document.getElementById('footer');

    // 특정 페이지들의 경로를 배열로 저장
    const specialPages = ['/login', '/', '/signup', '/memberModify', '/dashboard'];

    // 페이지가 특정 조건일 때 (예: URL에 특정 문자열이 포함된 경우)
    // if (window.location.pathname === '/login') { 페이지 1개만 조건일때 사용
    if (specialPages.includes(window.location.pathname)) {
        // 특정 페이지에서는 margin-left를 0으로 설정
        footer.style.marginLeft = '0px';
        header.style.marginLeft = '0px';
        // 로컬스토리지에 상태 저장 (필요한 경우)
        localStorage.setItem('isSpecialPage', 'true');
    } else if (localStorage.getItem('isSpecialPage') === 'true') {
        // 로컬스토리지에 저장된 상태에 따라 원래 마진값을 복원
        footer.style.marginLeft = '60px';
        header.style.marginLeft = '60px';
        localStorage.removeItem('isSpecialPage'); // 상태 초기화
    } else {
        // 기본 상태 (마진값 60px)
        footer.style.marginLeft = '60px';
        header.style.marginLeft = '60px';
    }
});
