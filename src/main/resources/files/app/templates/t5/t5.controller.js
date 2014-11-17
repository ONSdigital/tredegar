(function() {

    angular.module('onsTemplates')
        .controller('T5Controller', ['$scope', '$log', 'Downloader', 'DataLoader', T5Controller])
        .controller('ChartController', ['$scope', '$location', '$log', 'Downloader', 'ArrayUtil', ChartController])

    function T5Controller($scope, $log, Downloader, DataLoader) {

        var t5 = this;
        $scope.header = "Time Series";
        $scope.sidebar = true;
        $scope.sidebarUrl = "/app/templates/t5/t5sidebar.html";

        var data = $scope.taxonomy.data
        data.relatedBulletinData = []
        loadRelatedBulletins(data)

        function downloadXls() {
            download('xlsx');
        }

        function downloadCsv() {
            download('csv');
        }

        function download(type) {
            var downloadRequest = {
                type: type
            };
            downloadRequest.uriList = [$scope.getPath()];
            var fileName = $scope.getPage() + '.' + downloadRequest.type;
            Downloader.downloadFile(downloadRequest, fileName);
        }

        function loadRelatedBulletins(data) {
            var dataPath = '/data'
            var bulletins = data.relatedBulletins;

            if (bulletins != null) {
                for (var i = 0; i < bulletins.length; i++) {
                    var bulletin = bulletins[i]
                    var relatedBulletinPath = dataPath + bulletin
                    DataLoader.load(relatedBulletinPath)
                        .then(function(relatedBulletin) {
                            $log.debug('Loaded related bulletin: ', relatedBulletinPath, ' ', relatedBulletin)
                            data.relatedBulletinData.push(relatedBulletin)
                        })
                }
            }
        }

        angular.extend(t5, {
            downloadXls: downloadXls,
            downloadCsv: downloadCsv
        });

    }


    function ChartController($scope, $location, $log, Downloader, ArrayUtil) {
        var ctrl = this
        ctrl.timeseries = $scope.taxonomy.data
        ctrl.chartConfig = getChart()
        ctrl.showYearly = false
        ctrl.showMonthly = false
        ctrl.showQuarterly = false
        ctrl.activeChart = '' //years, months , quarters
        ctrl.timePeriod = 'A' //All by default
        ctrl.chartData = []
        ctrl.years = []
        ctrl.renderChart = false
        ctrl.tenYears
        ctrl.quarters = ['Q1', 'Q2', 'Q3', 'Q4']
        ctrl.months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

        initialize()
        changeChartType(ctrl.activeChart)

        function changeChartType(chartType) {
            $log.debug("Changing chart type to " + chartType)
            ctrl.activeChart = chartType
            changeTimePeriod('A') //List all data on chart data change
        }


        function changeTimePeriod(timePeriod) {

            timePeriod = timePeriod || 'Custom'
            $log.debug("Changing time period to " + timePeriod)

            ctrl.timePeriod = timePeriod
            prepareData()
        }

        function prepareData() {
            if (!ctrl.renderChart) {
                return
            }
            resolveFilters()
            ctrl.chartData = filterValues()
            ctrl.chartConfig.series[0].data = ctrl.chartData
            ctrl.years = getYears()
            ctrl.tenYears = tenYears(ctrl.years)
            ctrl.chartConfig.options.xAxis.tickInterval = tickInterval(ctrl.chartData.length);
            ctrl.chartConfig.options.title.text = ctrl.timeseries.name
            ctrl.chartConfig.options.yAxis.title.text = ctrl.timeseries.unit
            $log.debug("Chart:")
            $log.debug(ctrl.chartConfig)
            $log.debug("10y = " + ctrl.tenYears)


            function tenYears(array) {
                if ((ArrayUtil.getLast(array) - ArrayUtil.getFirst(array)) < 10) {
                    return false
                } else {
                    return true
                }
            }

            function tickInterval(length) {
                var tick;
                if (length <= 20) {
                    return 1;
                } else if (length <= 80) {
                    return 4;
                } else if (length <= 240) {
                    return 12;
                } else if (length <= 480) {
                    return 48;
                } else if (length <= 960) {
                    return 96;
                } else {
                    return 192;
                }
            }

            function filterValues() {
                var data = getAllValues()
                var current
                var i
                var filteredValues = []
                var from
                var to

                from = ctrl.fromYear + (ctrl.fromQuarter ? quarterVal(ctrl.fromQuarter) : '') + (ctrl.fromMonth ? monthVal(ctrl.fromMonth) : '')
                to = ctrl.toYear + (ctrl.toQuarter ? quarterVal(ctrl.toQuarter) : '') + (ctrl.toMonth ? monthVal(ctrl.toMonth) : '')
                from = +from //Cast to number
                to = +to
                $log.debug("From: ", from)
                $log.debug("To: ", to)
                for (i = 0; i < data.length; i++) {
                    current = data[i]
                    if (current.value >= from && current.value <= to) {
                        filteredValues.push(current)
                    }

                }
                return filteredValues
            }

            function resolveFilters() {
                var first = ArrayUtil.getFirst(getAllValues())
                var last = ArrayUtil.getLast(getAllValues())
                var now = new Date()
                var currentYear = now.getFullYear()
                var tenYearsAgo = currentYear - 10
                var fiveYearsAgo = currentYear - 5
                switch (ctrl.timePeriod) {

                    case 'A': //All
                        ctrl.fromYear = first.year
                        ctrl.fromMonth = first.month ? first.month.slice(0, 3) : first.month
                        ctrl.fromQuarter = first.quarter
                        break
                    case '10':
                        if (tenYearsAgo < first.year) { //Use first if within 10 years
                            ctrl.fromYear = first.year
                            ctrl.fromMonth = first.month ? first.month.slice(0, 3) : first.month
                            ctrl.fromQuarter = first.quarter
                        } else {
                            ctrl.fromYear = '' + tenYearsAgo
                            ctrl.fromQuarter = isActive('quarters') ? 'Q1' : undefined
                            ctrl.fromMonth = isActive('months') ? 'Jan' : undefined
                        }
                        break
                    case '5':
                        if (fiveYearsAgo < first.year) { //Use first if within 10 years
                            ctrl.fromYear = first.year
                            ctrl.fromMonth = first.month ? first.month.slice(0, 3) : first.month
                            ctrl.fromQuarter = first.quarter
                        } else {
                            ctrl.fromYear = '' + fiveYearsAgo
                            ctrl.fromQuarter = isActive('quarters') ? 'Q1' : undefined
                            ctrl.fromMonth = isActive('months') ? 'Jan' : undefined
                        }
                        break
                    case 'Custom':
                        return
                    default:

                }

                ctrl.toYear = last.year
                ctrl.toMonth = last.month ? last.month.slice(0, 3) : last.month
                ctrl.toQuarter = last.quarter
            }
        }

        function isActive(chartType) {
            return chartType === ctrl.activeChart
        }

        function getAllValues() {
            return ctrl.timeseries[ctrl.activeChart].values
        }

        function getYears() {
            return ctrl.timeseries[ctrl.activeChart].years
        }

        //Initialize controller and configuration
        function initialize() {
            resolveChartTypes(ArrayUtil)
            ctrl.chartConfig.series[0].name = ctrl.timeseries.name
            prepareData()

            function resolveChartTypes(ArrayUtil) {
                var data = ctrl.timeseries
                ctrl.showYearly = ArrayUtil.isNotEmpty(data.years)
                ctrl.showMonthly = ArrayUtil.isNotEmpty(data.months)
                ctrl.showQuarterly = ArrayUtil.isNotEmpty(data.quarters)

                if (ctrl.showMonthly) {
                    ctrl.activeChart = 'months'
                    data.months = formatData(data.months)
                }

                if (ctrl.showQuarterly) {
                    ctrl.activeChart = 'quarters'
                    data.quarters = formatData(data.quarters)

                }

                if (ctrl.showYearly) {
                    ctrl.activeChart = 'years'
                    data.years = formatData(data.years)
                }

                if ((ctrl.showMonthly || ctrl.showYearly || ctrl.showQuarterly)) {
                    ctrl.renderChart = true
                }

            }

            //Format data into high charts compatible format
            function formatData(timeseriesValues) {
                var data = {
                    values: [],
                    years: []
                }
                var current
                var i

                for (i = 0; i < timeseriesValues.length; i++) {
                    current = timeseriesValues[i]
                    data.values.push(enrichData(current, i))
                    data.years.push(current.year)
                }
                ArrayUtil.toUnique(data.years)
                return data
            }

            function enrichData(timeseriesValue) {
                var quarter = timeseriesValue.quarter
                var year = timeseriesValue.year
                var month = timeseriesValue.month

                timeseriesValue.y = +timeseriesValue.value //Cast to number
                timeseriesValue.value = +(year + (quarter ? quarterVal(quarter) : '') + (month ? monthVal(month) : ''))
                timeseriesValue.name = timeseriesValue.date //Appears on x axis
                delete timeseriesValue.date

                return timeseriesValue
            }
        }



        function monthVal(mon) {
            switch (mon.slice(0, 3).toUpperCase()) {
                case 'JAN':
                    return 01
                case 'FEB':
                    return 02
                case 'MAR':
                    return 03
                case 'APR':
                    return 04
                case 'MAY':
                    return 05
                case 'JUN':
                    return 06
                case 'JUL':
                    return 07
                case 'AUG':
                    return 08
                case 'SEP':
                    return 09
                case 'OCT':
                    return 10
                case 'NOV':
                    return 11
                case 'DEC':
                    return 12
                default:
                    throw 'Invalid Month:' + mon

            }
        }

        function quarterVal(quarter) {
            switch (quarter) {
                case 'Q1':
                    return 1
                case 'Q2':
                    return 2
                case 'Q3':
                    return 3
                case 'Q4':
                    return 4
                default:
                    throw 'Invalid Quarter:' + quarter

            }
        }


        function downloadXls() {
            download('xlsx');
        }

        function downloadCsv() {
            download('csv');
        }

        function download(type) {
            var downloadRequest = {
                type: type,
                from: {
                    year: ctrl.fromYear,
                    month: ctrl.fromMonth,
                    quarter: ctrl.fromQuarter
                },
                to: {
                    year: ctrl.toYear,
                    month: ctrl.toMonth,
                    quarter: ctrl.toQuarter
                }

            };
            downloadRequest.uriList = [$scope.getPath()];
            var fileName = $scope.getPage() + '.' + downloadRequest.type;
            Downloader.downloadFile(downloadRequest, fileName);
        }


        function exportImage() {
            var chartExp = $('#chart_prices').highcharts();
            chartExp.exportChart();
        }

        angular.extend(ctrl, {
            isActive: isActive,
            changeChartType: changeChartType,
            changeTimePeriod: changeTimePeriod,
            downloadCsv: downloadCsv,
            downloadXls: downloadXls,
            exportImage: exportImage
        })
    }

    function getChart() {
        var data = {
            options: {
                chart: {
                    type: 'line'
                },
                colors: ['#007dc3', '#409ed2', '#7fbee1', '#007dc3', '#409ed2', '#7fbee1'],

                title: {
                    text: ''
                },
                subtitle: {
                    text: ''
                },
                navigation: {
                    buttonOptions: {
                        enabled: false
                    }
                },
                xAxis: {
                    categories: [],
                    // tickInterval: 1,
                    labels: {
                        formatter: function() {
                            var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
                            var response = "";
                            if (w < 768) {
                                if (this.isFirst) {
                                    count = 0;
                                }
                                if (count % 3 === 0) {
                                    response = this.value;
                                }
                                count++;
                            } else {
                                response = this.value;
                            }
                            return response;
                        },
                    },
                    tickmarkPlacement: 'on'
                },
                yAxis: {
                    title: {
                        align: 'high',
                        rotation: 0,
                        textAlign: 'left',
                        x: 10,
                        y: -10
                    }
                },

                credits: {
                    enabled: false
                },

                plotOptions: {
                    series: {
                        shadow: false,
                        states: {
                            hover: {
                                enabled: true,
                                shadow: false,
                                lineWidth: 3,
                                lineWidthPlus: 0,
                                marker: {
                                    height: 0,
                                    width: 0,
                                    halo: false,
                                    enabled: true,
                                    fillColor: null,
                                    radiusPlus: null,
                                    lineWidth: 3,
                                    lineWidthPlus: 0
                                }
                            }
                        }
                    }
                },
                tooltip: {
                    shared: true,
                    width: '150px',
                    crosshairs: {
                        width: 2,
                        color: '#f37121'
                    },
                    positioner: function(labelWidth, labelHeight, point) {
                        var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
                        var points = {
                            x: 30,
                            y: 42
                        };
                        var tooltipX, tooltipY;
                        var chart = Highcharts.charts[Highcharts.charts.length - 1];
                        if (w > 768) {

                            if (point.plotX + labelWidth > chart.plotWidth) {
                                tooltipX = point.plotX + chart.plotLeft - labelWidth - 20;
                                $("#custom-tooltip").removeClass('tooltip-left');
                            } else {
                                tooltipX = point.plotX + chart.plotLeft + 20;
                                $("#custom-tooltip").removeClass('tooltip-right');
                            }

                            tooltipY = 50;
                            points = {
                                x: tooltipX,
                                y: tooltipY
                            };
                        } else {
                            $("#custom-tooltip").removeClass('tooltip-left');
                            $("#custom-tooltip").removeClass('tooltip-right');
                        }

                        return points;
                    },

                    formatter: function() {
                        var id = '<div id="custom-tooltip" class="tooltip-left tooltip-right">';
                        var block = id + "<div class='sidebar' >";
                        var title = '<b class="title">' + this.points[0].key + ': </b><br/>';
                        var symbol = ['<div class="circle">●</div>', '<div class="square">■</div>', '<div class="diamond">♦</div>', '<div class="triangle">▲</div>', '<div class="triangle">▼</div>'];

                        var content = block + "<div class='title'>&nbsp;</div>";

                        // symbols
                        $.each(this.points, function(i, val) {
                            content += symbol[i];
                        });

                        content += "</div>";
                        content += "<div class='mainText'>";
                        content += title;

                        // series names and values
                        $.each(this.points, function(i, val) {
                            content += '<div class="tiptext"><i>' + val.point.series.chart.series[i].name + "</i><br/><b>Value: " + Highcharts.numberFormat(val.y, 2) + '</b></div>';
                        });
                        content += "</div>";
                        return content;
                    },

                    backgroundColor: 'rgba(255, 255, 255, 0)',
                    borderWidth: 0,
                    borderColor: 'rgba(255, 255, 255, 0)',
                    shadow: false,
                    useHTML: true

                }
            },

            series: [{
                name: "",
                data: [],
                marker: {
                    symbol: "circle",
                    states: {
                        hover: {
                            fillColor: '#007dc3',
                            radiusPlus: 0,
                            lineWidthPlus: 0
                        }
                    }
                },
                dashStyle: 'Solid',
            }]
        };
        return data;
    }

})();