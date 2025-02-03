document.addEventListener("DOMContentLoaded", () => {
    const calendarEl = document.getElementById("fullCalendar");

    if (!calendarEl) {
        console.error("Calendar element not found!");
        return;
    }

    fetch('/managementPage/full-calendar-events')
        .then((response) => {
            if (!response.ok) {
                throw new Error(`Failed to fetch events: ${response.status}`);
            }
            return response.json();
        })
        .then((events) => {
            const calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'dayGridMonth',
                locale: 'ko',
                events: events, // 서버에서 가져온 이벤트 데이터 사용
            });
            calendar.render();
        })
        .catch((error) => {
            console.error("Error initializing FullCalendar:", error);
        });
});