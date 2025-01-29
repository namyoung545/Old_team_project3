$(document).ready(() => {
    function initialize() {
        console.log("Initialize - Fire Information JS")
        loadFireInfoSido();
    }

    function loadFireInfoSido() {
        const $fireInfo = $(".fireInfo");
        console.log($fireInfo)
        // $fireInfo.emtpy();

        fetchFireInfoSido().then((message) => {
            console.log(message);
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