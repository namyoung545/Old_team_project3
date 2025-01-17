$(document).ready(() => {
    console.log('sh_chart.js');

    function initialize() {

        initAPIData();
    }

    function initAPIData() {
        console.log('initAPI')
        Promise.all([
            getFiresDataByStatName("화재발생건수"),
            getFiresDataByStatName("화재발생건수(원인) 전기적 요인")
        ]).then(([firesData, firesCauseED]) => {
            initChart(firesData, firesCauseED);
        }).catch(() => {

        }).finally(() => {

        });
    }

    function initChart(firesData, firesCauseED) {
        callfiresChart(firesData, firesCauseED);
        console.log(firesCauseED);
    }

    // 연도별 화재발생건수 차트
    function callfiresChart(firesData, firesCauseED) {
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

        let $chartData = {
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
        createChartGraph($chartFires, $chartData, 'bar');
    }

    // 원인별 화재 차트


    // Chartjs Graph
    function createChartGraph($target, chartData, chartType = 'line') {
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

        // Chart.js로 그래프를 생성합니다.
        new Chart(ctx, {
            type: chartType,
            data: {
                labels: chartData.labels, // x축 레이블 (datetime)
                datasets: chartData.datasets // 데이터셋
            },
            options: {
                responsive: true,
                scales: scales
            }
        });
    }

    // Get Fires Data
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

    initialize();
});