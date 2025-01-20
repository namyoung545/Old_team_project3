$(document).ready(() => {
    console.log('sh_chart.js');

    function initialize() {

        initAPIData();
    }

    function initAPIData() {
        console.log('initAPI')
        Promise.all([
            getFiresDataByStatName("화재발생건수"),
            getFiresDataByStatName("화재발생건수(원인) 전기적 요인"),
            getFiresDataByYearAndStatName('2023', '화재발생건수(원인)'),
            getFiresDataByStatName("사망자 수(원인) 전기적 요인"),
            getFiresDataByStatName("부상자 수(원인) 전기적 요인"),
        ]).then(([firesData, firesCauseED, firesCause2023, fireDeaths, fireInjuries]) => {
            initChart(firesData, firesCauseED, firesCause2023, fireDeaths, fireInjuries);
        }).catch((error) => {
            console.log(error);
        }).finally(() => {
            console.log('initAPI Promise Finally');
        });
    }

    function initChart(firesData, firesCauseED, firesCause2023, fireDeaths, fireInjuries) {
        callFiresChart(firesData, firesCauseED);
        callFireCauseChart(firesCause2023);
        callFireCasualtiesChart(fireDeaths, fireInjuries);
    }

    // 연도별 화재발생건수 차트
    function callFiresChart(firesData, firesCauseED) {
        const labels = [];
        const firesChartTitle = firesData[0].statName;
        const firesChartData = [];
        const edChartTitle = firesCauseED[0].statName;
        const edChartData = [];
        firesData.forEach(item => {
            labels.push(item.year);
            firesChartData.push(item.statValue);
        });
        firesCauseED.forEach(item => {
            edChartData.push(item.statValue);
        });

        let chartData = {
            labels: labels,
            datasets: [
                {
                    label: firesChartTitle,
                    data: firesChartData,
                    backgroundColor: 'rgba(243, 84, 97, 0.2)',
                    borderColor: 'rgb(243, 84, 97)',
                    borderWidth: 1,
                },
                {
                    label: edChartTitle,
                    data: edChartData,
                    backgroundColor: 'rgba(255, 230, 89, 0.83)',
                    borderColor: 'rgb(255, 230, 89)',
                    borderWidth: 1,
                }
            ]
        }

        let $chartFires = $('#chartFires');
        createChartGraph($chartFires, chartData, 'bar');
    }

    // 원인별 화재 차트
    function callFireCauseChart(firesCauseYear) {
        const labels = [];
        const firesCauseTitle = firesCauseYear[0].statName;
        const firesCauseData = [];
        firesCauseYear.forEach(item => {
            let labelString = item.statName.replace("화재발생건수(원인) ", "");
            labels.push(labelString);
            firesCauseData.push(item.statValue);
        });

        let chartData = {
            labels: labels,
            datasets: [
                {
                    label: firesCauseTitle,
                    data: firesCauseData,
                    backgroundColor: [
                        '#FFCE56', '#FF6384', '#36A2EB', '#4BC0C0', '#9966FF', '#FF9F40',
                        '#C9DE00', '#F7464A', '#46BFBD', '#FDB45C', '#949FB1', '#4D5360',
                        '#00A2E8', '#76A1E5', '#FAA43A', '#60BD68', '#F17CB0', '#B2912F',
                        '#B276B2', '#DECF3F', '#F15854', '#5DA5DA'
                    ]
                    // borderColor: 'rgb()',
                    // borderWidth: 1
                }
            ]
        }

        let chartOptions = {
            responsive: true,
            plugins: {
                legend: {
                    display: false, // 기본 라벨 숨기기
                },
                tooltip: {
                    callbacks: {
                        // 툴팁에 % 표시 추가
                        label: (tooltipItem) => {
                            const value = firesCauseData[tooltipItem.dataIndex];
                            const percentage = ((value / total) * 100).toFixed(1);
                            return `${labels[tooltipItem.dataIndex]}: ${value}건 (${percentage}%)`;
                        }
                    }
                }
            }

        }

        let $chartCause = $('#chartCause');
        createChartGraph($chartCause, chartData, 'doughnut', chartOptions);

        let $labelCause = $('#labelCause');
        $labelCause.empty(); // 기존 내용 초기화

        // 전체 데이터 합산 계산
        const total = firesCauseData.reduce((sum, value) => sum + value, 0);

        // 라벨과 비율을 div에 추가
        firesCauseData.forEach((value, index) => {
            const percentage = ((value / total) * 100).toFixed(1);
            const labelHtml = `
            <div class="label-item">
                <span class="label-name" style="color: ${chartData.datasets[0].backgroundColor[index]}">
                    ● ${labels[index]}
                </span>
                <span class="label-value">
                    ${value}건 (${percentage}%)
                </span>
            </div>
        `;
            $labelCause.append(labelHtml);
        });
    }


    // 인명피해 차트
    function callFireCasualtiesChart(fireDeaths, fireInjuries) {
        const labels = [];
        const firesChartTitle = fireDeaths[0].statName;
        const deathsData = [];
        const injuriesData = [];
        fireDeaths.forEach(item => {
            labels.push(item.year);
            deathsData.push(item.statValue);
        });
        fireInjuries.forEach(item => {
            injuriesData.push(item.statValue);
        });

        let chartData = {
            labels: labels,
            datasets: [
                {
                    label: fireDeaths[0].statName,
                    data: deathsData,
                    backgroundColor: 'rgba(255, 100, 100, 0.5)',
                    borderColor: 'rgb(255, 100, 100)',
                    borderWidth: 1,
                }, {
                    label: fireInjuries[0].statName,
                    data: injuriesData,
                    backgroundColor: 'rgb(255, 200, 100)',
                    borderColor: 'rgb(255,200,100)',
                    borderWidth: 1
                }
            ]
        }

        let chartCasualties = $('#chartInjuries');
        createChartGraph(chartCasualties, chartData, 'line');
    }

    // Chartjs Graph
    function createChartGraph($target, chartData, chartType = 'line', chartOptions = '') {
        // jQuery로 캔버스 요소를 가져옵니다.
        const ctx = $($target)[0].getContext('2d');
        const scales = {};

        // y축 설정이 chartData에 있을 경우 적용, 없으면 기본 설정을 사용
        if (chartData.yAxisConfig) {
            // chartData의 y축 설정을 사용
            Object.keys(chartData.yAxisConfig).forEach(axis => {
                scales[axis] = {
                    type: chartData.yAxisConfig[axis].type,
                    position: chartData.yAxisConfig[axis].position
                };
            });
        }

        if (!chartOptions) {
            chartOptions = {
                responsive: true,
                scales: scales
            }
        }
        console.log(chartOptions)

        // Chart.js로 그래프를 생성합니다.
        new Chart(ctx, {
            type: chartType,
            data: {
                labels: chartData.labels, // x축 레이블 (datetime)
                datasets: chartData.datasets // 데이터셋
            },
            options: chartOptions
        });
    }

    // Get Fires Data By StatName
    function getFiresDataByYear(year) {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: '/sh_api/fires',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ year: year }),
                success: (data) => {
                    resolve(data)
                },
                error: (error) => {
                    console.error("[ERROR] : ", error);
                    reject(error);
                }
            })
        });
    }

    // Get Fires Data By StatName
    function getFiresDataByStatName(statName) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: "/sh_api/firesByStatName",
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify({ statName: statName }),
                success: function (data) {
                    resolve(data);
                },
                error: function (error) {
                    console.error("Error:", error);
                    reject(error);
                }
            })
        })
    }

    // Get Fires Data By Year And StatName
    function getFiresDataByYearAndStatName(year, statName) {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: '/sh_api/firesByYearAndStatName',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ year: year, statName: statName }),
                success: (data) => {
                    resolve(data);
                },
                error: (error) => {
                    console.error('[ERROR] : ', error);
                    reject(error);
                }
            })
        })
    }

    initialize();
});