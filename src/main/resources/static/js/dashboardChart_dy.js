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
        const colors = [
            '#FF0000', '#FF4500', '#FF8C00', '#FFD700', '#ADFF2F', '#32CD32', '#00FA9A', '#00CED1', 
            '#1E90FF', '#4169E1', '#8A2BE2', '#9400D3', '#C71585', '#FF1493', '#DC143C', '#B22222', '#8B0000'
        ]; // 17개의 색상 지정
    
        return {
            title: { text: title, textStyle: { color: '#000000' } },
            backgroundColor: '#fefefe',
            tooltip: {},
            xAxis3D: {
                type: 'category',
                data: categories,
                axisLabel: { show: false },
            },
            yAxis3D: {
                type: 'category',
                data: [''],
                axisLabel: { color: '#000000' },
            },
            zAxis3D: {
                type: 'value',
                axisLabel: { color: '#000000' },
            },
            grid3D: {
                axisLine: { lineStyle: { color: 'transparent' } },
                axisPointer: { show: false },
                viewControl: {
                    projection: 'perspective',
                    autoRotate: false,
                    rotateSensitivity: 3,
                    zoomSensitivity: 1,
                    panSensitivity: 1,
                    beta: 0,
                    alpha: 0,
                    minAlpha: 0,
                    maxAlpha: 0,
                    minBeta: 0,
                    maxBeta: 0,
                },
                boxWidth: 300,
                boxHeight: 120,
                boxDepth: 10,
                light: {
                    main: { intensity: 1 },
                    ambient: { intensity: 0.3 },
                },
            },
            series: [
                {
                    type: 'bar3D',
                    data: data,
                    shading: 'realistic',
                    label: {
                        show: true,
                        position: 'top',
                        formatter: (params) => categories[params.value[0]],
                        textStyle: {
                            color: '#000000',
                            fontSize: 10,
                            fontWeight: 'bold',
                        },
                    },
                    itemStyle: {
                        color: (params) => colors[params.dataIndex % colors.length], // 각 인덱스별 색상 적용
                    },
                },
            ],
        };
    }
    

     // 기존 차트2: 점선그래프
     function createLineChartOption(title, categories, data) {
        const colors = [
            '#FFCE56', '#FF6384', '#36A2EB', '#4BC0C0', '#9966FF', '#FF9F40',
            '#C9DE00', '#F7464A', '#46BFBD', '#FDB45C', '#949FB1', '#4D5360',
            '#00A2E8', '#76A1E5', '#FAA43A', '#60BD68', '#F17CB0'
        ];
    
        return {
            title: { text: title, textStyle: { color: '#000000' } },
            backgroundColor: '#fefefe',
            tooltip: {
                trigger: 'axis',
                formatter: (params) => `${params[0].name}: ${params[0].value}`
            },
            grid: { top: '15%', bottom: '5%', containLabel: true },
            xAxis: { type: 'category', data: categories, axisLabel: { color: '#000000', fontSize: 10 } },
            yAxis: { type: 'value', axisLabel: { color: '#000000', formatter: (value) => Math.round(value) } },
            series: [
                {
                    name: title,
                    type: 'line',
                    data: data,
                    lineStyle: { type: 'dashed' },
                    itemStyle: {
                        color: (params) => colors[params.dataIndex % colors.length] // 각 점마다 다른 색 적용
                    },
                },
            ],
        };
    }
    

    // 기존 차트3: 3D 위험레벨
function createRiskLevelChartOption(title, categories, data) {
    const colors = [
        '#FF0000', '#FF4500', '#FF8C00', '#FFD700', '#ADFF2F', '#32CD32', '#00FA9A', '#00CED1',
        '#1E90FF', '#4169E1', '#8A2BE2', '#9400D3', '#C71585', '#FF1493', '#DC143C', '#B22222', '#8B0000'
    ];

    return {
        title: { text: title, textStyle: { color: '#000000' } },
        backgroundColor: '#fefefe',
        tooltip: {},
        grid: { top: '15%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: categories, axisLabel: { color: '#000000' } },
        yAxis: { type: 'value', min: 0, max: 3, interval: 1, axisLabel: { color: '#000000', fontSize: 15 } },
        series: [
            {
                name: title,
                type: 'bar',
                data: data,
                itemStyle: {
                    color: (params) => {
                        return `rgba(${parseInt(colors[params.dataIndex % colors.length].slice(1, 3), 16)}, 
                                      ${parseInt(colors[params.dataIndex % colors.length].slice(3, 5), 16)}, 
                                      ${parseInt(colors[params.dataIndex % colors.length].slice(5, 7), 16)}, 0.8)`; // 80% 불투명도
                    },
                    opacity: 0.8, // 전체적인 투명도 적용
                    borderColor: "#000000", // 검은색 테두리
                    borderWidth: 1, // 테두리 두께
                    borderType: "solid", // 실선 테두리
                },
            },
        ],
    };
}

    // 기존 차트4: 도넛형 차트
    function createDonutChartOption(title, data) {
        const colors = [
            '#FFCE56', '#FF6384', '#36A2EB', '#4BC0C0', '#9966FF', '#FF9F40',
            '#C9DE00', '#F7464A', '#46BFBD', '#FDB45C', '#949FB1', '#4D5360',
            '#00A2E8', '#76A1E5', '#FAA43A', '#60BD68', '#F17CB0'
        ];
    
        return {
            title: { text: title, textStyle: { color: '#000000' } },
            backgroundColor: '#fefefe',
            tooltip: { trigger: 'item', formatter: '{b}: {d}%' },
            series: [
                {
                    name: title,
                    type: 'pie',
                    radius: ['40%', '70%'],
                    avoidLabelOverlap: false,
                    label: { show: true, formatter: '{b}\n{d}%', color: '#000000', fontSize: 12 },
                    emphasis: { label: { show: true, fontSize: '16', fontWeight: 'bold', formatter: '{b}\n{d}%' } },
                    labelLine: { show: true, length: 10, length2: 10 },
                    data: data,
                    itemStyle: {
                        color: (params) => colors[params.dataIndex % colors.length], // 색상 순환 적용
                        borderWidth: 2,   // ✅ 테두리 두께 (조각 사이 간격)
                        borderColor: '#ffffff' // ✅ 테두리 색상 (배경과 같은 색)
                    },
                },
            ],
        };
    }
    
    
    
    updateCharts('2023');
    oscillateView(chart1)
    console.log("ECharts Version:", echarts.version);
});
