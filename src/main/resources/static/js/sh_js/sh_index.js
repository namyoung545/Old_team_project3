$(document).ready(function () {
    console.log('sh_index.js');
    // 년도 검색
    $('#search-year-btn').on('click', function () {
        console.log('click-year-btn')
        const year = $('#year').val();

        // POST 요청
        $.ajax({
            url: '/sh_api/fires',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ year: year }),
            success: function (data) {
                console.log(data);
                // JSON 데이터를 테이블에 추가
                const rows = data.map(item => `
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.year}</td>
                        <td>${item.statName}</td>
                        <td>${item.statValue}</td>
                    </tr>
                `).join('');
                $('#data-rows').html(rows);
            },
            error: function () {
                alert('데이터를 불러오는 데 실패했습니다.');
            }
        });
    });

    // 통계명 검색
    $('#search-statName-btn').on('click', function () {
        console.log('click-statName-btn');
        const statName = $('#statName').val();

        $.ajax({
            url: '/sh_api/firesByStatName',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ statName: statName }),
            success: function (data) {
                console.log(data);
                const rows = data.map(item => `
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.year}</td>
                        <td>${item.statName}</td>
                        <td>${item.statValue}</td>
                    </tr>
               `).join('');
               $('#data-rows').html(rows);
            },
            error: function () {
                console.log('데이터를 불러오는데 실패했습니다.');
                const rows = `
                    <tr>
                        <td></td>
                        <td></td>
                        <td>데이터를 불러오는데 실패했습니다.</td>
                        <td></td>
                    </tr>
               `;
               $('#data-rows').html(rows);
            }
        });
    });

    // 통합 검색
    $('#search-combined-btn').on('click', function () {
        console.log('click-combined-btn');
        const year = $('#year').val();
        const statName = $('#statName').val();

        $.ajax({
            url: '/sh_api/firesByYearAndStatName',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ year: year, statName: statName }),
            success: function (data) {
                console.log(data);
                const rows = data.map(item => `
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.year}</td>
                        <td>${item.statName}</td>
                        <td>${item.statValue}</td>
                    </tr>
               `).join('');
               $('#data-rows').html(rows);
            },
            error: function () {
                console.log('데이터를 불러오는데 실패했습니다.');
                const rows = `
                    <tr>
                        <td></td>
                        <td></td>
                        <td>데이터를 불러오는데 실패했습니다.</td>
                        <td></td>
                    </tr>
               `;
               $('#data-rows').html(rows);
            }
        });
    });
});
