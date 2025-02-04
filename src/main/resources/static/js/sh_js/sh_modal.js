$(document).ready(function() {
    console.log("sh_modal.js loaded");
	const modal = $(".eventModal");
	const closeButton = modal.find(".eventButton");

	// 모달 열기 함수
	window.openModal = function openModal(eventDetails) {
		console.log(eventDetails);
		console.log(modal);
		$("#eventName").text(eventDetails.title || "고객 정보 없음");
		$("#eventTime").text(eventDetails.time || "시간 정보 없음");
		$("#eventLocation").text(eventDetails.location || "주소 없음");
		$("#eventDetailLocation").text(eventDetails.detailLocation || "세부 주소 없음");
		modal.css("display", "flex"); // 모달 표시
	}

	// 모달 닫기 함수
	function closeModal() {
		modal.css("display", "none"); // 모달 숨기기
	}

	closeButton.on("click", closeModal);

	// 외부 클릭 시 모달 닫기
	$(window).on("click", function(event) {
		if ($(event.target).is(modal)) {
			closeModal();
		}
	});

	// 테스트용 모달 열기 (예제)
	//	openModal({
	//		title: "테스트 이벤트",
	//		time: "2024-12-25 14:00",
	//		location: "서울시 강남구 테헤란로",
	//		detailLocation: "빌딩 10층 1002호"
	//	});
});