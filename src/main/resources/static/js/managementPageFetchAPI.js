// fullCalendar 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const menuCalendarLink = document.getElementById("menuCalendarLink");
//     const mainContent = document.getElementById("mainContent");

//     if (!menuCalendarLink || !mainContent) {
//         console.error("menuCalendarLink 또는 mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     menuCalendarLink.addEventListener("click", (event) => {
//         event.preventDefault();

//         // 비동기적으로 FullCalendar 페이지 로드
//         fetch('/managementPage/fullCalendar')
//             .then((response) => {
//                 if (!response.ok) {
//                     throw new Error(`HTTP error! status: ${response.status}`);
//                 }
//                 return response.text();
//             })
//             .then((html) => {
//                 mainContent.innerHTML = html;

//                 // JSON 데이터 로드
//                 fetch('/managementPage/full-calendar-events')
//                     .then((res) => res.json())
//                     .then((jsonData) => {
//                         initializeFullCalendar(jsonData); // 캘린더 초기화
//                     })
//                     .catch((err) => {
//                         console.error("Error loading events:", err);
//                     });
//             })
//             .catch((error) => {
//                 console.error("Error loading new content:", error);
//             });
//     });
// });

// 게시판 인덱스 페이지를 비동기적으로 로드하는 코드
document.addEventListener("DOMContentLoaded", () => {
    const menuCalendarLink = document.getElementById("menuBoardIndexLink");
    const mainContent = document.getElementById("mainContent");

    if (!menuCalendarLink || !mainContent) {
        console.error("menuBoardIndexLink 또는 mainContent 요소를 찾을 수 없습니다!");
        return;
    }

    menuCalendarLink.addEventListener("click", async (event) => {
        event.preventDefault();

        try {
            // HTML 로드
            const htmlResponse = await fetch('/managementPage/boardIndex');
            if (!htmlResponse.ok) {
                throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
            }
            const html = await htmlResponse.text();
            mainContent.innerHTML = html;
            updateHeaderText("boardIndex");
        } catch (error) {
            console.error("Error loading content or events:", error);
        }
    });
});

// fullCalendar 페이지를 비동기적으로 로드하는 코드
document.addEventListener("DOMContentLoaded", () => {
    const menuCalendarLink = document.getElementById("menuCalendarLink");
    const mainContent = document.getElementById("mainContent");

    if (!menuCalendarLink || !mainContent) {
        console.error("menuCalendarLink 또는 mainContent 요소를 찾을 수 없습니다!");
        return;
    }

    menuCalendarLink.addEventListener("click", async (event) => {
        event.preventDefault();

        try {
            // HTML 로드
            const htmlResponse = await fetch('/managementPage/fullCalendar');
            if (!htmlResponse.ok) {
                throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
            }
            const html = await htmlResponse.text();
            mainContent.innerHTML = html;

            // 이벤트 데이터 로드
            const eventsResponse = await fetch('/managementPage/full-calendar-events');
            if (!eventsResponse.ok) {
                throw new Error(`Events Load Error! status: ${eventsResponse.status}`);
            }
            const jsonData = await eventsResponse.json();

            // FullCalendar 초기화
            initializeFullCalendar(jsonData);
        } catch (error) {
            console.error("Error loading content or events:", error);
        }
    });
});

// as 접수 페이지를 비동기적으로 로드하는 코드
document.addEventListener("DOMContentLoaded", () => {
    const menuCalendarLink = document.getElementById("menuAsRegistLink");
    const mainContent = document.getElementById("mainContent");

    if (!menuCalendarLink || !mainContent) {
        console.error("menuAsRegistLink 또는 mainContent 요소를 찾을 수 없습니다!");
        return;
    }

    menuCalendarLink.addEventListener("click", async (event) => {
        event.preventDefault();

        try {
            // HTML 로드
            const htmlResponse = await fetch('/managementPage/registAS');
            if (!htmlResponse.ok) {
                throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
            }
            const html = await htmlResponse.text();
            mainContent.innerHTML = html;
        } catch (error) {
            console.error("Error loading content or events:", error);
        }
    });
});

// as 처리 현황 페이지를 비동기적으로 로드하는 코드
document.addEventListener("DOMContentLoaded", () => {
    const menuCalendarLink = document.getElementById("menuASprocessStatusLink");
    const mainContent = document.getElementById("mainContent");

    if (!menuCalendarLink || !mainContent) {
        console.error("menuASprocessStatusLink 또는 mainContent 요소를 찾을 수 없습니다!");
        return;
    }

    menuCalendarLink.addEventListener("click", async (event) => {
        event.preventDefault();

        try {
            // HTML 로드
            const htmlResponse = await fetch('/managementPage/ASprocessStatus');
            if (!htmlResponse.ok) {
                throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
            }
            const html = await htmlResponse.text();
            mainContent.innerHTML = html;
        } catch (error) {
            console.error("Error loading content or events:", error);
        }
    });
});


// // as 접수 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const menuCalendarLink = document.getElementById("menuAsRegistLink");
//     const mainContent = document.getElementById("mainContent");

//     if (!menuCalendarLink || !mainContent) {
//         console.error("menuAsRegistLink 또는 mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     const adjustScrollbarPadding = () => {
//         const html = document.documentElement;
//         const body = document.body;
//         const hasScrollbar = window.innerWidth > document.documentElement.clientWidth;

//         if (hasScrollbar) {
//             html.style.paddingRight = '16px'; // 스크롤바 너비 추가
//         } else {
//             html.style.paddingRight = '0';
//         }
//     };

//     // 초기 실행 (스크롤바 상태 확인)
//     adjustScrollbarPadding();

//     // 창 크기 변경 시 실행
//     window.addEventListener('resize', adjustScrollbarPadding);

//     menuCalendarLink.addEventListener("click", async (event) => {
//         event.preventDefault();

//         try {
//             // HTML 로드
//             const htmlResponse = await fetch('/managementPage/registAS');
//             if (!htmlResponse.ok) {
//                 throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
//             }
//             const html = await htmlResponse.text();
//             mainContent.innerHTML = html;

//             // 새로 로드된 컨텐츠에 대해 스크롤바 상태 확인 및 조정
//             adjustScrollbarPadding();
//         } catch (error) {
//             console.error("Error loading content or events:", error);
//         }
//     });
// });
