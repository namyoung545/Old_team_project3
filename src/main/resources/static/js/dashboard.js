/* scroll마다 한 페이지씩 이동하기 */
window.onload = function () {
    const elm = document.querySelectorAll(".scroll_page");
    const elmCount = elm.length;
    let isScrolling = false;

    elm.forEach(function (item, index) {
        item.addEventListener("wheel", function (event) {
            if (isScrolling) return;
            isScrolling = true;

            let delta = event.deltaY > 0 ? -1 : 1;
            let moveTop = window.scrollY;
            let elmSelector = elm[index];

            if (delta < 0 && index < elmCount - 1) {
                moveTop =
                    window.pageYOffset +
                    elmSelector.nextElementSibling.getBoundingClientRect().top;
            } else if (delta > 0 && index > 0) {
                moveTop =
                    window.pageYOffset +
                    elmSelector.previousElementSibling.getBoundingClientRect().top;
            }

            window.scrollTo({ top: moveTop, left: 0, behavior: "smooth" });
            setTimeout(() => (isScrolling = false), 1000); // 스크롤 방지
        });
    });
};