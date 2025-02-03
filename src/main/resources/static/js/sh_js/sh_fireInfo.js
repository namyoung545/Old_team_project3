$(document).ready(() => {
    function initialize() {
        console.log("Initialize - Fire Information JS")
        loadFireInfoSido();
    }

    function loadFireInfoSido() {
        const $fireInfo = $(".fireInfo");
        const $fireCasualty = $(".fireCasualty");
        const $fireDamage = $(".fireDamage");

        $fireInfo.empty();
        $fireCasualty.empty();
        $fireDamage.empty();

        Promise.all([
            fetchFireInfoSido(),
            fetchFireInfoSidoCasualty(),
            fetchFireInfoSidoDamage()])
            .then(([infoData, casualtyData, damageData]) => {
                if (infoData && Array.isArray(infoData) && infoData.length > 0) {
                    infoData.forEach((data) => {
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

                if (casualtyData && Array.isArray(casualtyData) && casualtyData.length > 0) {
                    const groupedCasualty = groupBySido(casualtyData, ["vctmPercnt", "injrdprPercnt", "lifeDmgPercnt", "ocrnMnb"]);
                    console.log(groupedCasualty);
                    for (const sido in groupedCasualty) {
                        const data = groupedCasualty[sido];
                        const dataElement = `
                            <div class="sidoData">
                                <div>${sido || "정보 없음"}</div>
                                <div>화재건수 : ${data.ocrnMnb || "0"}</div>
                                <div>사망자 : ${data.vctmPercnt || "0"}</div>
                                <div>부상자 : ${data.injrdprPercnt || "0"}</div>
                                <div>인명피해 : ${data.lifeDmgPercnt || "0"}</div>
                            </div>
                            `;
                        $fireCasualty.append(dataElement);
                    }

                }

                if (damageData && Array.isArray(damageData) && damageData.length > 0) {
                    const groupedDamage = groupBySido(damageData, ["prptDmgSbttAmt", "ocrnMnb"]);

                    for (const sido in groupedDamage) {
                        const data = groupedDamage[sido];
                        const dataElement = `
                            <div class="sidoData">
                                <div>${sido || "정보 없음"}</div>
                                <div>화재건수 : ${data.ocrnMnb || "0"}</div>
                                <div>재산피해 : ${data.prptDmgSbttAmt || "0"}</div>
                            </div>
                            `;
                        $fireDamage.append(dataElement);
                    }
                }
            }).catch((error) => {
                console.error("[ERROR] loadFireInfoSido / Faile dto load fire information", error);
                $fireInfo.append("<p>정보를 불러오는데 실패했습니다.</P>");
            });

    }

    // 데이터를 sidoNm 기준으로 그룹화하고 필드값을 합산하는 함수
    function groupBySido(data, fields) {
        return data.reduce((acc, item) => {
            const sido = item.sidoNm || item.sido_nm || "알 수 없음";
            if (!acc[sido]) {
                acc[sido] = fields.reduce((obj, field) => {
                    obj[field] = 0;
                    return obj;
                }, {});
            }
            fields.forEach(field => {
                acc[sido][field] += item[field] || 0;
            });
            return acc;
        }, {});
    }

    function fetchFireInfoSido() {
        return new Promise((resolve, reject) => {
            try {
                $.ajax({
                    url: '/sh_api/fireInfo',
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    success: (data) => {
                        // console.log(data);
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

    function fetchFireInfoSidoCasualty() {
        return new Promise((resolve, reject) => {
            try {
                $.ajax({
                    url: '/sh_api/fireInfoCasualty',
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    success: (data) => {
                        // console.log(data);
                        resolve(data);
                    },
                    error: (error) => {
                        console.error("[ERROR] fetchFireInfoSidoCasualty / AJAX", error);
                        reject(error);
                    }
                })
            } catch (error) {
                console.error("[ERROR] fetchFireInfoSidoCasualty / Try Catch", error);
                reject(error);
            }
        });
    }

    function fetchFireInfoSidoDamage() {
        return new Promise((resolve, reject) => {
            try {
                $.ajax({
                    url: '/sh_api/fireInfoDamage',
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    success: (data) => {
                        // console.log(data);
                        resolve(data);
                    },
                    error: (error) => {
                        console.error("[ERROR] fetchFireInfoSidoDamage / AJAX", error);
                        reject(error);
                    }
                })
            } catch (error) {
                console.error("[ERROR] fetchFireInfoSidoDamage / Try Catch", error);
                reject(error);
            }
        });
    }

    initialize();
});