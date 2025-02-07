document.addEventListener("DOMContentLoaded", function () {
    const yearSelect = document.getElementById("year-select_ny");

    // 기본값 설정
    if (yearSelect) {
        yearSelect.value = "2023";
    }

    yearSelect.addEventListener("change", function () {
        const selectedYear = yearSelect.value;
        console.log(`Selected Year: ${selectedYear}`);
        updateCharts(selectedYear);
    });

    const riskData = JSON.parse(document.getElementById("jsonData_ny").textContent);

    const chart_ny1 = echarts.init(document.getElementById('chart_ny1'));
    const chart_ny2 = echarts.init(document.getElementById('chart_ny2'));
    const chart_ny3 = echarts.init(document.getElementById('chart_ny3'));
    const chart_ny4 = echarts.init(document.getElementById('chart_ny4'));

    let direction = 1; // 회전 방향 (1: 오른쪽, -1: 왼쪽)
    let currentAngle = 0; // 현재 각도
    const maxAngle = 30; // 최대 각도
    const minAngle = -30; // 최소 각도
    let isUserInteracting = false; // 사용자 입력 상태 감지

    // 사용자 입력 감지
    chart_ny1.getZr().on('mousedown', () => {
        isUserInteracting = true; // 사용자 입력 시작
    });
    chart_ny1.getZr().on('mouseup', () => {
        isUserInteracting = false; // 사용자 입력 종료
    });
    chart_ny1.getZr().on('mousemove', () => {
        if (isUserInteracting) {
            currentAngle = null; // 사용자 조작 중 각도 초기화
        }
    });

    function oscillateView(chart) {
        if (isUserInteracting) return; // 사용자 입력 중에는 애니메이션 중지

        // 각도를 갱신 (방향에 따라 증가 또는 감소)
        currentAngle += direction * 0.5; // 부드러운 움직임을 위해 각도를 천천히 변경

        // 방향 전환 (최대/최소 각도에 도달했을 때)
        if (currentAngle >= maxAngle || currentAngle <= minAngle) {
            direction *= -1;
        }

        // `setOption`을 사용하여 `viewControl`의 `beta` 값을 업데이트
        chart.setOption({
            grid3D: {
                viewControl: {
                    beta: currentAngle, // 현재 각도 적용
                },
            },
        });
    }

    function updateCharts(selectedYear) {
        const filteredData = riskData.filter(item => item.year === parseInt(selectedYear));
    
        if (filteredData.length === 0) {
            chart_ny1.clear();
            chart_ny2.clear();
            chart_ny3.clear();
            chart_ny4.clear();
            return;
        }
    
        const regions = filteredData.map(item => item.region.split("").join("\n"));
        const totalIncidents = filteredData.map((item, index) => [index, 0, item.totalIncidents]);
         // 데이터 확인용 출력
        console.log("Total Incidents Data:", totalIncidents);
        const totalDamage = filteredData.map(item => item.totalDamage/100000); // 단위, 억원으로 적용
        const riskLevels = filteredData.map(item => item.riskLevel);
        const riskScores = filteredData.map(item => ({
            name: item.region,
            value: item.riskScore,
        }));
    
        // 총 재해건수 애니메이션 설정
        chart_ny1.setOption(create3DBarOption('연간 총 재해건수', regions, totalIncidents), { replaceMerge: ['series'] });
        chart_ny2.setOption(createLineChartOption('연간 총 피해금액(단위, 억원)', regions, totalDamage));
        chart_ny3.setOption(createRiskLevelChartOption('연간 위험도 레벨(3:고위험, 2:중위험, 1:저위험)', regions, riskLevels));
        chart_ny4.setOption(createDonutChartOption('연간 위험도 점수비율', riskScores));
    }
    

    //3D 막대차트
    function create3DBarOption(title, categories, data) {
        // 1위와 2위 데이터 값 계산
        const maxIndex = data.reduce((maxIdx, item, idx, arr) => 
            item[2] > arr[maxIdx][2] ? idx : maxIdx, 0); // 최대값 인덱스
        const secondMaxIndex = data.reduce((maxIdx, item, idx, arr) => 
            idx !== maxIndex && item[2] > arr[maxIdx][2] ? idx : maxIdx, 0); // 두 번째 최대값 인덱스
    
        const firstValue = data[maxIndex][2]; // 1위 값
        const secondValue = data[secondMaxIndex][2]; // 2위 값
    
        console.log('원본 데이터:', data);
        console.log('1위 값:', firstValue, '2위 값:', secondValue);
    
        return {
            title: { text: title, textStyle: { color: '#ffffff' } },
            backgroundColor: '#1e1e1e',
            tooltip: {},
            xAxis3D: {
                type: 'category',
                data: categories, // 원래 카테고리 적용
                axisLabel: {
                    show: false, // X축 레이블 숨기기
                },
            },
            yAxis3D: {
                type: 'category',
                data: [''], // Y축 데이터 (고정된 단일 값)
                axisLabel: { color: '#ffffff' },
            },
            zAxis3D: {
                type: 'value',
                axisLabel: { color: '#ffffff' },
            },
            grid3D: {
                axisLine: {
                    lineStyle: {
                        color: 'transparent', // X축 선 색상을 투명하게 설정
                    },
                },
                axisPointer: {
                    show: false, // 카메라 축 포인터 숨기기
                },
                viewControl: {
                    projection: 'perspective',
                    autoRotate: false, // 자동 회전 비활성화
                    rotateSensitivity: 3, // 회전 비활성화
                    zoomSensitivity: 1, // 줌 비활성화
                    panSensitivity: 1, // 팬 비활성화
                    beta: currentAngle, // 초기 각도
                    alpha: 0, // 초기 각도
                    beta: 0,  // 초기 각도
                    minAlpha: 0,
                    maxAlpha: 0,
                    minBeta: 0,
                    maxBeta: 0,
                },
                boxWidth: 300,
                boxHeight: 120,
                boxDepth: 10,
                light: {
                    main: { intensity: 1 }, // 메인 라이트
                    ambient: { intensity: 0.3 }, // 환경 라이트
                },
            },
            series: [
                {
                    type: 'bar3D',
                    data: data, // 원래 데이터 배열 적용
                    shading: 'realistic',
                    label: {
                        show: true, // 레이블 표시
                        position: 'top', // 막대 상단에 레이블 위치
                        formatter: (params) => {
                            return categories[params.value[0]]; // 원래 카테고리 표시
                        },
                        textStyle: {
                            color: '#ffffff', // 텍스트 색상
                            fontSize: 10,
                            fontWeight: 'bold',
                        },
                    },
                    itemStyle: {
                        color: (params) => {
                            const value = params.value[2];
                            if (params.dataIndex === maxIndex) return '#FFD700'; // 1위 진노랑
                            if (params.dataIndex === secondMaxIndex) return '#FFEC8B'; // 2위 중간 노랑
                            return '#FFFACD'; // 나머지 옅은 노랑
                        },
                    },
                },
            ],
        };
    }

     // 기존 차트2: 점선그래프
     function createLineChartOption(title, categories, data) {
        // 데이터와 카테고리를 원래 순서대로 사용
        const newCategories = [...categories]; // 기존 카테고리 그대로 사용
        const newData = data.map(value => Math.round(value)); // 데이터 소수점 제거
    
        const firstValue = Math.max(...newData); // 1위 값
        const firstIndex = newData.indexOf(firstValue); // 1위 값의 인덱스
    
        const secondValue = [...newData]
            .filter(value => value !== firstValue) // 1위 값 제외
            .reduce((max, value) => Math.max(max, value), -Infinity); // 2위 값
        const secondIndex = newData.indexOf(secondValue); // 2위 값의 인덱스
    
        return {
            title: { text: title, textStyle: { color: '#ffffff' } },
            backgroundColor: '#1e1e1e',
            tooltip: { 
                trigger: 'axis',
                formatter: (params) => {
                    return `${params[0].name}: ${params[0].value}`; // 소수점 제거된 값 표시
                }
            },
            grid: {
                top: '15%',    // 위쪽 여백
                bottom: '5%', // 아래쪽 여백 (적절히 조정)
                containLabel: true, // 레이블이 잘리지 않도록 설정
            },
            xAxis: {
                type: 'category',
                data: newCategories, // 기존 카테고리 그대로 사용
                axisLabel: {
                    color: '#ffffff',
                    fontSize: 10,
                },
            },
            yAxis: {
                type: 'value',
                axisLabel: { 
                    color: '#ffffff',
                    formatter: (value) => Math.round(value), // y축 레이블 소수점 제거
                },
            },
            series: [
                {
                    name: title,
                    type: 'line',
                    data: newData, // 기존 데이터 그대로 사용
                    lineStyle: {
                        type: 'dashed',
                    },
                    itemStyle: {
                        color: '#FFD700',
                    },
                    markPoint: {
                        data: [
                            {
                                name: '1위',
                                xAxis: firstIndex, // 1위 값의 인덱스
                                yAxis: firstValue, // 1위 값
                                itemStyle: {
                                    color: '#FFD700', // 진한 노랑
                                },
                                symbolSize: 30,
                            },
                            {
                                name: '2위',
                                xAxis: secondIndex, // 2위 값의 인덱스
                                yAxis: secondValue, // 2위 값
                                itemStyle: {
                                    color: '#FFEC8B', // 중간 노랑
                                },
                                symbolSize: 20,
                            },
                        ],
                        symbol: 'circle',
                        label: {
                            show: false,
                        },
                    },
                },
            ],
        };
    }

    // 기존 차트3: 3D 위험레벨
    function createRiskLevelChartOption(title, categories, data) {
        // 데이터를 정렬하지 않고 그대로 사용
        const newCategories = [...categories]; // 기존 카테고리 그대로 사용
        const newData = [...data]; // 기존 데이터 그대로 사용
    
        return {
            title: { 
                text: title, 
                textStyle: { color: '#ffffff' } 
            },
            backgroundColor: '#1e1e1e',
            tooltip: {},
            grid: {
                top: '15%',    // 위쪽 여백
                bottom: '3%', // 아래쪽 여백
                containLabel: true, // 레이블이 잘리지 않도록 설정
            },
            xAxis: {
                type: 'category',
                data: newCategories, // 기존 카테고리 그대로 사용
                axisLabel: { color: '#ffffff' },
            },
            yAxis: {
                type: 'value',
                min: 0, // y축 최소값 0 설정
                max: 3, // y축 최대값 3 설정
                interval: 1, // 0, 1, 2, 3 단위로 표시
                axisLabel: {
                    color: '#ffffff',
                    fontSize: 15,
                },
            },
            series: [
                {
                    name: title,
                    type: 'bar',
                    data: newData, // 기존 데이터 그대로 사용
                    itemStyle: {
                        color: (params) => {
                            const value = params.value; // 데이터 값 (레벨)
                            if (value === 3) return '#FFD700'; // 레벨 3: 진한 빨간색
                            if (value === 2) return '#FFEC8B'; // 레벨 2: 덜 진한 빨간색
                            return '#FFFACD'; // 나머지 레벨: 연한 빨간색
                        },
                    },
                },
            ],
        };
    }

    // 기존 차트4: 도넛형 차트
    function createDonutChartOption(title, data) {
        // 데이터를 정렬하지 않고 그대로 사용
        const originalData = [...data]; // 기존 데이터를 그대로 사용
    
        // 1위와 2위 데이터 값 확인
        const maxIndex = originalData.reduce((maxIdx, item, idx, arr) => 
            item.value > arr[maxIdx].value ? idx : maxIdx, 0); // 최대값 인덱스
        const secondMaxIndex = originalData.reduce((maxIdx, item, idx, arr) => 
            idx !== maxIndex && item.value > arr[maxIdx].value ? idx : maxIdx, 0); // 두 번째 최대값 인덱스
    
        return {
            title: { 
                text: title,
                textStyle: { color: '#ffffff'},
            },
            backgroundColor: '#1e1e1e',
            tooltip: { 
                trigger: 'item',
                formatter: '{b}: {d}%' // {d}는 비율 표시, 소숫점 제외
            },
            series: [
                {
                    name: title,
                    type: 'pie',
                    radius: ['40%', '70%'],
                    avoidLabelOverlap: false,
                    label: {
                        show: true, // 각 데이터 비율 표시
                        formatter: '{b}\n{d}%', // 지역 이름과 퍼센트 표시
                        color: '#ffffff',
                        fontSize: 12,
                    },
                    emphasis: {
                        label: {
                            show: true,
                            fontSize: '16',
                            fontWeight: 'bold',
                            formatter: '{b}\n{d}%' // 강조 시 비율만 표시
                        },
                    },
                    labelLine: {
                        show: true,
                        length: 10,
                        length2: 10,
                    },
                    data: originalData, // 기존 데이터 그대로 사용
                    itemStyle: {
                        color: (params) => {
                            const index = params.dataIndex;
                            if (index === maxIndex) return '#FFD700'; // 1위: 진한 빨간색
                            if (index === secondMaxIndex) return '#FFEC8B'; // 2위: 덜 진한 빨간색
                            return '#FFFACD'; // 나머지: 연한 빨간색
                        },
                    },
                },
            ],
        };
    }
    
    
    updateCharts('2023');
    oscillateView(chart1)
    console.log("ECharts Version:", echarts.version);
});
