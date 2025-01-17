// h1 요소 선택
const headerText = document.querySelectorAll('.headerText');
console.log("headerText:",headerText);

// 클릭 이벤트 추가
headerText.forEach((element) => {
    element.addEventListener('click', () => {
        // URL 이동
        window.location.href = '/'; // 이동할 URL 설정
    });
});

// 마우스커서 변환 이벤트 추가
headerText.forEach((element) => {
    element.addEventListener('mouseover', () => {
        // 마우스커서 모양 변경
        element.style.cursor = 'pointer';
    });
});

// // h1 요소 선택
// const headerMemberModify = document.querySelectorAll('.header_link_memberModify');

// // 클릭 이벤트 추가
// function updateHeader_memberModify(){
//     headerMemberModify.forEach((element) => {
//         element.addEventListener('click', (event) => {
//             const targetId = element.id;

//             // 상태 저장
//             sessionStorage.setItem("previousHeaderState", targetId);

//             // URL 이동
//             window.location.href = '/memberModify'; // 이동할 URL 설정
//         });
//     });
// };

// 페이지 상태에 따라 표시할 헤더 설정
function updateHeader(state) {
    console.log("Updating header state:", state);
    // 초기화
    defaultHeader.style.display = "none";
    defaultHeader.style.height = "0";
    defaultHeader.style.padding = "0";
    defaultHeader.style.margin = "0";
    boardIndexHeader.style.display = "none";
    boardIndexHeader.style.height = "0";
    boardIndexHeader.style.padding = "0";
    boardIndexHeader.style.margin = "0";

    if (state === "default") {
        console.log("default")
        // defaultHeader.style.display = "grid";
        // boardIndexHeader.style.display = "none";
        defaultHeader.style.display = "grid";
        defaultHeader.style.height = "auto";
        defaultHeader.style.padding = "";
        defaultHeader.style.margin = "";
    } else if (state === "boardIndex") {
        console.log("boardIndex")
        // defaultHeader.style.display = "none";
        boardIndexHeader.style.display = "grid";
        boardIndexHeader.style.gridTemplateColumns = "1fr 2fr 1fr";
        boardIndexHeader.style.gridTemplateRows = "auto";
        boardIndexHeader.style.gap = "10px";
        headerText_container.style.gridColumn = "1";
        headerText_container.style.fontSize = "0.7em";
        header_link_container.style.gridColumn = "2";
        header_link_container.style.textAlign = "center";
        header_link_container.style.alignContent = "center";
        headerboardIndex_container.style.gridColumn = "3";
        headerboardIndex_container.style.textAlign = "right";
        headerboardIndex_container.style.alignContent = "center";
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const mainContent = document.getElementById("mainContent");
    const menuLinks = document.querySelectorAll("a[id^='menu']");
    console.log("menuLinks:",menuLinks);

    if (!mainContent || menuLinks.length === 0) {
        console.error("mainContent 또는 메뉴 링크 요소를 찾을 수 없습니다!");
        return;
    }

    // 메뉴 링크 클릭 이벤트 위임
    menuLinks.forEach(link => {
        link.addEventListener("click", async (event) => {
            const targetId = event.currentTarget.id;
            console.log("targetId:",targetId);
          
            event.preventDefault();

            let targetUrl = "";

            // ID에 따라 로드할 URL 결정
            switch (targetId) {
                case "menuBoardIndexLink":
                    targetUrl = "/managementPage/boardIndex";
                    break;
                case "menuCalendarLink":
                    targetUrl = "/managementPage/fullCalendar";
                    break;
                case "menuAsRegistLink":
                    targetUrl = "/managementPage/registAS";
                    break;
                case "menuASprocessStatusLink":
                    targetUrl = "/managementPage/ASprocessStatus";
                    break;
                default:
                    console.error("알 수 없는 링크입니다.");
                    return;
            }

            try {
                // 비동기 로드
                const response = await fetch(targetUrl);
                if (!response.ok) {
                    throw new Error(`HTML Load Error! status: ${response.status}`);
                }

                const html = await response.text();
                mainContent.innerHTML = html;

                // 상태에 따라 header 업데이트
                updateHeaderByTargetId(targetId);
            } catch (error) {
                console.error("Error loading content or events:", error);
            }
        });
    });
});

// Header 상태 업데이트 함수
function updateHeaderByTargetId(targetId) {
    if (targetId === "menuBoardIndexLink") {
        console.log("menuBoardIndexLink")
        updateHeader("boardIndex");
    } else if (targetId === "menuCalendarLink") {
        console.log("menuCalendarLink")
        updateHeader("boardIndex");
    } else if (targetId === "menuAsRegistLink") {
        console.log("menuAsRegistLink")
        updateHeader("boardIndex");
    } else if (targetId === "menuASprocessStatusLink") {
        console.log("menuASprocessStatusLink")
        updateHeader("boardIndex");
    } else {
        console.log("테스트용");
        updateHeader("default");
    }
}

// document.addEventListener("DOMContentLoaded", () => {
//     const userId = sessionStorage.getItem("userId"); // 예: 세션에서 사용자 ID 확인
//     console.log("userId:",userId);  
//     if (userId !== null) {
//         updateHeader("default"); // 기본 헤더로 설정
//         console.log("확인용")
//     }
// });


