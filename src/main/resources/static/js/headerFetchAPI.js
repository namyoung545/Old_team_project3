// 게시판 인덱스 페이지를 비동기적으로 로드하는 코드
document.addEventListener("DOMContentLoaded", () => {
    const menuCalendarLink = document.getElementById("headerBoardIndexLink");
    const mainContent = document.getElementById("mainContent");

    if (!menuCalendarLink || !mainContent) {
        console.error("headerBoardIndexLink 또는 mainContent 요소를 찾을 수 없습니다!");
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
    const menuCalendarLink = document.getElementById("headerCalendarLink");
    const mainContent = document.getElementById("mainContent");

    if (!menuCalendarLink || !mainContent) {
        console.error("headerCalendarLink 또는 mainContent 요소를 찾을 수 없습니다!");
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
    const menuCalendarLink = document.getElementById("headerAsRegistLink");
    const mainContent = document.getElementById("mainContent");

    if (!menuCalendarLink || !mainContent) {
        console.error("headerAsRegistLink 또는 mainContent 요소를 찾을 수 없습니다!");
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
    const menuCalendarLink = document.getElementById("headerASprocessStatusLink");
    const mainContent = document.getElementById("mainContent");

    if (!menuCalendarLink || !mainContent) {
        console.error("headerASprocessStatusLink 또는 mainContent 요소를 찾을 수 없습니다!");
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

// // mypage 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const menuCalendarLink = document.getElementById("menuMemberModifyLink");
//     const mainContent = document.getElementById("mainContent");

//     if (!menuCalendarLink || !mainContent) {
//         console.error("menuMemberModifyLink 또는 mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     menuCalendarLink.addEventListener("click", async (event) => {
//         event.preventDefault();

//         try {
//             // HTML 로드
//             const htmlResponse = await fetch('/managementPage/memberModify');
//             if (!htmlResponse.ok) {
//                 throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
//             }
//             const html = await htmlResponse.text();
//             mainContent.innerHTML = html;
//         } catch (error) {
//             console.error("Error loading content or events:", error);
//         }
//     });
// });