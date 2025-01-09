document.addEventListener('DOMContentLoaded', function() {
    var calendarEl = document.getElementById('dy_calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        events: jsonData, // JSON 데이터 (Controller에서 전달)
    });
    calendar.render();
});

