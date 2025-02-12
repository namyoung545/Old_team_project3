// /* scroll마다 한 페이지씩 이동하기 */
// window.onload = function () {
//     const elm = document.querySelectorAll(".scroll_page");
//     const elmCount = elm.length;
//     let isScrolling = false;

//     elm.forEach(function (item, index) {
//         item.addEventListener("wheel", function (event) {
//             if (isScrolling) return;
//             isScrolling = true;

//             let delta = event.deltaY > 0 ? -1 : 1;
//             let moveTop = window.scrollY;
//             let elmSelector = elm[index];

//             if (delta < 0 && index < elmCount - 1) {
//                 moveTop =
//                     window.pageYOffset +
//                     elmSelector.nextElementSibling.getBoundingClientRect().top;
//             } else if (delta > 0 && index > 0) {
//                 moveTop =
//                     window.pageYOffset +
//                     elmSelector.previousElementSibling.getBoundingClientRect().top;
//             }

//             window.scrollTo({ top: moveTop, left: 0, behavior: "smooth" });
//             setTimeout(() => (isScrolling = false), 1000); // 스크롤 방지
//         });
//     });
// };

document.addEventListener("DOMContentLoaded", () => {
    const pages = document.querySelectorAll(".scroll_page");
    const prevBtn = document.getElementById("prevBtn");
    const nextBtn = document.getElementById("nextBtn");
    let currentPage = 0;

    function showPage(index) {
        pages.forEach((page, i) => {
            page.classList.toggle("active", i === index);
        });

        // 첫 번째 페이지에서는 이전 버튼 비활성화, 마지막 페이지에서는 다음 버튼 비활성화
        prevBtn.style.display = index === 0 ? "none" : "block";
        nextBtn.style.display = index === pages.length - 1 ? "none" : "block";
    }

    // 아이콘 클릭 시 페이지 이동
    document.querySelectorAll(".chevron_btn").forEach(btn => {
        btn.addEventListener("click", (event) => {
            if (event.target.closest("#prevBtn") && currentPage > 0) {
                currentPage--;
            } else if (event.target.closest("#nextBtn") && currentPage < pages.length - 1) {
                currentPage++;
            }
            showPage(currentPage);
        });
    });

    // 초기 페이지 표시
    showPage(currentPage);
});
