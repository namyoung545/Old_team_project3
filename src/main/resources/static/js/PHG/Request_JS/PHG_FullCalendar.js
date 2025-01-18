function initializeFullCalendar(jsonData) {
    const calendarEl = document.getElementById("fullCalendar");
    if (!calendarEl) {
        console.error("fullCalendar 요소를 찾을 수 없습니다!");
        return;
    }

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: "dayGridMonth",
        locale: "ko",
        events: jsonData, // 서버에서 전달받은 이벤트 데이터
    });

    calendar.render(); // 캘린더 렌더링
}

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