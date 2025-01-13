// h1 요소 선택
// const headerText = document.getElementById('headerText');

// h1 요소 선택
const headerText = document.querySelectorAll('.headerText');

// 클릭 이벤트 추가
// headerText.addEventListener('click', () => {
//     // URL 이동
//     window.location.href = '/'; // 이동할 URL 설정
// });

// 클릭 이벤트 추가
headerText.forEach((element) => {
    element.addEventListener('click', () => {
        // URL 이동
        window.location.href = '/'; // 이동할 URL 설정
    });
});

// 마우스커서 변환 이벤트 추가
// headerText.addEventListener('mouseover', () => {
//     // 마우스커서 모양 변경
//     headerText.style.cursor = 'pointer';
// });

// 마우스커서 변환 이벤트 추가
headerText.forEach((element) => {
    element.addEventListener('mouseover', () => {
        // 마우스커서 모양 변경
        element.style.cursor = 'pointer';
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const defaultHeader = document.getElementById("defaultHeader");
    const boardIndexHeader = document.getElementById("boardIndexHeader");

    // 페이지 상태에 따라 표시할 헤더 설정
    function updateHeader(state) {
        if (state === "default") {
            defaultHeader.style.display = "block";
            boardIndexHeader.style.display = "none";
        } else if (state === "boardIndex") {
            defaultHeader.style.display = "none";
            boardIndexHeader.style.display = "block";
        }
    }

    // 예: 현재 URL로 상태 결정 (사용자 정의 로직)
    const currentPage = window.location.pathname;
    if (currentPage.includes("/managementPage")) {
        updateHeader("boardIndex");
    } else {
        updateHeader("default");
    }
});
