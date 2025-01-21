document.addEventListener("DOMContentLoaded", function () {
    const yearSelect = document.getElementById("year-select");

    // 기본값 설정
    if (yearSelect) {
        yearSelect.value = "2023";
    }

    yearSelect.addEventListener("change", function () {
        const selectedYear = yearSelect.value;
        console.log(`Selected Year: ${selectedYear}`);
        updateCharts(selectedYear);
    });

    const riskData = JSON.parse(document.getElementById("jsonData").textContent);

    const chart1 = echarts.init(document.getElementById('chart1'));
    const chart2 = echarts.init(document.getElementById('chart2'));
    const chart3 = echarts.init(document.getElementById('chart3'));
    const chart4 = echarts.init(document.getElementById('chart4'));

    let direction = 1; // 회전 방향 (1: 오른쪽, -1: 왼쪽)
    let currentAngle = 0; // 현재 각도
    const maxAngle = 60; // 최대 각도
    const minAngle = -60; // 최소 각도
    let isUserInteracting = false; // 사용자 입력 상태 감지

    // 사용자 입력 감지
    chart1.getZr().on('mousedown', () => {
        isUserInteracting = true; // 사용자 입력 시작
    });
    chart1.getZr().on('mouseup', () => {
        isUserInteracting = false; // 사용자 입력 종료
    });
    chart1.getZr().on('mousemove', () => {
        if (isUserInteracting) {
            currentAngle = null; // 사용자 조작 중 각도 초기화
        }
    });

    function oscillateView(chart) {
        if (isUserInteracting) return; // 사용자 입력 중에는 애니메이션 중지
        
        // `setOption`을 사용하여 `viewControl`의 `beta` 값을 업데이트
        chart.setOption({
            grid3D: {
                viewControl: {
                    beta: currentAngle, // 현재 각도 적용
                },
            },
        });

        // 각도를 갱신 (방향에 따라 증가 또는 감소)
        currentAngle += direction;

        // 방향 전환 (최대/최소 각도에 도달했을 때)
        if (currentAngle >= maxAngle || currentAngle <= minAngle) {
            direction *= -1;
        }
    }

    function updateCharts(selectedYear) {
        const filteredData = riskData.filter(item => item.year === parseInt(selectedYear));
    
        if (filteredData.length === 0) {
            chart1.clear();
            chart2.clear();
            chart3.clear();
            chart4.clear();
            return;
        }
    
        const regions = filteredData.map(item => item.region.split("").join("\n"));
        const totalIncidents = filteredData.map((item, index) => [index, 0, item.totalIncidents]);
         // 데이터 확인용 출력
        console.log("Total Incidents Data:", totalIncidents);
        const totalDamage = filteredData.map(item => item.totalDamage);
        const riskLevels = filteredData.map(item => item.riskLevel);
        const riskScores = filteredData.map(item => ({
            name: item.region,
            value: item.riskScore,
        }));
    
        // 총 재해건수 애니메이션 설정
        chart1.setOption(create3DBarOption('연간 총 재해건수', regions, totalIncidents), { replaceMerge: ['series'] });
        chart2.setOption(createLineChartOption('연간 총 피해금액', regions, totalDamage));
        chart3.setOption(createRiskLevelChartOption('연간 위험도 레벨(3:고위험, 2:중위험, 1:저위험)', regions, riskLevels));
        chart4.setOption(createDonutChartOption('연간 위험도 점수비율', riskScores));
    }
    

    // 3D 막대 그래프 옵션 생성
    function create3DBarOption(title, categories, data) {
         // 데이터를 값 기준으로 정렬하여 1, 2위를 확인
        const sortedData = [...data].sort((a, b) => b[2] - a[2]); // z축 값 기준 내림차순 정렬
        const firstValue = sortedData[0][2]; // 1위 값
        const secondValue = sortedData[1][2]; // 2위 값
        
        console.log('1위 값:', firstValue, '2위 값:', secondValue);

        return {
            title: { text: title, textStyle: { color: '#ffffff' } },
            backgroundColor: '#1e1e1e',
            tooltip: {},
            xAxis3D: {
                type: 'category',
                data: categories,
                axisLabel: {
                    color: '#ffffff',
                    fontSize: 10,
                    margin: 10,
                    interval: 0,
                },
            },
            yAxis3D: {
                type: 'category',
                data: [''],
                axisLabel: { color: '#ffffff' },
            },
            zAxis3D: {
                type: 'value',
                axisLabel: { color: '#ffffff' },
            },
            grid3D: {
                viewControl: {
                    projection: 'perspective',
                    autoRotate: false,
                    rotateSensitivity: 1,
                    zoomSensitivity: 1,
                    panSensitivity: 1,
                    alpha: 0,
                    beta: 0,
                    minAlpha: -90,
                    maxAlpha: 90,
                    minBeta: -180,
                    maxBeta: 180,
                },
                boxWidth: 220,
                boxHeight: 80,
                boxDepth: 40,
                light: {
                    main: { intensity: 1.2 },
                    ambient: { intensity: 0.3 },
                },
            },
            series: [
                {
                    type: 'bar3D',
                    data: data,
                    shading: 'realistic',
                    itemStyle: {
                        color: (params) => {
                            const value = params.value[2];
                            if (value === firstValue) return '#C80000'; // 1위 빨간색
                            if (value === secondValue) return '#FF4545'; // 2위 주황색
                            return '#FF9999'; // 나머지 연한 빨간색
                        },
                    },
                },
        ],
    };
}

    // 기존 차트2: 점선그래프
    function createLineChartOption(title, categories, data) {
          // 1위와 2위를 계산
        const sortedData = [...data].sort((a, b) => b - a); // 내림차순 정렬
        const firstValue = sortedData[0]; // 1위 값
        const secondValue = sortedData[1]; // 2위 값

        // 1위와 2위의 인덱스 찾기
        const firstIndex = data.indexOf(firstValue);
        const secondIndex = data.indexOf(secondValue);

        return {
            title: { text: title, textStyle: { color: '#ffffff' } },
            backgroundColor: '#1e1e1e',
            tooltip: { trigger: 'axis' },
            xAxis: {
                type: 'category',
                data: categories,
                axisLabel: {
                    color: '#ffffff',
                    fontSize: 10,
                },
            },
            yAxis: {
                type: 'value',
                axisLabel: { color: '#ffffff' },
            },
            series: [
                {
                    name: title,
                    type: 'line',
                    data: data,
                    lineStyle: {
                        type: 'dashed',
                    },
                    itemStyle: {
                        color: '#FF6F61',
                    },
                    markPoint: {
                        data: [
                            {
                                name: '1위',
                                xAxis: firstIndex,
                                yAxis: firstValue,
                                itemStyle: {
                                    color: '#C80000', // 1위 진한 빨간색
                                },
                                symbolSize:30, // 1위 점 크기
                            },
                            {
                                name: '2위',
                                xAxis: secondIndex,
                                yAxis: secondValue,
                                itemStyle: {
                                    color: '#FF4545', // 2위 덜 진한 빨간색
                                },
                                symbolSize:20, // 2위 점 크기
                            },
                        ],
                        symbol: 'circle', // 점 모양
                        symbolSize: 20, // 점 크기
                        label: {
                            show: false, // 레이블 숨김
                        },
                        itemStyle: {
                            color: '#FF0000', // 점 색상
                        },
                    },
                },
            ],
        };
    }

    // 기존 차트3: 3D 위험레벨
    function createRiskLevelChartOption(title, categories, data) {
        return {
            title: { text: title, textStyle: { color: '#ffffff' } },
            backgroundColor: '#1e1e1e',
            tooltip: {},
            xAxis: {
                type: 'category',
                data: categories,
                axisLabel: { color: '#ffffff' },
            },
            yAxis: {
                type: 'value',
                min: 1,
                max: 3,
                interval: 1,
                axisLabel: {
                    color: '#ffffff',
                    fontSize: 10,
                },
            },
            series: [
                {
                    name: title,
                    type: 'bar',
                    data: data,
                    itemStyle: {
                        color: '#74C476',
                    },
                },
            ],
        };
    }

    // 기존 차트4: 도넛형 차트
    function createDonutChartOption(title, data) {
        return {
            title: { text: title, textStyle: { color: '#ffffff' }, left: 'center' },
            backgroundColor: '#1e1e1e',
            tooltip: { trigger: 'item' },
            legend: {
                top: '5%',
                left: 'center',
                textStyle: { color: '#ffffff' },
            },
            series: [
                {
                    name: title,
                    type: 'pie',
                    radius: ['40%', '70%'],
                    avoidLabelOverlap: false,
                    label: {
                        show: false,
                        position: 'center',
                    },
                    emphasis: {
                        label: {
                            show: true,
                            fontSize: '20',
                            fontWeight: 'bold',
                        },
                    },
                    labelLine: {
                        show: false,
                    },
                    data: data,
                },
            ],
        };
    }

    updateCharts('2023');
    setInterval(() => oscillateView(chart1), 50);
    console.log("ECharts Version:", echarts.version);
});
