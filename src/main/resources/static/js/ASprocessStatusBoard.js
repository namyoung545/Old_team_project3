// 모달 박스 열기: 선택된 접수번호를 인자로 받음
function openModal(button) {
    let requestId = button.getAttribute("data-request-id");
    let name = button.getAttribute("data-name");

    document.getElementById('selectedRequestId').value = requestId;
    document.getElementById('displayRequestId').innerText = requestId;
    document.getElementById('displayRequestName').innerText = name;
    document.getElementById('modalOverlay').style.display = 'flex';
}

// 모달 박스 닫기
function closeModal() {
	document.getElementById('modalOverlay').style.display = 'none';
}