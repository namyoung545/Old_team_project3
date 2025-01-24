$(document).ready(() => {
    console.log('dashboardChart_vworldmap.js');
    let map;

    function initialize() {
        loadVWorldMap();
    }

    function eventHandler() {
        let $faclityButton = $(document).find('.facilityButton')
        $faclityButton.on('click', (e, f) => {
            checkLayer(e, 'facility_build');
        });
        let $cityNameButton = $(document).find('.cityNameButton')
        $cityNameButton.on('click', (e, f) => {
            checkLayer(e, 'poi_bound');
        });
        let $cityBoundButton = $(document).find('.cityBoundButton')
        $cityBoundButton.on('click', (e, f) => {
            checkLayer(e, 'hybrid_bound');
        });
        let $geoJsonButton = $(document).find('.geoJsonButton')
        $geoJsonButton.on('click', (e) => {
            geojson();
        });
    }

    function loadVWorldMap() {
        Promise.all([
            // getVWorldWFSData('lt_c_adsido_info')
        ]).then(([wfsAdsigg]) => {

            map = new vw.Map();
            let options = {
                mapId: "vworldMap_ny", // 지도맵 컨테이너 아이디
                initPosition: new vw.CameraPosition( // 초기 위치
                    new vw.CoordZ(127.921883, 36.024370, 750000),
                    new vw.Direction(0, -90, 0)
                ),
                logo: false, // 하단 로고 설정. true : 표출, false : 미표출
                navigation: false // 오른쪽 상단 네비게이션 설정. true : 표출, false : 미표출
            };
            map.setOption(options); //3D지도 초기 옵션 설정
            map.start(); // 지도 생성

            // 지도 로드 완료 이벤트 처리
            vw.ws3dInitCallBack = () => {
                console.log('Map loaded successfully');
                map.getLayerElement('poi_base').hide();
                map.getLayerElement('poi_bound').hide();
                map.getLayerElement('hybrid_bound').hide();
                map.getLayerElement('facility_build').hide();
                loadGeoJsonLayer('lt_c_adsido_info');
                // getVWorldWFSData('lt_c_adsido_info').then(
                //     (data) => {
                //         console.log('Promise - then')
                //         console.log(data)
                //         createLayerFromFeatures(data)
                //     }
                // );
            }
        }).catch((error) => {
            console.error("[ERROR] loadVWorldMap : ", error);
        }).finally(() => {
            console.log('[SUCCESS] loadVWorldMap : finally');
        });
    }

    function loadGeoJsonLayer(dataType) {
        let url = `http://localhost:8080/sh_api/vworldWFS?TYPENAME=${dataType}`;

        // GMLParser 생성
        var parser = new vw.GMLParser();
        parser.setId(`${dataType}_data`); // 고유 ID 설정

        // GeoJSON 데이터를 파싱
        var featureInfos = parser.read(vw.GMLParserType.GEOJSON, url, "EPSG:4326");

        // 옵션 설정
        var options = {
            isTerrain: false,
            width: 50,
            material: new vw.Color(255, 255, 0, 255).ws3dColor.withAlpha(0.2),
            outline: true,
            outlineWidth: 1,
            outlineColor: vw.Color.YELLOW.ws3dColor,
            height: 1600.0
        };
        featureInfos.setOption(options);

        // 좌표 데이터 생성
        featureInfos.makeCoords();

        // 데이터 출력 (콘솔 로그로 확인 가능)
        featureInfos.objCollection.collectionProp.forEach(function (item) {
            console.log("Feature Name:", item.properties.ctp_kor_nm);
        });

        // 클릭 이벤트 핸들러 정의
        var featureEventHandler = function (windowPosition, ecefPosition, cartographic, featureInfo) {
            console.log(windowPosition)
            console.log(ecefPosition)
            console.log(cartographic)
            console.log(featureInfo)
            if (featureInfo) {
                var feature = featureInfos.getById(featureInfo.groupId);
                console.log("Clicked Feature Properties:", feature.getProperties());

                // 사용자 지정 코드 실행
                console.log("You clicked on: " + feature.getProperties().ctp_kor_nm);

                // 피처 숨기기 (필요 시)
                // feature.hide();
            }
        };

        // 이벤트 리스너 추가
        featureInfos.addEventListener(featureEventHandler);

        // 지도에 레이어 출력
        featureInfos.show();
        console.log("GeoJSON layer added to the map.");
    }

    function getVWorldWFSData(dataType) {
        return new Promise((resolve, reject) => {
            $.ajax({
                // url: 'http://localhost:8080/sh_api/vworldWFS?key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&SERVICE=WFS&version=2.0.0&request=GetFeature&TYPENAME=lt_c_adsigg&OUTPUT=application/json&SRSNAME=EPSG:4326&domain=localhost',
                url: 'http://localhost:8080/sh_api/vworldWFS',
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify({
                    key: '2D9BDADB-D581-3DB0-991E-11E5985B77CC',
                    SERVICE: 'WFS',
                    VERSION: '1.1.0',
                    REQUEST: 'GetFeature',
                    OUTPUT: 'application/json',
                    SRSNAME: 'EPSG:4326',
                    DOMAIN: 'localhost',
                    TYPENAME: dataType,
                }),
                success: function (data) {
                    console.log("데이터 로드 성공:", data);
                    resolve(data)
                },
                error: function (error) {
                    console.error("데이터 로드 실패:", error);
                    reject(error);
                }
            });
        });
    }

    function createLayerFromFeatures(featureCollection) {
        // 반환된 FeatureCollection에서 features를 가져옵니다.
        const features = featureCollection.features;

        features.forEach((feature, index) => {
            const geometryType = feature.geometry.type; // Geometry 유형 (Point, LineString, Polygon 등)
            const coordinates = feature.geometry.coordinates; // 좌표 데이터
            console.log(feature)
            console.log(index)
            if (geometryType === 'LineString') {
                // LineString 좌표 배열 생성
                const lineCoords = coordinates.map(coord => new vw.Coord(coord[0], coord[1]));
                const lineCollection = new vw.Collection(lineCoords);

                // LineString 객체 생성 및 스타일 설정
                const line = new vw.geom.LineString(lineCollection);
                line.setFillColor(vw.Color.BLUE); // 파란색
                line.setWidth(2); // 너비
                line.setName(`라인 ${index + 1}`);
                line.create();

            } else if (geometryType === 'Polygon') {
                // Polygon 좌표 배열 생성 (첫 번째 외곽선만 처리)
                const polygonCoords = coordinates[0].map(coord => new vw.Coord(coord[0], coord[1]));
                const polygonCollection = new vw.Collection(polygonCoords);

                // Polygon 객체 생성 및 스타일 설정
                const polygon = new vw.geom.Polygon(polygonCollection);
                polygon.setFillColor(new vw.Color(255, 0, 0, 70)); // 반투명 빨간색
                polygon.setWidth(1); // 테두리 두께
                polygon.setName(`폴리곤 ${index + 1}`);
                polygon.create();
            } else if (geometryType === 'MultiPolygon') {
                const multiPolygonCoords = new Array();

                // MultiPolygon의 각 폴리곤을 순회
                coordinates.forEach(polygon => {
                    const exteriorRing = polygon[0].map(coord => new vw.Coord(coord[0], coord[1])); // 외곽선
                    const interiorRings = polygon.slice(1).map(ring =>
                        ring.map(coord => new vw.Coord(coord[0], coord[1]))
                    ); // 내부 링들

                    const exteriorCollection = new vw.Collection(exteriorRing); // 외곽선 좌표 컬렉션
                    const interiorCollections = interiorRings.map(
                        ring => new vw.Collection(ring)
                    ); // 내부 링 컬렉션들

                    multiPolygonCoords.push({ exterior: exteriorCollection, interiors: interiorCollections });
                });

                // MultiPolygon 생성 및 스타일 설정
                const multiPolygon = new vw.geom.MultiPolygon(multiPolygonCoords);
                multiPolygon.setFillColor(new vw.Color(0, 255, 0, 70)); // 반투명 초록색
                multiPolygon.setWidth(2); // 테두리 두께
                multiPolygon.setName(`멀티폴리곤 ${index + 1}`);
                // multiPolygon.create();
                // multiPolygon.makeCoords();
            }

            // 추가적인 Geometry 유형(Point, MultiPolygon 등)을 처리하려면 여기에 구현
        });
    }


    let checkLayer = function (c, name) { //레이어 제어
        console.log($(c).is(":checked"));
        if ($(c.currentTarget).is(":checked") == false) {
            map.getLayerElement(name).hide() //레이어 숨김
        } else {
            map.getLayerElement(name).show() // 레이어 보여주기
        }
    }

    initialize();

    // function callVWorldWFSData() {
    //     return new Promise((resolve, reject) => {

    //         // GeoJSON 데이터 가져오기
    //         $.ajax({
    //             url: 'http://localhost:8080/sh_api/vworldWFS?SERVICE=WFS&version=2.0.0&request=GetFeature&typeName=lt_c_adsido_info&output=json&key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&domain=localhost',
    //             type: 'POST',
    //             dataType: 'json',
    //             contentType: 'application/json',
    //             success: function (data) {
    //                 console.log("데이터 로드 성공:", data);
    //                 resolve(data)

    //             },
    //             error: function (error) {
    //                 console.error("데이터 로드 실패:", error);
    //                 reject(error);
    //             }
    //         });
    //     });
    // }

    // function geoJsonLayer() {
    // GeoJSON 데이터 가져오기
    // $.ajax({
    //     url: 'http://localhost:8080/sh_api/vworldWFS?service=WFS&version=2.0.0&request=GetFeature&typeName=lt_c_adsido_info,lt_c_adsido_3d&output=json&key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&domain=localhost',
    //     type: 'POST',
    //     dataType: 'json',
    //     success: function (data) {
    //         console.log("데이터 로드 성공:", data);

    //         // GeoJsonLayer 생성
    //         const geoJsonLayer = new vw.layer.GeoJsonLayer({
    //             geoJson: data,
    //             style: {
    //                 strokeColor: '#ff0000', // 경계선 색상
    //                 strokeWidth: 2, // 경계선 두께
    //                 fillColor: 'rgba(255, 0, 0, 0.1)' // 영역 채우기 색상 (투명도 포함)
    //             }
    //         });
    //         console.log(geoJsonLayer);
    //         // 레이어를 지도에 추가
    //         map.addLayer(geoJsonLayer);
    //     },
    //     error: function (error) {
    //         console.error("데이터 로드 실패:", error);
    //     }
    // });
    // }

    // function geojson() {

    //     // var url = "https://map.vworld.kr/proxy.do?url=http%3A%2F%2Fapi.vworld.kr%2Freq%2Fwfs%3Fkey%3D@{apikey}%26SERVICE%3DWFS%26version%3D1.1.0%26request%3DGetFeature%26TYPENAME%3Dlt_c_adsigg%26OUTPUT%3Dapplication%2Fjson%26SRSNAME%3DEPSG%3A4326%26maxfeatures%3D100%26domain%3Dmap.vworld.kr";
    //     //var url = "http://localhost:8082/example/web2/geojson_sample2.json"; // 로컬 파일 호출 시
    //     let url = "http://localhost:8080/sh_api/vworldWFS?key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&SERVICE=WFS&version=1.1.0&request=GetFeature&TYPENAME=lt_c_adsigg&OUTPUT=application/json&SRSNAME=EPSG:4326&maxfeatures=100&domain=localhost";

    //     // parser 설명.
    //     // parser 생성자.
    //     var parser = new vw.GMLParser();
    //     // 아이디 지정. --> parser에 지정이 되면, Feature객체에게도 상속 "sample-0,1,..." 형태로 아이디가 부여됩니다.
    //     parser.setId("sample");
    //     //var featureInfos = parser.readTemp(vw.GMLParserType.GEOJSON, url, "EPSG:4326");
    //     // data 읽기. parser.read( 데이터타입, 데이터경로, 데이터좌표계)
    //     // 전달되는 좌표계를 의미하며, 이 좌표를 웹지엘에서는 EPSG:4326으로 변환하여 사용합니다.
    //     // 데이터타입. vw.GMLParserType { GEOJSON, GML1, GML2, GML2 } 
    //     var featureInfos = parser.read(vw.GMLParserType.GEOJSON, url, "EPSG:4326");
    //     console.log(featureInfos);
    //     var options = {
    //         isTerrain: false,		// 지형 따라 출력시 true, 지면에서 위로 출력시 false
    //         width: 50,			// 선의 경우 크기지정.
    //         material: new vw.Color(255, 0, 0, 255).ws3dColor.withAlpha(0.2),	// RGBA A값만 255이하로 주면 투명 또는 withAlpha(1.0~0.1)로 설정.
    //         outline: true,			// 아웃라인지정시 true, 아웃라인 미지정 false
    //         outlineWidth: 5,		// 아웃라인 너비. 
    //         outlineColor: vw.Color.YELLOW.ws3dColor,		// 아웃라인 색상. 
    //         height: 1600.0			// 높이 지정값 meter.
    //     };
    //     // 출력 색상등 지정.
    //     featureInfos.setOption(options);
    //     // Point의 경우 이미지 설정. options 항목이 필요없음.
    //     //featureInfos.setImage("https://map.vworld.kr/images/comm/symbol_05.png");
    //     // 출력 좌표 설정.

    //     const promise = new Promise((resolve, reject) => {
    //         featureInfos.makeCoords();
    //         resolve('promise');
    //     });

    //     promise.then((value) => {
    //         console.log(value);

    //         var result = "";
    //         featureInfos.objCollection.collectionProp.forEach(function (i) { // objCollection.collectionProp 객체의 속성 값을 가지고 있음
    //             result += i.properties.full_nm + " "
    //             $("#features").html(result);
    //         })
    //     });

    //     // 이벤트
    //     var feafureEventHandler = function (windowPosition, ecefPosition, cartographic, featureInfo) {
    //         //ws3d.viewer.map.clearAllHighlightedFeatures();
    //         //featureInfo 가 존재 -> 3D 객체 클릭
    //         //onsole.log("featureInfo :" , windowPosition, ecefPosition, cartographic, featureInfo);
    //         if (featureInfo) {
    //             var feature = featureInfos.getById(featureInfo.groupId);
    //             console.log("after feature :", featureInfos.objCollection);
    //             console.log("feature :", feature);
    //             // 그래픽객체 미출력
    //             // feature.hide();
    //             // FeatureInfo 객체 삭제1.
    //             //featureInfos.removeById(feature.id);
    //             // FeatureInfo 객체 삭제2.
    //             featureInfos.removeByObj(feature);
    //             // feature 정보를 가지고 있는 properties
    //             //console.log("feature properties:" , feature.getProperties());
    //             console.log("after feature :", featureInfos.objCollection);
    //         }
    //     };
    //     // 이벤트 설정.
    //     featureInfos.addEventListener(feafureEventHandler);
    //     // 출력.
    //     featureInfos.show();
    //     console.log('END - geojson')
    // }

    // function vworldWFSData() {
    // GeoJsonLayer 생성
    // const geoJsonLayer = new vw.layer.GeoJsonLayer({
    //     geoJson: data,
    //     style: {
    //         strokeColor: '#ff0000', // 경계선 색상
    //         strokeWidth: 2, // 경계선 두께
    //         fillColor: 'rgba(255, 0, 0, 0.1)' // 영역 채우기 색상 (투명도 포함)
    //     }
    // });
    // console.log(geoJsonLayer);

    // resolve(data)

    // // OpenLayers를 사용해 GeoJSON 데이터 렌더링
    // const vectorSource = new ol.source.Vector({
    //     features: new ol.format.GeoJSON().readFeatures(data, {
    //         dataProjection: 'EPSG:3857', // 데이터 좌표계
    //         featureProjection: 'EPSG:3857' // 지도 좌표계
    //     })
    // });

    // const vectorLayer = new ol.layer.Vector({
    //     source: vectorSource,
    //     style: new ol.style.Style({
    //         stroke: new ol.style.Stroke({
    //             color: '#ff0000',
    //             width: 2
    //         }),
    //         fill: new ol.style.Fill({
    //             color: 'rgba(255, 0, 0, 0.1)'
    //         })
    //     })
    // });
    // // OpenLayers 사용 예시
    // const features = new ol.format.GeoJSON().readFeatures(data);
    // const vectorSource = new ol.source.Vector({
    //     features: features
    // });

    // const boundaryLayer = new ol.layer.Vector({
    //     source: vectorSource,
    //     style: new ol.style.Style({
    //         stroke: new ol.style.Stroke({
    //             color: '#ff0000', // 경계선 색상
    //             width: 2 // 경계선 두께
    //         }),
    //         fill: new ol.style.Fill({
    //             color: 'rgba(255, 0, 0, 0.1)' // 영역 채우기 색상 (투명도 포함)
    //         })
    //     })
    // });
    // resolve(vectorLayer);


    // 브이월드 스타일 설정
    // const layer = new vw.layer.VwFeatureLayer({
    //     data: data,
    //     style: {
    //         stroke: {
    //             color: '#ff0000',
    //             width: 2
    //         },
    //         fill: {
    //             color: 'rgba(255, 0, 0, 0.1)'
    //         }
    //     }
    // });
    // resolve(layer);

    // // GeoJsonLayer 생성
    // const geoJsonLayer = new vw.layer.GeoJsonLayer({
    //     geoJson: data,
    //     style: {
    //         strokeColor: '#ff0000', // 경계선 색상
    //         strokeWidth: 2, // 경계선 두께
    //         fillColor: 'rgba(255, 0, 0, 0.1)' // 영역 채우기 색상 (투명도 포함)
    //     }
    // });
    // resolve(geoJsonLayer);
    // }

    // function loadVmap() {
    //     let map;

    //     // 브이월드 지도 초기화
    //     map = new vw.Map("vworldMap", {
    //         apiKey: "2D9BDADB-D581-3DB0-991E-11E5985B77CC", // API 키 입력
    //         basemapType: vw.BasemapType.GRAPHIC
    //     });
    //     let options = {
    //         mapId: "vworldMap", // 지도맵 컨테이너 아이디
    //         initPosition: new vw.CameraPosition( // 초기 위치
    //             new vw.CoordZ(126.921883, 37.524370, 2400),
    //             new vw.Direction(0, -90, 0)
    //         ),
    //         logo: true, // 하단 로고 설정. true : 표출, false : 미표출
    //         navigation: true // 오른쪽 상단 네비게이션 설정. true : 표출, false : 미표출
    //     };
    //     map.setOption(options); //3D지도 초기 옵션 설정
    //     map.setMapId("vworldMap"); //지도가 탑재될 컨테이너 설정 가능
    //     map.setInitPosition(new vw.CameraPosition( //초기 위치설정 가능
    //         new vw.CoordZ(126.921883, 37.524370, 24000),
    //         new vw.Direction(0, -90, 0)
    //     ));
    //     // map.setCenterAndZoom(new vw.Coord(127.024612, 37.532600), 7); // 대한민국 중심 좌표
    //     map.start(); // 설정 항목을 가지고 최종지도생성       

    //     $.ajax({
    //         url: 'http://localhost:8080/sh_api/vworldWFS?service=WFS&version=2.0.0&request=GetFeature&typeName=lt_c_adsido_info&output=json&key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&domain=localhost',
    //         type: 'POST',
    //         contentType: 'application/json',
    //         success: function (data) {
    //             console.log(data);
    //             // GeoJSON 데이터를 지도에 추가
    //             var boundaryLayer = new vw.layer.Vector({
    //                 source: new vw.source.Vector({
    //                     features: new vw.format.GeoJSON().readFeatures(data)
    //                 }),
    //                 style: new vw.style.Style({
    //                     stroke: new vw.style.Stroke({
    //                         color: '#ff0000', // 경계선 색상
    //                         width: 2 // 경계선 두께
    //                     }),
    //                     fill: new vw.style.Fill({
    //                         color: 'rgba(255, 0, 0, 0.1)' // 영역 채우기 색상 (투명도 포함)
    //                     })
    //                 })
    //             });
    //             map.addLayer(boundaryLayer);
    //         },
    //         error: function () {
    //             console.log('error')
    //         }
    //     });


    //     // 행정 경계 데이터를 가져오기
    //     // fetch("https://api.vworld.kr/req/wms?key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&SERVICE=WMS&REQUEST=GetMap&VERSION=1.3.0&LAYERS=lp_pa_cbnd_bonbun,lp_pa_cbnd_bubun&STYLES=lp_pa_cbnd_bonbun_line,lp_pa_cbnd_bubun_line&CRS=EPSG:900913&BBOX=14133818.022824,4520485.8511757,14134123.770937,4520791.5992888&WIDTH=256&HEIGHT=256&FORMAT=image/png&TRANSPARENT=false&BGCOLOR=0xFFFFFF&EXCEPTIONS=text/xml&domain=localhost")
    //     // fetch("https://api.vworld.kr/req/wfs?service=WFS&version=2.0.0&request=GetFeature&typeName=lt_c_adsido_info&output=json&key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&domain=localhost")
    //     //         .then(response => response.json())
    //     //         .then(data => {
    //     //             console.log(data);
    //     //             // GeoJSON 데이터를 지도에 추가
    //     //             var boundaryLayer = new vw.layer.Vector({
    //     //                 source: new vw.source.Vector({
    //     //                     features: new vw.format.GeoJSON().readFeatures(data)
    //     //                 }),
    //     //                 style: new vw.style.Style({
    //     //                     stroke: new vw.style.Stroke({
    //     //                         color: '#ff0000', // 경계선 색상
    //     //                         width: 2 // 경계선 두께
    //     //                     }),
    //     //                     fill: new vw.style.Fill({
    //     //                         color: 'rgba(255, 0, 0, 0.1)' // 영역 채우기 색상 (투명도 포함)
    //     //                     })
    //     //                 })
    //     //             });
    //     //             map.addLayer(boundaryLayer);
    //     //         })
    //     //         .catch(error => console.error("행정 경계 데이터를 가져오는 중 오류 발생:", error));
    //     // fetch("http://localhost:8080/sh_api/vworldWFS?service=WFS&version=1.3.0&request=GetFeature&typeName=lt_c_adsido_info&output=json&key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&domain=localhost")
    //     //     .then(response => response.json())
    //     //     .then(data => {
    //     //         console.log(data);
    //     //         // 데이터 처리
    //     //     })
    //     //     .catch(error => console.error("오류 발생:", error));


    // }


    // function oldLoadVmap() {
    //예시
    // let options = {
    //     mapId: "vworldMap", // 지도맵 컨테이너 아이디
    //     initPosition: new vw.CameraPosition( // 초기 위치
    //         new vw.CoordZ(126.921883, 37.524370, 2400),
    //         new vw.Direction(0, -90, 0)
    //     ),
    //     logo: true, // 하단 로고 설정. true : 표출, false : 미표출
    //     navigation: true // 오른쪽 상단 네비게이션 설정. true : 표출, false : 미표출
    // };

    // let map = new vw.Map();
    // map.setOption(options); //3D지도 초기 옵션 설정
    // map.setMapId("vworldMap"); //지도가 탑재될 컨테이너 설정 가능
    // map.setInitPosition(new vw.CameraPosition( //초기 위치설정 가능
    //     new vw.CoordZ(126.921883, 37.524370, 24000),
    //     new vw.Direction(0, -90, 0)
    // ));

    // // 로고 및 네비 설정.
    // map.setLogoVisible(true);
    // map.setNavigationZoomVisible(true);
    // map.start(); // 설정 항목을 가지고 최종지도생성    
    // }

});