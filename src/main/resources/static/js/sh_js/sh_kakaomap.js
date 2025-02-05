/**
 * 
 */

// 카카오 지도 로드 함수
function loadMap(location) {
	let mapContainer = document.getElementById('map');
	mapContainer.innerHTML = ''; // 이전 지도 내용 초기화

	let geocoder = new kakao.maps.services.Geocoder();
	let mapOption = {
		center: new kakao.maps.LatLng(33.450701, 126.570667), // 기본 위치
		level: 3
	};

	// 지도 객체 미리 생성
	let map = new kakao.maps.Map(mapContainer, mapOption);

	// 주소-좌표 변환을 위한 카카오 지도 API 호출
	//	geocoder.addressSearch(location, function(result, status) {
	//
	//		if (status === kakao.maps.services.Status.OK) {
	//
	//			let coords = new kakao.maps.LatLng(result[0].y, result[0].x);
	//
	//			// 새로운 지도 생성 및 업데이트
	//			// 지도 초기화 후 약간의 지연을 두고 지도를 다시 렌더링
	//			setTimeout(function() {
	//				map.setCenter(coords);
	//
	//				// 마커 표시
	//				let marker = new kakao.maps.Marker({
	//					map: map,
	//					position: coords
	//				});
	//
	//				// 인포윈도우 표시
	//				let infowindow = new kakao.maps.InfoWindow({
	//					content: '<div style="width:150px;text-align:center;padding:6px 0;">' + location + '</div>'
	//				});
	//				infowindow.open(map, marker);
	//
	//				// 지도 레이아웃 갱신
	//				map.relayout(); // 레이아웃 다시 계산
	//
	//				// 지도 로딩 완료 후 모달 표시
	//				//				let eventDetails = document.getElementById('eventDetails');
	//				//				eventDetails.style.display = 'block';
	//			}, 100);
	//		} else {
	//			console.error("주소 변환 실패: " + status);
	//		}
	//	});

	//출발지 주소를 '울산 남구 삼산중로100번길 26'으로 설정하여 좌표를 검색합니다
	geocoder.addressSearch('서울 중구 세종대로 110', function(result, status) {

		// 정상적으로 검색이 완료됐으면 
		if (status === kakao.maps.services.Status.OK) {

			var startCoords = new kakao.maps.LatLng(result[0].y, result[0].x);

			// 출발지 마커를 표시합니다
			var startMarker = new kakao.maps.Marker({
				map: map,
				position: startCoords
			});

			// 출발지 마커에 대한 인포윈도우 표시
			var startInfowindow = new kakao.maps.InfoWindow({
				content: '<div style="width:150px;text-align:center;padding:6px 0;">출발지</div>'
			});
			startInfowindow.open(map, startMarker);

			// 목적지 주소를 '울산 남구 중앙로 201'으로 설정하여 좌표를 검색합니다
			geocoder.addressSearch(location, function(result, status) {

				if (status === kakao.maps.services.Status.OK) {

					var destinationCoords = new kakao.maps.LatLng(result[0].y, result[0].x);

					// 목적지 마커를 표시합니다
					var destinationMarker = new kakao.maps.Marker({
						map: map,
						position: destinationCoords
					});

					// 목적지 마커에 대한 인포윈도우 표시
					var infowindow = new kakao.maps.InfoWindow({
						content: '<div style="width:150px;text-align:center;padding:6px 0;">목적지</div>'
					});
					infowindow.open(map, destinationMarker);

					// 출발지와 목적지 사이의 중간 지점을 계산하여 지도 중심 설정
					var centerLat = (startCoords.getLat() + destinationCoords.getLat()) / 2;
					var centerLng = (startCoords.getLng() + destinationCoords.getLng()) / 2;
					var centerCoords = new kakao.maps.LatLng(centerLat, centerLng);
					map.setCenter(centerCoords);

					// 출발지와 목적지의 경계 설정
					var bounds = new kakao.maps.LatLngBounds();
					bounds.extend(startCoords);
					bounds.extend(destinationCoords);
					map.setBounds(bounds);

					getCarDirection(startCoords, destinationCoords, map);

				}
			});
		}
	});
}

//async function getCarDirection(startCoords, destinationCoords, map) {
async function getCarDirection(startCoords, destinationCoords, map) {

	const REST_API_KEY = '4f996114cccadd84c1b311d572c14783'; // 여기에 카카오 네비 API 키 입력
	const url = 'https://apis-navi.kakaomobility.com/v1/directions';

	/*     const origin = String(startCoords.getLng()) + ',' + String(startCoords.getLat());
		const destination = String(destinationCoords.getLng()) + ',' + String(destinationCoords.getLat()); */

	const origin = [startCoords.getLng(), startCoords.getLat()].join(',');
	const destination = [destinationCoords.getLng(), destinationCoords.getLat()].join(',');

	const headers = {
		Authorization: `KakaoAK 4f996114cccadd84c1b311d572c14783`,
		'Content-Type': 'application/json'
	};

	const queryParams = new URLSearchParams({
		origin: origin,
		destination: destination,
	});
	
	// const requestUrl = String(url) + '?' + String(queryParams); // 파라미터까지 포함된 전체 URL
	const requestUrl = String(url) + '?' + String(queryParams); // 파라미터까지 포함된 전체 URL
	const response = await fetch(requestUrl, {
		method: 'GET',
		headers: headers
	});
	if (!response.ok) {
		throw new Error(`HTTP error! Status: ${response.status}`);
	}
	const data = await response.json();

	const linePath = [];
	data.routes[0].sections[0].roads.forEach(router => {
		router.vertexes.forEach((vertex, index) => {
			// x,y 좌표가 우르르 들어옵니다. 그래서 인덱스가 짝수일 때만 linePath에 넣어봅시다.
			// 저도 실수한 것인데 lat이 y이고 lng이 x입니다.
			if (index % 2 === 0) {
				linePath.push(new kakao.maps.LatLng(router.vertexes[index + 1], router.vertexes[index]));
			}
		});
	});

	var polyline = new kakao.maps.Polyline({
		path: linePath,
		strokeWeight: 5,
		strokeColor: '#0078ff',
		strokeOpacity: 0.7,
		strokeStyle: 'solid'
	});

	polyline.setMap(map);  // 지도에 폴리라인을 표시
}