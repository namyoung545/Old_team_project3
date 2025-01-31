$(document).ready(() => {
    function initialize() {
        console.log("Initialize - Fire Information JS")
        loadFireInfoSido();
    }

    function loadFireInfoSido() {
        const $fireInfo = $(".fireInfo");
        console.log($fireInfo)
        $fireInfo.empty();

        fetchFireInfoSido().then((data) => {
            if(data && Array.isArray(data) && data.length > 0) {
                data.forEach((data) => {
                    const dataElement = `
                    <div class="sidoData">
                        <div>${data.sido_nm || "정보 없음"} ${data.ocrn_ymd || "정보 없음"}</div>
                        <div>화재접수 : ${data.fire_rcpt_mnb || "0"}</div>
                        <div>상황종료 : ${data.stn_end_mnb || "0"}</div>
                        <div>자체진화 : ${data.slf_extsh_mnb || "0"}</div>
                        <div>오보처리 : ${data.flsrp_prcs_mnb || "0"}</div>
                        <div>허위신고 : ${data.fals_dclr_mnb || "0"}</div>
                    </div>
                    `;
                    $fireInfo.append(dataElement);
                });
            }
        }).catch((error) => {
            console.error("[ERROR] loadFireInfoSido / Faile dto load fire information", error);
            $fireInfo.append("<p>정보를 불러오는데 실패했습니다.</P>");
        });
    }

    function fetchFireInfoSido() {
        return new Promise((resolve, reject) => {
            try {
                $.ajax({
                    url: '/sh_api/fireInformation',
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    success: (data) => {
                        console.log(data);
                        resolve(data);
                    },
                    error: (error) => {
                        console.error("[ERROR] fetchFireInfoSido / AJAX", error);
                        reject(error);
                    }
                })
            } catch (error) {
                console.error("[ERROR] fetchFireInfoSido / Try Catch", error);
                reject(error);
            }
        });
    }

    initialize();
});