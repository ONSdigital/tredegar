// Taxonomy base, data is injected. Data is loaded throug route service
(function() {
	'use strict';

	angular.module('onsTemplates')
		.controller('TaxonomyController', ['$scope', 'data', TaxonomyController])
		.controller('T1RepeatItemController', ['$scope', '$location', '$log', 'Downloader', 'Taxonomy', 'Chart', T1RepeatItemController])

	function TaxonomyController($scope, data) {
		var taxonomy = this
		taxonomy.data = data
		prepareBreadcrumb($scope, data)

		function prepareBreadcrumb($scope, data) {
			if (data.level === 't1') {
				return
			}
			$scope.breadcrumb = {}
			$scope.breadcrumb.parent = data.breadcrumb
			$scope.breadcrumb.current = data.name
		}

	}
	
	function T1RepeatItemController($scope, $location, $log, Downloader, Taxonomy, Chart) {
		var scopedTimeseries = $scope.item
        var ctrl = this
        ctrl.timeseries = scopedTimeseries
//        console.log('T1RepeatItemController scopedTimeseries data: ' + JSON.stringify(scopedTimeseries))
        ctrl.chartConfig = Chart.getSparkline(false)
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
        	console.log('chartType: ' + chartType)
            console.log("Changing chart type to " + chartType)
            ctrl.activeChart = chartType
            changeTimePeriod('A') //List all data on chart data change
        }


        function changeTimePeriod(timePeriod) {

            timePeriod = timePeriod || 'Custom'
            console.log("Changing time period to " + timePeriod)

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

            console.log("Chart:")
            console.log(ctrl.chartConfig)
            console.log("10y = " + ctrl.tenYears)


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
                console.log("From: ", from)
                console.log("To: ", to)
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
                
//                console.log('ctrl: ' + JSON.stringify(ctrl.timeseries))
                
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
                    console.log('ctrl.renderChart: ' + ctrl.renderChart)
                }

            }
        }
	}

})()