$(document).ready(() => {
    function initialize() {
        console.log("Initialize : Disaster JS")
        loadDisasterMessage();
    }

    function loadDisasterMessage() {
        try {
            $.ajax( {
                url: '/sh_api/disaster',
                type: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                success : (data) => {
                    console.log(data);
                }, 
                error : (error) => {
                    console.error("[ERROR] loadDisasterMessage - AJAX", error);
                }
            })
        } catch (error) {
            console.error("[ERROR] loadDisasterMessage - TRY CATCH" + error)
        }
    }

    initialize();
});