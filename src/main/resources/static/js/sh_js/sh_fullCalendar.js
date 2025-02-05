document.addEventListener("DOMContentLoaded", () => {
    console.log("sh_fullCalendar.js loaded");
    const calendarEl = document.getElementById("fullCalendar");

    if (!calendarEl) {
        console.error("Calendar element not found!");
        return;
    }

    function initialize() {
        featchCalendarEvents()
            .then((events) => {
                // console.log("Fetched events:", events);
                events = dataToEvent(events);
                const calendar = new FullCalendar.Calendar(calendarEl, {
                    initialView: 'dayGridMonth',
                    locale: 'ko',
                    headerToolbar: {
                        start: 'today prev,next',
                        center: 'title',
                        end: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
                    },
                    events: events,
                    eventColor: '#e77c2a', // 기본 배경색
                    eventTextColor: '#f0f0f0', // 텍스트 색상
                    eventClick: function (info) {
                        handleEventClick(info);
                    }
                });
                calendar.render();
            })
            .catch((error) => {
                console.error("Error initializing FullCalendar:", error);
            });
    }


    // 이벤트 클릭 시 상세 정보 표시
    function handleEventClick(info) {
        selectedEvent = info.event;
        // console.log("Selected event:", selectedEvent.extendedProps);
        // 날짜 및 시간을 시간대 설정으로 포맷
        let startDate = new Date(selectedEvent.start);
        let startTime = startDate.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
        let timeData = startTime;

        locationData = selectedEvent.extendedProps.address || "주소 확인요망";
        detailLocationData = selectedEvent.extendedProps.detailAddress || "세부주소 확인요망";
        facilityTypeData = String(selectedEvent.extendedProps.facilityType).charAt(0).toUpperCase() + String(selectedEvent.extendedProps.facilityType).slice(1) || "시설 유형 확인요망";

        window.openModal({
            title: selectedEvent.title,
            time: timeData,
            phone: selectedEvent.extendedProps.phone,
            email: selectedEvent.extendedProps.email,
            details: selectedEvent.extendedProps.details,
            postCode: selectedEvent.extendedProps.postCode,
            location: locationData,
            detailLocation: detailLocationData,
            facilityType: facilityTypeData,
            issueTitle: selectedEvent.extendedProps.issueTitle,
            issueDetails: selectedEvent.extendedProps.issueDetails,
            prefferedDateTime: selectedEvent.extendedProps.prefferedDateTime,
            receptionStatus: selectedEvent.extendedProps.receptionStatus,
            receptionDelivery: selectedEvent.extendedProps.receptionDelivery,
        });

        // 카카오 지도 로드
        loadMap(locationData);
    }

    function dataToEvent(data) {
        return data.map(event => ({
            title: event.name || "제목 없음",
            start: event.prefferedDateTime || null,  // 시작 날짜
            end: event.end || null,  // 종료 날짜 (데이터에 없으면 null)
            extendedProps: {
                name: event.name || "이름 없음",
                phone: event.phone || "연락처 없음",
                email: event.email || "이메일 없음",
                postCode: event.postCode || "우편번호 없음",
                address: event.address || "주소 없음",
                detailAddress: event.detailAddress || "상세 주소 없음",
                facilityType: event.facilityType || "시설 유형 없음",
                issueTitle: event.issueTitle || "상태 없음",
                issueDetails: event.issueDetails || "상세 내용 없음",
                prefferedDateTime: event.prefferedDateTime || "날짜 없음",
                receptionStatus: event.receptionStatus || "접수 확인 중",
                receptionDelivery: event.receptionDelivery || "미배정"
            }
        }));
    }


    function featchCalendarEvents() {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: '/sh_api/calendarEvents',
                type: 'POST',
                // dataType: 'json',
                success: function (data) {
                    resolve(data);
                },
                error: function (error) {
                    console.error("Error fetching events:", error);
                    reject(error);
                }
            })
        });
    }

    initialize();

    // fetch('/managementPage/full-calendar-events')
    //     .then((response) => {
    //         console.log("Response status:", response);
    //         if (!response.ok) {
    //             throw new Error(`Failed to fetch events: ${response.status}`);
    //         }
    //         return response.json();
    //     })
    //     .then((events) => {
    //         console.log("Fetched events:", events);
    //         const calendar = new FullCalendar.Calendar(calendarEl, {
    //             initialView: 'dayGridMonth',
    //             locale: 'ko',
    //             events: events, // 서버에서 가져온 이벤트 데이터 사용
    //         });
    //         calendar.render();
    //     })
    //     .catch((error) => {
    //         console.error("Error initializing FullCalendar:", error);
    //     });
});