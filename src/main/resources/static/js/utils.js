// 페이지 스크롤바 너비 조정
export function adjustScrollbarPadding() {
    const body = document.body;
    const hasScrollbar = window.innerWidth > document.documentElement.clientWidth;

    if (hasScrollbar) {
        body.style.paddingRight = '16px'; // 스크롤바 너비 추가
    } else {
        body.style.paddingRight = '0';
    }
}