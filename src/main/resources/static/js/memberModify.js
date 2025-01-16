function showContent(contentId) {
    document.querySelectorAll('.content_container').forEach(content => {
        content.style.display = 'none';
    });
    document.getElementById(contentId).style.display = 'block';
    console.log("contentId:",contentId);
}

function memberDelete(){
    if(confirm('정말로 탈퇴하시겠습니까?')){
        $.ajax({
            url: '/remove',
            type: 'POST',
            data:{
                userId: '${sessionScope.userId}'
            },
            success: function(response) {
                alert('회원 탈퇴가 완료되었습니다.');
                window.location.href = '/';
            },
            //실패시
            error: function(xhr, status, error) {
                alert('회원 탈퇴 처리 중 오류가 발생했습니다.');
                console.error(error);
            }
        });
    }
}

function validatePassword() {
    // 새 비밀번호와 새 비밀번호 확인 필드의 값을 가져옴
    var newPw = document.getElementById('userNewPw').value;
    var newPwCheck = document.getElementById('userNewPwCheck').value;
    
    // 만약 둘 다 값이 입력되었다면 비교 (빈 값인 경우, 비밀번호 변경이 없다고 판단할 수 있음)
    if(newPw !== "" || newPwCheck !== "") {
        if (newPw !== newPwCheck) {
            alert("새 비밀번호와 확인이 일치하지 않습니다.");
            return false; // 제출 중지
        }
    }
    
    // 모두 일치하면 제출 진행
    return true;
}

// document.addEventListener("DOMContentLoaded", () => {
//     const previousHeaderState = sessionStorage.getItem("previousHeaderState");
//     if (previousHeaderState) {
//         updateHeader("default");
//         updateHeaderByTargetId(previousHeaderState);
//         sessionStorage.removeItem("previousHeaderState"); // 일회성 동작
//         console.log("previousHeaderState:",previousHeaderState);

//         // 페이지 새로고침
//         location.reload();
//     }
// });
