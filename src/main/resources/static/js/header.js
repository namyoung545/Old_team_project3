// h1 요소 선택
const headerText = document.getElementById('headerText');

// 클릭 이벤트 추가
headerText.addEventListener('click', () => {
    // URL 이동
    window.location.href = '/'; // 이동할 URL 설정
});

// 마우스커서 변환 이벤트 추가
headerText.addEventListener('mouseover', () => {
    // 마우스커서 모양 변경
    headerText.style.cursor = 'pointer';
});