$(document).ready(function() {
    var yearPickerSlider = document.getElementById('year-picker');

    noUiSlider.create(yearPickerSlider, {
        start: [window.yearRange.startYear, window.yearRange.endYear],
        step: 1,
        range: {
            'min': [window.yearRange.startYear],
            'max': [window.yearRange.endYear]
        },
        format: {
          to: function ( value ) {
            return parseInt(value)
          },
          from: function ( value ) {
            return parseInt(value)
          }
        }
    });

    var nonLinearStepSliderValueElement = document.getElementById('year-picker-value');

    yearPickerSlider.noUiSlider.on('update', function (values) {
        nonLinearStepSliderValueElement.innerHTML = values.join(' - ');
    });

    yearPickerSlider.noUiSlider.on('change', function () {
        fetchMetroReport();
    })

    $('#results').DataTable(
        {
            order: [],
            ajax: {
                type: 'GET',
                url: '/api/v1/reports/metro',
                traditional: true,
                data: function(d) {
                    d.metricType = document.getElementById('metric-type').value,
                    d.startYear = yearPickerSlider.noUiSlider.get()[0],
                    d.endYear = yearPickerSlider.noUiSlider.get()[1],
                    d.leagueId =  $('input.form-check-input[type="checkbox"]:checked').map(function() { return this.value; }).get(),
                    d.minLastActiveYear = yearPickerSlider.noUiSlider.get()[1] - 1
                },
                dataSrc: function(json) {
                    const data = json.data;
                    const total = data.length;
                    data.forEach((obj, idx) => {
                        obj.rank = total - idx;
                    })
                    return data;
                }
            },
            columns: [
                { data: 'rank', type: 'num' },
                { data: 'name' },
                { data: 'total' },
                { data: 'opportunities' },
                { 
                    data: 'rate',
                    type: 'num-fmt',
                    render: function(data, type) {
                        if (typeof data === 'number') {
                            return (data * 100).toFixed(2) + '%';
                        }
                        return '';
                    } 
                },
            ],
            paging: false
        }
    );
});

function fetchMetroReport() {
     $('#results').DataTable().ajax.reload();
}