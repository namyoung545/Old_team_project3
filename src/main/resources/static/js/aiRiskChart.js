$(document).ready(function () {
    let chart;
    const ctx = document.getElementById('riskChart').getContext('2d');
    let fullData = [];
    let excludedCauses = [];

    // ✅ **서버에서 데이터 가져오기**
    $.getJSON("/aiRisk/data", function (riskData) {
        fullData = riskData;
        populateSelect(fullData);
        updateChart(fullData, "", []);
    });

    // ✅ **지역 선택 옵션 추가**
    function populateSelect(data) {
        let uniqueRegions = [...new Set(data.map(d => d.Region))];
        uniqueRegions.forEach(region => {
            $("#regionSelect").append(`<option value="${region}">${region}</option>`);
        });
    }

    // ✅ **지역별 총 위험점수를 기준으로 정규화**
    function normalizeDataByRegion(data, region) {
        let regionData = region ? data.filter(d => d.Region === region) : data;
        let regionTotalRisk = regionData.reduce((sum, d) => sum + parseFloat(d["Reduction (%)"]), 0);

        return regionData.map(d => ({
            ...d,
            "Reduction (%)": ((parseFloat(d["Reduction (%)"]) / regionTotalRisk) * 100).toFixed(2)
        }));
    }

    // ✅ **원인을 그룹화하여 중복 제거**
    function aggregateByCause(data, excludeCauses = []) {
        let grouped = {};
        data.forEach(d => {
            if (excludeCauses.includes(d.Cause)) return;
            if (!grouped[d.Cause]) {
                grouped[d.Cause] = { Cause: d.Cause, Reduction: 0 };
            }
            grouped[d.Cause].Reduction += parseFloat(d["Reduction (%)"]);
        });

        return Object.values(grouped);
    }

    // ✅ **제거된 원인의 기여도를 남은 요인에 비례하여 재분배**
    function redistributeReduction(data, excludeCauses) {
        let filteredData = data.filter(d => !excludeCauses.includes(d.Cause));
        let remainingTotal = filteredData.reduce((sum, d) => sum + parseFloat(d.Reduction), 0);

        return filteredData.map(d => ({
            Cause: d.Cause,
            Reduction: ((parseFloat(d.Reduction) / remainingTotal) * 100).toFixed(2)
        }));
    }

    // ✅ **막대 위 숫자 플러그인 (특정 차트에만 적용)**
    function createDataLabelsPlugin() {
        return {
            id: 'dataLabelsPlugin',
            afterDatasetsDraw(chart) {
                if (!chart || !chart.ctx) return;
                const ctx = chart.ctx;
                ctx.save();
                chart.data.datasets.forEach((dataset, i) => {
                    const meta = chart.getDatasetMeta(i);
                    meta.data.forEach((bar, index) => {
                        const value = dataset.data[index];
                        if (value !== undefined) {
                            ctx.fillStyle = '#000';
                            ctx.font = '10px Arial';
                            ctx.textAlign = 'center';
                            ctx.fillText(`${value}%`, bar.x, bar.y - 10);
                        }
                    });
                });
                ctx.restore();
            }
        };
    }

    // ✅ **X축 한글 세로쓰기 플러그인 (특정 차트에만 적용)**
    function createXAxisVerticalTextPlugin() {
        return {
            id: 'xAxisVerticalText',
            afterDraw(chart) {
                if (!chart.scales.x || !chart.scales.x.ticks) return;
                const ctx = chart.ctx;
                ctx.save();

                chart.scales.x.ticks.forEach((tick, i) => {
                    let label = chart.data.labels[i];
                    if (!label) return;

                    let x = chart.scales.x.getPixelForTick(i);
                    let y = chart.scales.x.bottom + 10;

                    ctx.fillStyle = "#000";
                    ctx.font = "10px Arial";
                    ctx.textAlign = "center";

                    label.split("").forEach((char, j) => {
                        ctx.fillText(char, x, y + j * 10);
                    });
                });

                ctx.restore();
            }
        };
    }

    // ✅ **차트 업데이트 함수**
    function updateChart(riskData, region, excludeCauses) {
        let filteredData = normalizeDataByRegion(riskData, region);
        let aggregatedData = aggregateByCause(filteredData, excludeCauses);

        if (excludeCauses.length > 0) {
            aggregatedData = redistributeReduction(aggregatedData, excludeCauses);
        }

        aggregatedData.sort((a, b) => b.Reduction - a.Reduction);
        const labels = aggregatedData.map(d => d.Cause);
        const values = aggregatedData.map(d => parseFloat(d.Reduction).toFixed(2));

        if (chart) {
            chart.destroy();
        }

        chart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: '위험 감소 기여도',
                    data: values,
                    backgroundColor: 'rgba(54, 162, 235, 0.6)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 2,
                    barPercentage: 0.7,
                    categoryPercentage: 1
                }]
            },
            options: {
                responsive: true,
                layout: { padding: { bottom: 150 } },
                plugins: {
                    tooltip: { enabled: false },
                    legend: { display: false }
                },
                scales: {
                    x: {
                        ticks: { display: false }
                    },
                    y: { beginAtZero: true, max: 100 }
                },
                onClick(event, elements) {
                    if (!elements || elements.length === 0) {
                        console.warn("클릭한 위치에 데이터가 없습니다.");
                        return;
                    }

                    let index = elements[0].index;
                    let clickedLabel = chart.data.labels[index];

                    excludedCauses.push(clickedLabel);
                    updateChart(fullData, region, excludedCauses);
                }
            },
            plugins: [createDataLabelsPlugin(), createXAxisVerticalTextPlugin()]
        });
    }

    // ✅ **지역 변경 이벤트**
    $("#regionSelect").change(function () {
        let selectedRegion = $(this).val();
        excludedCauses = [];
        updateChart(fullData, selectedRegion, excludedCauses);
    });
});
