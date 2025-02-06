// 메뉴로 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const menuCalendarLink = document.getElementById("menuBoardIndexLink");
//     const mainContent = document.getElementById("mainContent");

//     if (!menuCalendarLink || !mainContent) {
//         console.error("menuBoardIndexLink 또는 mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     menuCalendarLink.addEventListener("click", async (event) => {
//         event.preventDefault();

//         try {
//             // HTML 로드
//             const htmlResponse = await fetch('/managementPage/boardIndex');
//             if (!htmlResponse.ok) {
//                 throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
//             }
//             const html = await htmlResponse.text();
//             mainContent.innerHTML = html;
//             updateHeaderText("boardIndex");
//         } catch (error) {
//             console.error("Error loading content or events:", error);
//         }
//     });
// });

// 게시판 인덱스 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const menuCalendarLink = document.getElementById("menuBoardIndexLink");
//     const mainContent = document.getElementById("mainContent");

//     if (!menuCalendarLink || !mainContent) {
//         console.error("menuBoardIndexLink 또는 mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     menuCalendarLink.addEventListener("click", async (event) => {
//         event.preventDefault();

//         try {
//             // HTML 로드
//             const htmlResponse = await fetch('/managementPage/boardIndex');
//             if (!htmlResponse.ok) {
//                 throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
//             }
//             const html = await htmlResponse.text();
//             mainContent.innerHTML = html;
//             updateHeaderText("boardIndex");
//         } catch (error) {
//             console.error("Error loading content or events:", error);
//         }
//     });
// });

// // 공지사항 게시판 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const mainContent = document.getElementById("mainContent");

//     if (!mainContent) {
//         console.error("mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     // 이벤트 위임: mainContent 내부에서 클릭 이벤트 처리
//     mainContent.addEventListener("click", async (event) => {
//         // 클릭된 요소 확인
//         const target = event.target.closest("#menuNoticeBoardLink");
//         if (!target) return; // 클릭된 요소가 해당 ID가 아니면 무시

//         event.preventDefault();

//         try {
//             // HTML 로드
//             const htmlResponse = await fetch('/managementPage/noticeBoard');
//             if (!htmlResponse.ok) {
//                 throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
//             }
//             const html = await htmlResponse.text();
//             mainContent.innerHTML = html;
//             // updateHeaderText("boardIndex");
//         } catch (error) {
//             console.error("Error loading content or events:", error);
//         }
//     });
// });

// // QnA 게시판 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const mainContent = document.getElementById("mainContent");

//     if (!mainContent) {
//         console.error("mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     // 이벤트 위임: mainContent 내부에서 클릭 이벤트 처리
//     mainContent.addEventListener("click", async (event) => {
//         // 클릭된 요소 확인
//         const target = event.target.closest("#menuQnaBoardLink");
//         if (!target) return; // 클릭된 요소가 해당 ID가 아니면 무시

//         event.preventDefault();

//         try {
//             // HTML 로드
//             const htmlResponse = await fetch('/managementPage/qnaBoard');
//             if (!htmlResponse.ok) {
//                 throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
//             }
//             const html = await htmlResponse.text();
//             mainContent.innerHTML = html;
//             // updateHeaderText("boardIndex");
//         } catch (error) {
//             console.error("Error loading content or events:", error);
//         }
//     });
// });

// fullCalendar 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const menuCalendarLink = document.getElementById("menuCalendarLink");
//     const mainContent = document.getElementById("mainContent");

//     if (!menuCalendarLink || !mainContent) {
//         console.error("menuCalendarLink 또는 mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     menuCalendarLink.addEventListener("click", async (event) => {
//         event.preventDefault();

//         try {
//             // HTML 로드
//             const htmlResponse = await fetch('/managementPage/fullCalendar');
//             if (!htmlResponse.ok) {
//                 throw new Error(`HTML Load Error! status: ${htmlResponse.status}`);
//             }
//             const html = await htmlResponse.text();
//             mainContent.innerHTML = html;

//             // 이벤트 데이터 로드
//             const eventsResponse = await fetch('/managementPage/full-calendar-events');
//             if (!eventsResponse.ok) {
//                 throw new Error(`Events Load Error! status: ${eventsResponse.status}`);
//             }
//             const jsonData = await eventsResponse.json();

//             // FullCalendar 초기화
//             initializeFullCalendar(jsonData);
//         } catch (error) {
//             console.error("Error loading content or events:", error);
//         }
//     });
// });


// as 접수 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const menuCalendarLink = document.getElementById("menuAsRegistLink");
//     const mainContent = document.getElementById("mainContent");

//     if (!menuCalendarLink || !mainContent) {
//         console.error("menuAsRegistLink 또는 mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

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
//         } catch (error) {
//             console.error("Error loading content or events:", error);
//         }
//     });
// });

// as 처리 현황 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const menuCalendarLink = document.getElementById("menuASprocessStatusLink");
//     const mainContent = document.getElementById("mainContent");

//     if (!menuCalendarLink || !mainContent) {
//         console.error("menuASprocessStatusLink 또는 mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     menuCalendarLink.addEventListener("click", async (event) => {
//         event.preventDefault();

//         try {
//             // HTML 로드
//             const htmlResponse = await fetch('/managementPage/ASprocessStatus');
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

