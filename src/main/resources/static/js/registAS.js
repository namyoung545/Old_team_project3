    // 주소 검색 함수 - 전역 스코프에 정의
    function searchAddress() {
        new daum.Postcode({
            oncomplete: function(data) {
                document.getElementById('postcode').value = data.zonecode;
                document.getElementById('address').value = data.address;
                document.getElementById('detailAddress').focus();
            }
        }).open();
    }

    document.addEventListener('DOMContentLoaded', function() {
        // DOM 요소 참조
        const form = document.getElementById('maintenanceForm');
        const phoneNumber2 = document.getElementById('phoneNumber2');
        const phoneNumber3 = document.getElementById('phoneNumber3');
        const textarea = document.getElementById('issueDetails');
        const charCount = document.querySelector('.char-count');
        const preferredDate = document.getElementById('preferredDate');
        const preferredTime = document.getElementById('preferredTime');
        const privacyCheckbox = document.getElementById('privacyAgreement');
        
        // 1) 내일 날짜(오늘 +1일)를 min으로 설정 -> 오늘까지는 선택 불가
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1); 
        const tomorrowString = tomorrow.toISOString().split('T')[0];
        preferredDate.setAttribute('min', tomorrowString);

        // 날짜 선택 시 가능한 시간 슬롯 로드
        preferredDate.addEventListener('change', function() {
            const selectedDate = this.value;
            const preferredTimeSelect = document.getElementById('preferredTime');
            
            // 모든 옵션 초기화
            preferredTimeSelect.innerHTML = '<option value="">시간 선택</option>';
            
            // AJAX 요청
            fetch(`/managementPage/registAS/checkAvailableTimeSlots?selectedDate=${selectedDate}`)
                .then(response => response.json())
                .then(availableTimeSlots => {
                    availableTimeSlots.forEach(timeSlot => {
                        const option = document.createElement('option');
                        option.value = timeSlot;
                        option.textContent = `${timeSlot.split(':')[0]}시`;
                        preferredTimeSelect.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('시간 정보를 불러오는 중 오류가 발생했습니다.');
                });
        });

        // 유틸리티 함수: 시간을 제외한 날짜 객체
        function getDateWithoutTime(date) {
            return new Date(date.getFullYear(), date.getMonth(), date.getDate());
        }

        // 방문 일자/시간 유효성 검사 (유효하지 않으면 체크박스 자동 해제)
        function validateDateTime() {
            if (!preferredDate.value || !preferredTime.value) {
                // 둘 중 하나라도 선택되지 않은 경우 별도 처리하지 않음
                return true;
            }
            
            const selectedDate = new Date(preferredDate.value);
            const selectedTime = preferredTime.value;
            const now = new Date();
            
            const todayWithoutTime = getDateWithoutTime(now);
            const selectedDateWithoutTime = getDateWithoutTime(selectedDate);
            
            // 과거 날짜인 경우
            if (selectedDateWithoutTime < todayWithoutTime) {
                alert("과거 날짜는 선택할 수 없습니다.");
                preferredDate.value = '';
                privacyCheckbox.checked = false;  // 체크박스 자동 해제
                return false;
            }

            // 오늘 날짜인 경우 시간도 비교(만약 오늘 날짜를 막아두지 않는다면)
            if (selectedDateWithoutTime.getTime() === todayWithoutTime.getTime()) {
                const currentHour = now.getHours();
                const selectedHour = parseInt(selectedTime.split(':')[0]);
                
                if (selectedHour <= currentHour) {
                    alert("현재 시간 이후의 시간을 선택해주세요.");
                    preferredTime.value = '';
                    privacyCheckbox.checked = false;
                    return false;
                }
            }
            return true;
        }

        // 상세설명 글자수 카운팅
        function updateCharCount() {
            const currentLength = textarea.value.length;
            charCount.textContent = `${currentLength}/2000`;
            
            if (currentLength > 2000) {
                textarea.value = textarea.value.substring(0, 2000);
                charCount.textContent = '2000/2000';
            }
        }

        // 이벤트 리스너
        textarea.addEventListener('input', updateCharCount);
        preferredDate.addEventListener('change', validateDateTime);
        preferredTime.addEventListener('change', validateDateTime);

        // 폼 제출 이벤트
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            // 날짜/시간 유효성 검사
            if (!validateDateTime()) {
                return;
            }

            // 전화번호 조합
            try {
                const phoneNumber1 = document.getElementById('phoneNumber1').value;
                document.getElementById('phoneNumber').value = 
                  `${phoneNumber1}-${phoneNumber2.value}-${phoneNumber3.value}`;

                // 방문 일자와 시간 합치기
                const selectedDate = preferredDate.value;
                const selectedTime = preferredTime.value;
                
                
                if (selectedDate && selectedTime) {
                  // datetime 형식으로 정확히 합치기 (YYYY-MM-DD HH:mm:ss)
                  const combinedDateTime = `${selectedDate} ${selectedTime}`;
                  document.getElementById('preferredDateTime').value = combinedDateTime;
                  
                } else {
                    // 날짜나 시간이 선택되지 않았을 경우 처리
                    alert('방문 가능 일자와 시간을 모두 선택해주세요.');
                    event.preventDefault(); // 폼 제출 방지
                }
                
                // 모든 검증 통과 시 폼 제출
                this.submit();
            } catch (error) {
                console.error('전화번호 처리 중 오류:', error);
                alert('전화번호 정보를 확인해주세요.');
            }
        });

        // 초기화 버튼 이벤트
        const resetBtn = document.querySelector('.reset-btn');
        if (resetBtn) {
            resetBtn.addEventListener('click', function() {
                setTimeout(() => {
                    form.reset();
                    charCount.textContent = '0/2000';
                    privacyCheckbox.checked = false;
                }, 0);
            });
        }
    });