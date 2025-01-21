document.addEventListener("DOMContentLoaded", function () {
    const yearSelect = document.getElementById("year-select");

    // 기본값 설정
    if (yearSelect) {
        yearSelect.value = "2023";
    }
    // 선택 변경 이벤트 처리
    yearSelect.addEventListener("change", function () {
        const selectedYear = yearSelect.value;
        console.log(`Selected Year: ${selectedYear}`);
        updateCharts(selectedYear); // 차트 업데이트 로직 호출
    });

    const riskData = JSON.parse(document.getElementById("jsonData").textContent);

    const chart1 = echarts.init(document.getElementById('chart1'));
    const chart2 = echarts.init(document.getElementById('chart2'));
    const chart3 = echarts.init(document.getElementById('chart3'));
    const chart4 = echarts.init(document.getElementById('chart4'));

    function updateCharts(selectedYear) {
        const filteredData = riskData.filter(item => item.year === parseInt(selectedYear));

        if (filteredData.length === 0) {
            chart1.clear();
            chart2.clear();
            chart3.clear();
            chart4.clear();
            return;
        }

        const regions = filteredData.map(item => item.region);
        const totalIncidents = filteredData.map((item, index) => [index, 0, item.totalIncidents]);
        const totalDamage = filteredData.map(item => item.totalDamage);
        const riskLevels = filteredData.map(item => item.riskLevel);
        const riskScores = filteredData.map(item => ({
            name: item.region,
            value: item.riskScore,
        }));

        // 총 재해발생건수 - 막대그래프
        chart1.setOption(create3DBarOption('연간 총 재해건수', regions, totalIncidents));

        // 총 피해금액 - 점선그래프
        chart2.setOption(createLineChartOption('연간 총 피해금액', regions, totalDamage));

        // 위험레벨 - 3D 막대그래프, Y 값은 3, 2, 1만 표시
        chart3.setOption(createRiskLevelChartOption('연간 위험도 레벨(3:고위험, 2:중위험, 1:저위험)', regions, riskLevels));

        // 위험점수 - 도넛형 차트
        chart4.setOption(createDonutChartOption('연간 위험도 점수비율', riskScores));
    }

    function create3DBarOption(title, categories, data) {
        return {
            title: { text: title, textStyle: { color: '#ffffff' } },
            backgroundColor: '#1e1e1e',
            tooltip: {},
            xAxis3D: {
                type: 'category',
                data: categories,
                axisLabel: { color: '#ffffff' },
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
                },
                boxWidth: 200,
                boxHeight: 80,
                boxDepth: 80,
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
                            const colors = ['#FF6F61', '#6BAED6', '#74C476', '#FD8D3C'];
                            return colors[params.dataIndex % colors.length];
                        },
                    },
                },
            ],
        };
    }

    function createLineChartOption(title, categories, data) {
        return {
            title: { text: title, textStyle: { color: '#ffffff' } },
            backgroundColor: '#1e1e1e',
            tooltip: { trigger: 'axis' },
            xAxis: {
                type: 'category',
                data: categories,
                axisLabel: { color: '#ffffff' },
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
                },
            ],
        };
    }

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
                axisLabel: { color: '#ffffff' },
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

    // 초기 업데이트
    updateCharts('2023');
});
