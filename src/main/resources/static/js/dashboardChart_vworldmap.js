$(document).ready(() => {
    function initialize() {

        loadVWorldMap();
    }

    function loadVWorldMap() {
        try {
            let map;
            map = new vw.Map();
            let options = {
                mapId: "vworldMap_ny", // 지도맵 컨테이너 아이디
                initPosition: new vw.CameraPosition( // 초기 위치
                    new vw.CoordZ(127.921883, 36.024370, 750000),
                    new vw.Direction(0, -90, 0)
                ),
                logo: true, // 하단 로고 설정. true : 표출, false : 미표출
                navigation: true // 오른쪽 상단 네비게이션 설정. true : 표출, false : 미표출
            };
            map.setOption(options); //3D지도 초기 옵션 설정
            map.start(); // 지도 생성

            // 지도 로드 완료 이벤트 처리
            vw.ws3dInitCallBack = () => {
                console.log('Map loaded successfully');
                // 내부 WebGL 관련 요소 확인
                // console.log(map);
                // console.log(map._wsmap);

                map.getLayerElement('poi_base').hide();
                map.getLayerElement('poi_bound').hide();
                map.getLayerElement('hybrid_bound').hide();
                map.getLayerElement('facility_build').hide();
                loadGeoJsonLayer('lt_c_adsido_info', map);

            }
        } catch (error) {
            console.error("[ERROR] loadVWorldMap : ", error);
        } finally {
            console.log('[FINALLY] loadVWorldMap : finally');
        }

    }

    function loadGeoJsonLayer(dataType, map) {
        let url = `http://localhost:8080/sh_api/vworldWFS?TYPENAME=${dataType}`;

        // GMLParser 생성
        let parser = new vw.GMLParser();

        // 객체 고유 ID 설정
        // Feature 객체에도 아이디가 부여됩니다.
        parser.setId(`${dataType}_data`);

        // GeoJSON 데이터를 파싱
        // data 읽기. parser.read( 데이터타입, 데이터경로, 데이터좌표계)
        // 전달되는 좌표계를 의미하며, 이 좌표를 웹지엘에서는 EPSG:4326으로 변환하여 사용합니다.
        // 데이터타입. vw.GMLParserType { GEOJSON, GML1, GML2, GML2 } 
        let featureInfos = parser.read(vw.GMLParserType.GEOJSON, url, "EPSG:4326");

        // 옵션 설정
        let options = {
            // 지형 따라 출력시 true, 지면에서 위로 출력시 false
            isTerrain: false,
            // 선의 경우 크기지정.
            width: 50,
            // RGBA A값만 255이하로 주면 투명 또는 withAlpha(1.0~0.1)로 설정.
            material: new vw.Color(255, 255, 0, 255).ws3dColor.withAlpha(0.2),
            // 아웃라인지정시 true, 아웃라인 미지정 false
            outline: true,
            // 아웃라인 너비. 
            outlineWidth: 1,
            // 아웃라인 색상. 
            outlineColor: vw.Color.YELLOW.ws3dColor,
            // 높이 지정값 meter.
            height: 1600.0
        };

        // 출력 색상등 지정.
        let activeOptions = {
            // 지형 따라 출력시 true, 지면에서 위로 출력시 false
            isTerrain: false,
            // 선의 경우 크기지정.
            width: 50,
            // RGBA A값만 255이하로 주면 투명 또는 withAlpha(1.0~0.1)로 설정.
            material: new vw.Color(255, 167, 36, 255).ws3dColor.withAlpha(0.5),
            // 아웃라인지정시 true, 아웃라인 미지정 false
            outline: true,
            // 아웃라인 너비. 
            outlineWidth: 1,
            // 아웃라인 색상. 
            outlineColor: vw.Color.YELLOW.ws3dColor,
            // outlineColor: new vw.Color(255, 167, 36, 255).ws3dColor,
            // 높이 지정값 meter.
            height: 1600.0
        };

        // Point의 경우 이미지 설정. options 항목이 필요없음.
        //featureInfos.setImage("https://map.vworld.kr/images/comm/symbol_05.png");
        featureInfos.setOption(options);

        // 좌표 데이터 생성
        // featureInfos.makeCoords();
        const promise = new Promise((resolve, reject) => {
            featureInfos.makeCoords();
            // resolve('[PROMISE] resolve - makeCoords');
        });

        promise.then((value) => {
            console.log(value);

            let result = "";
            // 데이터 출력 (콘솔 로그로 확인 가능)
            // objCollection.collectionProp 객체의 속성 값을 가지고 있음
            featureInfos.objCollection.collectionProp.forEach(function (item) {
                // result += i.properties.full_nm + " "
                // $("#features").html(result);
                // console.log("Feature Name:", item.properties.ctp_kor_nm);
                console.log("Feature Properties:", item);
            })
        });

        // 클릭 이벤트 핸들러 정의
        let featureEventHandler = function (windowPosition, ecefPosition, cartographic, featureInfo) {
            // console.log(windowPosition)
            // console.log(ecefPosition)
            // console.log(cartographic)
            // console.log(featureInfo)
            if (featureInfo) {
                let feature = featureInfos.getById(featureInfo.groupId);
                console.log("You clicked on: " + feature.getProperties().ctp_kor_nm);
                console.log("Clicked Feature:", feature.id);
                // let featureOptions = feature.getOptions();
                // console.log(featureOptions);
                
                let objectCollection = featureInfos.objCollection.collectionProp;
                console.log(objectCollection);
                objectCollection.forEach((item) =>{ 
                    if (item.id != feature.id) {
                        // console.log(item.id);
                        // featureInfos.getById(item.id).setOptions(options);
                        item.setOptions(options);
                        item.show();
                    }
                });

                
                // 사용자 지정 코드 실행
                // 피처 숨기기 (필요 시)
                // feature.hide();
                // 옵션 설정
                feature.setOptions(activeOptions);
                feature.show();
                loadFireInfoSido(feature.getProperties().ctp_kor_nm);
            }
        };

        // 이벤트 리스너 추가
        featureInfos.addEventListener(featureEventHandler);

        // 지도에 레이어 출력
        featureInfos.show();
        console.log("GeoJSON layer added to the map.");
    }

    // 화재 현황 정보 조회
    function loadFireInfoSido(targetName) {
        const $fireInfo = $(".fireInfo");
        const $fireCasualty = $(".fireCasualty");
        const $fireDamage = $(".fireDamage");

        $fireInfo.empty();
        $fireCasualty.empty();
        $fireDamage.empty();

        if (targetName == "강원특별자치도") {
            targetName = "강원도";
        } else if (targetName == "전북특별자치도") {
            targetName = "전라북도";
        }

        Promise.all([
            fetchFireInfoSido(),
            fetchFireInfoSidoCasualty(),
            fetchFireInfoSidoDamage()])
            .then(([infoData, casualtyData, damageData]) => {

                if (infoData && Array.isArray(infoData) && infoData.length > 0) {
                    infoData.forEach((data) => {
                        if (data.sido_nm == targetName) {
                            const dataElement = `
                            <div class="sidoData">
                                <div class="sidoTitle">화재 현황 (${data.ocrn_ymd || "정보 없음"})</div>
                                <div class="sidoName">${data.sido_nm || "정보 없음"}</div>
                                <div>화재접수 : ${data.fire_rcpt_mnb || "0"} 건</div>
                                <div>상황종료 : ${data.stn_end_mnb || "0"} 건</div>
                                <div>자체진화 : ${data.slf_extsh_mnb || "0"} 건</div>
                                <div>오보처리 : ${data.flsrp_prcs_mnb || "0"} 건</div>
                                <div>허위신고 : ${data.fals_dclr_mnb || "0"} 건</div>
                            </div>
                            `;
                            $fireInfo.append(dataElement);
                        }
                    });
                }

                if (casualtyData && Array.isArray(casualtyData) && casualtyData.length > 0) {
                    const groupedCasualty = groupBySido(casualtyData, ["vctmPercnt", "injrdprPercnt", "lifeDmgPercnt", "ocrnMnb"]);

                    let today = new Date();
                    let weekStart = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 7).toLocaleDateString().replace(" ", "").replace(" ", "");
                    let weekEnd = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 1).toLocaleDateString().replace(" ", "").replace(" ", "");
                    for (const sido in groupedCasualty) {
                        if (sido == targetName) {
                            const data = groupedCasualty[sido];
                            const dataElement = `
                            <div class="sidoData">
                                <div class="sidoTitle">주간 피해 현황 (${weekStart} ~ ${weekEnd})</div>
                                <div class="sidoName">${sido || "정보 없음"}</div>
                                <div>화재건수 : ${data.ocrnMnb || "0"} 건</div>
                                <div>사망자 : ${data.vctmPercnt || "0"} 명</div>
                                <div>부상자 : ${data.injrdprPercnt || "0"} 명</div>
                                <div>인명피해 : ${data.lifeDmgPercnt || "0"} 명</div>
                            </div>
                            `;
                            $fireCasualty.append(dataElement);
                        }
                    }
                }

                if (damageData && Array.isArray(damageData) && damageData.length > 0) {
                    const groupedDamage = groupBySido(damageData, ["prptDmgSbttAmt", "ocrnMnb"]);
                    for (const sido in groupedDamage) {
                        if (sido == targetName) {
                            const data = groupedDamage[sido];
                            const propertyDamage = parseInt(data.prptDmgSbttAmt + "000").toLocaleString() || "0";
                            const dataElement = `
                            <div class="sidoData">
                                <div>재산피해 : ${propertyDamage} 원</div>
                            </div>
                            `;
                            $fireDamage.append(dataElement);
                        }
                    }
                }

            }).catch((error) => {
                console.error("[ERROR] loadFireInfoSido / Faile dto load fire information", error);
                $fireInfo.append("<p>화재정보를 불러오는데 실패했습니다.</P>");
                $fireCasualty.append("<p>인명피해 정보를 불러오는데 실패했습니다.</P>");
                $fireDamage.append("<p>재산피해 정보를 불러오는데 실패했습니다.</P>");
            }).finally(() => {
                // console.log("[FINALLY] loadFireInfoSido / Fire information loaded successfully.");
                if ($fireInfo.children().length == 0) {
                    $fireInfo.append(`
                            <div class="sidoData">
                                <div class="sidoTitle">화재 현황</div>
                                <div class="sidoName">${targetName || "정보 없음"}</div>
                                <p>해당 지역의 화재정보가 없습니다.</p>
                            </div>
                            `);
                }
                if ($fireCasualty.children().length == 0) {
                    $fireCasualty.append(`
                            <div class="sidoData">
                                <div class="sidoTitle">화재 현황</div>
                                <div class="sidoName">${targetName || "정보 없음"}</div>
                                <p>해당 지역의 인명피해 정보가 없습니다.</p>
                            </div>
                            `);
                }
                if ($fireDamage.children().length == 0) {
                    $fireDamage.append("<p>해당 지역의 재산피해 정보가 없습니다.</P>");
                }

                // Canvas 테스트 코드
                // let canvas = document.querySelector("canvas");
                // let gl = canvas.getContext("webgl");
                // console.log(gl);
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

    // 레이어 제어 테스트 코드
    // let checkLayer = function (c, name) { //레이어 제어
    //     console.log($(c).is(":checked"));
    //     if ($(c.currentTarget).is(":checked") == false) {
    //         map.getLayerElement(name).hide() //레이어 숨김
    //     } else {
    //         map.getLayerElement(name).show() // 레이어 보여주기
    //     }
    // }

    // 레이어 제어 테스트 이벤트 핸들러
    // function eventHandler() {
    //     let $faclityButton = $(document).find('.facilityButton')
    //     $faclityButton.on('click', (e, f) => {
    //         checkLayer(e, 'facility_build');
    //     });
    //     let $cityNameButton = $(document).find('.cityNameButton')
    //     $cityNameButton.on('click', (e, f) => {
    //         checkLayer(e, 'poi_bound');
    //     });
    //     let $cityBoundButton = $(document).find('.cityBoundButton')
    //     $cityBoundButton.on('click', (e, f) => {
    //         checkLayer(e, 'hybrid_bound');
    //     });
    //     let $geoJsonButton = $(document).find('.geoJsonButton')
    //     $geoJsonButton.on('click', (e) => {
    //         geojson();
    //     });
    // }

    // 멀티 폴리곤 수동 생성 테스트 코드 (작동안함)
    // function createLayerFromFeatures(featureCollection) {
    //     // 반환된 FeatureCollection에서 features를 가져옵니다.
    //     const features = featureCollection.features;

    //     features.forEach((feature, index) => {
    //         const geometryType = feature.geometry.type; // Geometry 유형 (Point, LineString, Polygon 등)
    //         const coordinates = feature.geometry.coordinates; // 좌표 데이터
    //         console.log(feature)
    //         console.log(index)
    //         if (geometryType === 'LineString') {
    //             // LineString 좌표 배열 생성
    //             const lineCoords = coordinates.map(coord => new vw.Coord(coord[0], coord[1]));
    //             const lineCollection = new vw.Collection(lineCoords);

    //             // LineString 객체 생성 및 스타일 설정
    //             const line = new vw.geom.LineString(lineCollection);
    //             line.setFillColor(vw.Color.BLUE); // 파란색
    //             line.setWidth(2); // 너비
    //             line.setName(`라인 ${index + 1}`);
    //             line.create();

    //         } else if (geometryType === 'Polygon') {
    //             // Polygon 좌표 배열 생성 (첫 번째 외곽선만 처리)
    //             const polygonCoords = coordinates[0].map(coord => new vw.Coord(coord[0], coord[1]));
    //             const polygonCollection = new vw.Collection(polygonCoords);

    //             // Polygon 객체 생성 및 스타일 설정
    //             const polygon = new vw.geom.Polygon(polygonCollection);
    //             polygon.setFillColor(new vw.Color(255, 0, 0, 70)); // 반투명 빨간색
    //             polygon.setWidth(1); // 테두리 두께
    //             polygon.setName(`폴리곤 ${index + 1}`);
    //             polygon.create();
    //         } else if (geometryType === 'MultiPolygon') {
    //             const multiPolygonCoords = new Array();

    //             // MultiPolygon의 각 폴리곤을 순회
    //             coordinates.forEach(polygon => {
    //                 const exteriorRing = polygon[0].map(coord => new vw.Coord(coord[0], coord[1])); // 외곽선
    //                 const interiorRings = polygon.slice(1).map(ring =>
    //                     ring.map(coord => new vw.Coord(coord[0], coord[1]))
    //                 ); // 내부 링들

    //                 const exteriorCollection = new vw.Collection(exteriorRing); // 외곽선 좌표 컬렉션
    //                 const interiorCollections = interiorRings.map(
    //                     ring => new vw.Collection(ring)
    //                 ); // 내부 링 컬렉션들

    //                 multiPolygonCoords.push({ exterior: exteriorCollection, interiors: interiorCollections });
    //             });

    //             // MultiPolygon 생성 및 스타일 설정
    //             const multiPolygon = new vw.geom.MultiPolygon(multiPolygonCoords);
    //             multiPolygon.setFillColor(new vw.Color(0, 255, 0, 70)); // 반투명 초록색
    //             multiPolygon.setWidth(2); // 테두리 두께
    //             multiPolygon.setName(`멀티폴리곤 ${index + 1}`);
    //             // multiPolygon.create();
    //             // multiPolygon.makeCoords();
    //         }

    //         // 추가적인 Geometry 유형(Point, MultiPolygon 등)을 처리하려면 여기에 구현
    //     });
    // }
});