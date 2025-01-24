$(document).ready(() => {
    function initialize() {
        console.log("Initialize : Disaster JS")
        loadDisasterMessage();
    }

    function loadDisasterMessage() {
        const $disasterMessage = $(".disasterMessage");
        $disasterMessage.empty();

        getDisasterMessage().then((messages) => {
            if (messages && Array.isArray(messages) && messages.length > 0) {
                messages.forEach((message) => {
                    const messageElement = `
                    <div class="message">
                        <p>${message.crt_dt || "시간정보 없음"}</p>
                        <p>${message.msg_cn || "메시지 없음"}</p>
                    </div>
                   `;
                    $disasterMessage.append(messageElement);
                });
            } else {
                $disasterMessage.append('<p>표시할 메시지가 없습니다.</p>');
            }
        }).catch((error) => {
            console.error("[ERROR] Failed to load disaster messages : ", error);
            $disasterMessage.append('<p>메시지를 불러오는 데 실패했습니다.</p>');
        });
    }

    function getDisasterMessage() {
        return new Promise((resolve, reject) => {
            try {
                $.ajax({
                    url: '/sh_api/disasterMessage',
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    success: (data) => {
                        console.log(data);
                        resolve(data);
                    },
                    error: (error) => {
                        console.error("[ERROR] loadDisasterMessage - AJAX", error);
                        reject(error);
                    }
                })
            } catch (error) {
                console.error("[ERROR] loadDisasterMessage - TRY CATCH" + error)
                reject(error);
            }
        });
    }

    initialize();
});