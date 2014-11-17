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
        var t1 = this
        t1.timeseries = scopedTimeseries
        console.log('T1RepeatItemController scopedTimeseries data: ' + JSON.stringify(scopedTimeseries))
        t1.chartConfig = Chart.getSparkline(false)
        t1.showYearly = false
        t1.showMonthly = false
        t1.showQuarterly = false
        t1.activeChart = '' //years, months , quarters
        t1.timePeriod = 'A' //All by default
        t1.chartData = []
        t1.years = []
        t1.showCustomFilters = false
        t1.chartVisible = true
        t1.tableVisible = false
        t1.renderChart = false
        t1.tenYears
        t1.quarters = ['Q1', 'Q2', 'Q3', 'Q4']
        t1.months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

        initialize()
        changeChartType(t1.activeChart)

        function changeChartType(chartType) {
        	console.log('chartType: ' + chartType)
            console.log("Changing chart type to " + chartType)
            t1.activeChart = chartType
            changeTimePeriod('A') //List all data on chart data change
        }


        function changeTimePeriod(timePeriod) {

            timePeriod = timePeriod || 'Custom'
            console.log("Changing time period to " + timePeriod)

            t1.timePeriod = timePeriod
            prepareData()
        }

        function prepareData() {
            if (!t1.renderChart) {
                return
            }
            resolveFilters()
            t1.chartData = filterValues()
            t1.chartConfig.series[0].data = t1.chartData
            t1.years = getYears()
            t1.tenYears = tenYears(t1.years)
            t1.chartConfig.options.xAxis.tickInterval = tickInterval(t1.chartData.length);

            console.log("Chart:")
            console.log(t1.chartConfig)
            console.log("10y = " + t1.tenYears)


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

                from = t1.fromYear + (t1.fromQuarter ? Chart.quarterVal(t1.fromQuarter) : '') + (t1.fromMonth ? Chart.monthVal(t1.fromMonth) : '')
                to = t1.toYear + (t1.toQuarter ? Chart.quarterVal(t1.toQuarter) : '') + (t1.toMonth ? Chart.monthVal(t1.toMonth) : '')
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
                switch (t1.timePeriod) {

                    case 'A': //All
                        t1.fromYear = first.year
                        t1.fromMonth = first.month ? first.month.slice(0, 3) : first.month
                        t1.fromQuarter = first.quarter
                        break
                    case '10':
                        if (tenYearsAgo < first.year) { //Use first if within 10 years
                            t1.fromYear = first.year
                            t1.fromMonth = first.month ? first.month.slice(0, 3) : first.month
                            t1.fromQuarter = first.quarter
                        } else {
                            t1.fromYear = '' + tenYearsAgo
                            t1.fromQuarter = isActive('quarters') ? 'Q1' : undefined
                            t1.fromMonth = isActive('months') ? 'Jan' : undefined
                        }
                        break
                    case '5':
                        if (fiveYearsAgo < first.year) { //Use first if within 10 years
                            t1.fromYear = first.year
                            t1.fromMonth = first.month ? first.month.slice(0, 3) : first.month
                            t1.fromQuarter = first.quarter
                        } else {
                            t1.fromYear = '' + fiveYearsAgo
                            t1.fromQuarter = isActive('quarters') ? 'Q1' : undefined
                            t1.fromMonth = isActive('months') ? 'Jan' : undefined
                        }
                        break
                    case 'Custom':
                        return
                    default:

                }

                t1.toYear = last.year
                t1.toMonth = last.month ? last.month.slice(0, 3) : last.month
                t1.toQuarter = last.quarter
            }
        }


        function toggleCustomFilters() {
            t1.showCustomFilters = !t1.showCustomFilters
        }

        function isActive(chartType) {
            return chartType === t1.activeChart
        }


        function showTable() {
            if (t1.tableVisible) {
                return
            }

            t1.tableVisible = true
            t1.chartVisible = false
        }

        function showChart() {
            if (t1.chartVisible) {
                return
            }
            t1.chartVisible = true
            t1.tableVisible = false
        }

        function getAllValues() {
            return t1.timeseries[t1.activeChart].values
        }

        function getYears() {
            return t1.timeseries[t1.activeChart].years
        }

        //Initialize controller and configuration
        function initialize() {
            resolveChartTypes()
            t1.chartConfig.series[0].name = t1.timeseries.name
            prepareData()

            function resolveChartTypes() {
                var data = t1.timeseries
                
//                console.log('t1: ' + JSON.stringify(t1.timeseries))
                
                t1.showYearly = Chart.isNotEmpty(data.years)
                t1.showMonthly = Chart.isNotEmpty(data.months)
                t1.showQuarterly = Chart.isNotEmpty(data.quarters)
                
                if (t1.showMonthly) {
                    t1.activeChart = 'months'
                    data.months = Chart.formatData(data.months)
                }

                if (t1.showQuarterly) {
                    t1.activeChart = 'quarters'
                    data.quarters = Chart.formatData(data.quarters)

                }

                if (t1.showYearly) {
                    t1.activeChart = 'years'
                    data.years = Chart.formatData(data.years)
                }

                if ((t1.showMonthly || t1.showYearly || t1.showQuarterly)) {
                    t1.renderChart = true
                    console.log('t1.renderChart: ' + t1.renderChart)
                }

            }
        }
	}

})()