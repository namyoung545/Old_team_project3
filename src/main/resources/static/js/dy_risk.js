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
        const totalDamage = filteredData.map(item => item.totalDamage/100000); // 단위, 억원으로 적용
        const riskLevels = filteredData.map(item => item.riskLevel);
        const riskScores = filteredData.map(item => ({
            name: item.region,
            value: item.riskScore,
        }));
    
        // 총 재해건수 애니메이션 설정
        chart1.setOption(create3DBarOption('연간 총 재해건수', regions, totalIncidents), { replaceMerge: ['series'] });
        chart2.setOption(createLineChartOption('연간 총 피해금액(단위, 억원)', regions, totalDamage));
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
                // axisLabel: {
                //     color: '#ffffff',
                //     fontSize: 10,
                //     margin: 10,
                //     interval: 0,
                // },
            },
            yAxis3D: {
                type: 'category',
                data: [''],
                axisLabel: { color: '#ffffff' },

            },
            zAxis3D: {
                type: 'value',
                axisLabel: { color: '#ffffff' },
                // axisLine: { show: false }, // z축 선 제거
                // splitLine: { show: false }, // z축 격자선 제거
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
                boxWidth: 300,
                boxHeight: 120,
                boxDepth: 10,
                light: {
                    main: { intensity: 1.5 },
                    ambient: { intensity: 0.3 },
                },
            },
            series: [
                {
                    type: 'bar3D',
                    data: data,
                    shading: 'realistic',
                    label: {
                        show: true, // 레이블 표시
                        position: 'top', // 막대 상단에 레이블 위치
                        formatter: (params) => {
                            return categories[params.value[0]]; // x축에 해당하는 지역(region) 정보 표시
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
                            if (value === firstValue) return '#FFD700'; // 1위 진노랑
                            if (value === secondValue) return '#FFEC8B'; // 2위 중간 노랑
                            return '#FFFACD'; // 나머지 옅은 노랑
                        },
                    },
                 
                },
        ],
    };
}

     // 기존 차트2: 점선그래프
     function createLineChartOption(title, categories, data) {
        const sortedData = [...data].sort((a, b) => b - a);
        const firstValue = sortedData[0];
        const secondValue = sortedData[1];

        const firstIndex = data.indexOf(firstValue);
        const secondIndex = data.indexOf(secondValue);

        return {
            title: { text: title, textStyle: { color: '#ffffff' } },
            backgroundColor: '#1e1e1e',
            tooltip: { 
                trigger: 'axis',
                formatter: (params) => {
                    return `${params[0].name}: ${Math.round(params[0].value)}`; // 소수점 제거
                }
            },
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
                axisLabel: { 
                    color: '#ffffff',
                    formatter: (value) => Math.round(value), // y축 레이블 소수점 제거
                },
            },
            series: [
                {
                    name: title,
                    type: 'line',
                    data: data.map(value => Math.round(value)), // 소수점 제거
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
                                xAxis: firstIndex,
                                yAxis: Math.round(firstValue), // 소수점 제거
                                itemStyle: {
                                    color: '#FFD700', // 진한 노랑
                                },
                                symbolSize: 30,
                            },
                            {
                                name: '2위',
                                xAxis: secondIndex,
                                yAxis: Math.round(secondValue), // 소수점 제거
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
        return {
            title: { 
                text: title, 
                textStyle: { color: '#ffffff' } 
            },
            backgroundColor: '#1e1e1e',
            tooltip: {},
            xAxis: {
                type: 'category',
                data: categories,
                axisLabel: { color: '#ffffff' },
            },
            yAxis: {
                type: 'value',
                min: 0, // y축 최소값 0 설정
                max: 3, // y축 최대값 3 설정
                interval: 1, // 0, 1, 2, 3 단위로 표시
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
        // 데이터를 내림차순 정렬
        const sortedData = data.sort((a, b) => b.value - a.value);

        return {
            title: { 
                text: title,
                textStyle: { color: '#ffffff'},
            },
            backgroundColor: '#1e1e1e',
            tooltip: { 
                trigger: 'item' ,
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
                        // position: 'center',
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
                    data: sortedData,
                    itemStyle: {
                        color: (params) => {
                            const index = params.dataIndex;
                            if (index === 0) return '#FFD700'; // 1위: 진한 빨간색
                            if (index === 1) return '#FFEC8B'; // 2위: 덜 진한 빨간색
                            return '#FFFACD'; // 나머지: 연한 빨간색
                        },
                    },
                },
            ],
        };
    }
    updateCharts('2023');
    setInterval(() => oscillateView(chart1), 50);
    console.log("ECharts Version:", echarts.version);
});
