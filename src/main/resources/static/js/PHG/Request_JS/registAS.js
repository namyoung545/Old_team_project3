$(document).ready(() => {
	function initialize() {
		createTimeButton();
		createEmployeeButton();
		eventHandler();
	}

	function eventHandler() {
		$(document).on('click', '.searchAddress', function(event) {
			callPostCodeAPI($(this), event);
		});
	}

	function callPostCodeAPI(target, event) {
		let targetParent = target.closest('.customerAddress'); // 부모 요소를 지정
		let addressPostcode = targetParent.find('.addressPostcode');
		let addressRoad = targetParent.find('.addressRoad');
		let addressBname = targetParent.find('.addressBname');

		new daum.Postcode({
			oncomplete: function(data) {
				let roadAddr = data.roadAddress;
				let extraRoadAddr = '';

				if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
					extraRoadAddr += data.bname;
				}
				if (data.buildingName !== '' && data.apartment === 'Y') {
					extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
				}
				if (extraRoadAddr !== '') {
					extraRoadAddr = ' (' + extraRoadAddr + ')';
				}

				addressPostcode.val(data.zonecode);
				addressRoad.val(roadAddr);

				if (roadAddr !== '') {
					addressBname.val(extraRoadAddr);
				} else {
					addressBname.val('');
				}
			}
		}).open();
	}

	function createTimeButton() {
		const timeButtonsContainer = document.querySelector('.time-buttons');
		//		const displayTime = document.getElementById('displayTime');
		const selectedTimeInput = document.getElementById('selectedTime');

		// 버튼 생성 및 추가
		for (let hour = 9; hour <= 17; hour++) {
			const button = document.createElement('button');
			const time = `${hour.toString().padStart(2, '0')}:00`;

			button.type = 'button';
			button.textContent = time;
			button.dataset.time = time;

			button.addEventListener('click', () => {
				// 기존 활성화 상태 제거
				document.querySelectorAll('.time-buttons button').forEach(btn => btn.classList.remove('active'));

				// 선택한 버튼 활성화
				button.classList.add('active');

				// 선택 시간 표시 및 저장
				//				displayTime.textContent = time;
				selectedTimeInput.value = time;
			});

			timeButtonsContainer.appendChild(button);
		}

		//		// 폼 제출 이벤트
		//		document.getElementById('timeForm').addEventListener('submit', function(e) {
		//			if (!selectedTimeInput.value) {
		//				alert('시간을 선택해주세요!');
		//				e.preventDefault();
		//			} else {
		//				alert('선택된 시간: ' + selectedTimeInput.value);
		//			}
		//		});
	}

	function createEmployeeButton() {
		let $employeeContainer = $(document).find('.employee');
		let $employeeButton = $employeeContainer.find('.employeeButton');
		let $selectedEmployee = $employeeContainer.find('.selectedEmployee');

		let employeeArr = ['정동원', '허도혁', '오범우', '장광수', '손기환'];

		// 직원 버튼 생성 및 추가
		employeeArr.forEach(employee => {
			const button = document.createElement('button');
			button.type = 'button';
			button.textContent = employee;
			button.dataset.employee = employee;

			// 버튼 클릭 이벤트
			$(button).on('click', () => {
				// 기존 활성화 상태 제거
				$employeeButton.find('button').removeClass('active');

				// 선택한 버튼 활성화
				$(button).addClass('active');

				// 선택된 직원 이름 저장
				$selectedEmployee.val(employee);
			});

			// 버튼 추가
			$employeeButton.append(button);
		});
	}

	initialize();
});
