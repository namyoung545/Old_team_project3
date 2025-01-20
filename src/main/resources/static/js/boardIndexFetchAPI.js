// 공지사항 게시판 페이지를 비동기적으로 로드하는 코드
// document.addEventListener("DOMContentLoaded", () => {
//     const menuCalendarLink = document.getElementById("menuNoticeBoardLink");
//     const mainContent = document.getElementById("mainContent");

//     if (!menuCalendarLink || !mainContent) {
//         console.error("menuNoticeBoardLink 또는 mainContent 요소를 찾을 수 없습니다!");
//         return;
//     }

//     menuCalendarLink.addEventListener("click", async (event) => {
//         event.preventDefault();

//         try {
//             // HTML 로드
//             const htmlResponse = await fetch('/managementPage/noticeBoard');
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