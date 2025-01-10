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
