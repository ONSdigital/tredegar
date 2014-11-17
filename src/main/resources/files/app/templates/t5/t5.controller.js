(function() {

    angular.module('onsTemplates')
        .controller('T5Controller', ['$scope', '$log', 'Downloader', 'DataLoader', T5Controller])
        .controller('ChartController', ['$scope', '$location', '$log', 'Downloader', 'ArrayUtil', 'Chart', ChartController])

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


    function ChartController($scope, $location, $log, Downloader, ArrayUtil, Chart) {
        var ctrl = this
        ctrl.timeseries = $scope.taxonomy.data
        ctrl.chartConfig = Chart.getChart()
        ctrl.showYearly = false
        ctrl.showMonthly = false
        ctrl.showQuarterly = false
        ctrl.activeChart = '' //years, months , quarters
        ctrl.timePeriod = 'A' //All by default
        ctrl.chartData = []
        ctrl.years = []
        ctrl.showCustomFilters = false
        ctrl.chartVisible = true
        ctrl.tableVisible = false
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
                if ((Chart.getLast(array) - Chart.getFirst(array)) < 10) {
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

                from = ctrl.fromYear + (ctrl.fromQuarter ? Chart.quarterVal(ctrl.fromQuarter) : '') + (ctrl.fromMonth ? Chart.monthVal(ctrl.fromMonth) : '')
                to = ctrl.toYear + (ctrl.toQuarter ? Chart.quarterVal(ctrl.toQuarter) : '') + (ctrl.toMonth ? Chart.monthVal(ctrl.toMonth) : '')
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
                var first = Chart.getFirst(getAllValues())
                var last = Chart.getLast(getAllValues())
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


        function toggleCustomFilters() {
            ctrl.showCustomFilters = !ctrl.showCustomFilters
        }

        function isActive(chartType) {
            return chartType === ctrl.activeChart
        }


        function showTable() {
            if (ctrl.tableVisible) {
                return
            }

            ctrl.tableVisible = true
            ctrl.chartVisible = false
        }

        function showChart() {
            if (ctrl.chartVisible) {
                return
            }
            ctrl.chartVisible = true
            ctrl.tableVisible = false
        }

        function getAllValues() {
            return ctrl.timeseries[ctrl.activeChart].values
        }

        function getYears() {
            return ctrl.timeseries[ctrl.activeChart].years
        }

        //Initialize controller and configuration
        function initialize() {
            resolveChartTypes()
            ctrl.chartConfig.series[0].name = ctrl.timeseries.name
            prepareData()

            function resolveChartTypes() {
                var data = ctrl.timeseries
                ctrl.showYearly = Chart.isNotEmpty(data.years)
                ctrl.showMonthly = Chart.isNotEmpty(data.months)
                ctrl.showQuarterly = Chart.isNotEmpty(data.quarters)

                if (ctrl.showMonthly) {
                    ctrl.activeChart = 'months'
                    data.months = Chart.formatData(data.months)
                }

                if (ctrl.showQuarterly) {
                    ctrl.activeChart = 'quarters'
                    data.quarters = Chart.formatData(data.quarters)

                }

                if (ctrl.showYearly) {
                    ctrl.activeChart = 'years'
                    data.years = Chart.formatData(data.years)
                }

                if ((ctrl.showMonthly || ctrl.showYearly || ctrl.showQuarterly)) {
                    ctrl.renderChart = true
                }

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
            console.log(chartExp)
            chartExp.exportChart();
        }

        angular.extend(ctrl, {
            isActive: isActive,
            changeChartType: changeChartType,
            showTable: showTable,
            showChart: showChart,
            changeTimePeriod: changeTimePeriod,
            toggleCustomFilters: toggleCustomFilters,
            downloadCsv:downloadCsv,
            downloadXls:downloadXls,
            exportImage: exportImage
        })
    }
})();
