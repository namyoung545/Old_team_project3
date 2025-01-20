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
