$(document).ready(() => {
    console.log('sj_map.js');

    function initialize() {
        callVWorldWFSData().then(
            (data) => {
                console.log('Promise - then')
                console.log(data)
            }
        );


        // loadVmap();
    }

    function loadVmap() {

        Promise.all([
            callVWorldWFSData()
        ]).then(([wfsData]) => {
            console.log('loadVMap - then')
            let map;

            // 브이월드 지도 초기화
            map = new vw.Map("vworldMap", {
                apiKey: "2D9BDADB-D581-3DB0-991E-11E5985B77CC", // API 키 입력
                basemapType: vw.BasemapType.GRAPHIC,
                initPosition: new vw.CameraPosition( // 초기 위치 설정
                    new vw.CoordZ(126.921883, 37.524370, 24000),
                    new vw.Direction(0, -90, 0)
                ),
                logo: true, // 하단 로고 활성화
                navigation: true // 네비게이션 활성화
            });
            let options = {
                mapId: "vworldMap", // 지도맵 컨테이너 아이디
                initPosition: new vw.CameraPosition( // 초기 위치
                    new vw.CoordZ(126.921883, 37.524370, 2400),
                    new vw.Direction(0, -90, 0)
                ),
                logo: true, // 하단 로고 설정. true : 표출, false : 미표출
                navigation: true // 오른쪽 상단 네비게이션 설정. true : 표출, false : 미표출
            };
            map.setOption(options); //3D지도 초기 옵션 설정
            map.setMapId("vworldMap"); //지도가 탑재될 컨테이너 설정 가능
            map.start(); // 지도 생성
            // map.addLayer(wfsData); // 레이어 추가

            // GeoJSON 데이터 가져오기
            $.ajax({
                url: 'http://localhost:8080/sh_api/vworldWFS?service=WFS&version=2.0.0&request=GetFeature&typeName=lt_c_adsido_info,lt_c_adsido_3d&output=json&key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&domain=localhost',
                type: 'POST',
                dataType: 'json',
                success: function (data) {
                    console.log("데이터 로드 성공:", data);

                    // GeoJsonLayer 생성
                    const geoJsonLayer = new vw.layer.GeoJsonLayer({
                        geoJson: data,
                        style: {
                            strokeColor: '#ff0000', // 경계선 색상
                            strokeWidth: 2, // 경계선 두께
                            fillColor: 'rgba(255, 0, 0, 0.1)' // 영역 채우기 색상 (투명도 포함)
                        }
                    });
                    console.log(geoJsonLayer);
                    // 레이어를 지도에 추가
                    map.addLayer(geoJsonLayer);
                },
                error: function (error) {
                    console.error("데이터 로드 실패:", error);
                }
            });
        }).catch((error) => {

        }).finally(() => {
            console.log('loadVMap - Finally');
        })

    }

    function callVWorldWFSData() {
        return new Promise((resolve, reject) => {

            // GeoJSON 데이터 가져오기
            $.ajax({
                url: 'http://localhost:8080/sh_api/vworldWFS?service=WFS&version=2.0.0&request=GetFeature&typeName=lt_c_adsido_info&output=json&key=2D9BDADB-D581-3DB0-991E-11E5985B77CC&domain=localhost',
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                success: function (data) {
                    console.log("데이터 로드 성공:", data);
                    // GeoJsonLayer 생성
                    const geoJsonLayer = new vw.layer.GeoJsonLayer({
                        geoJson: data,
                        style: {
                            strokeColor: '#ff0000', // 경계선 색상
                            strokeWidth: 2, // 경계선 두께
                            fillColor: 'rgba(255, 0, 0, 0.1)' // 영역 채우기 색상 (투명도 포함)
                        }
                    });
                    console.log(geoJsonLayer);

                    resolve(data)

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
                },
                error: function (error) {
                    console.error("데이터 로드 실패:", error);
                    reject(error);
                }
            });
        });
    }

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

    initialize();

    function oldLoadVmap() {

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
    }

});