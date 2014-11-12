(function() {

	angular.module('onsTemplates')
		.controller('T3Controller', ['$scope', '$http', 'Downloader', T3Controller])
		.controller('T3ChartController', ['$scope', '$location', '$log', 'Downloader', 'Taxonomy', 'Chart', T3ChartController])
		.controller('T3RepeatItemController', ['$scope', '$location', '$log', 'Downloader', 'Taxonomy', 'Chart', T3RepeatItemController])
		.directive('stSelectAll', [SelectAllDirective])
		.directive('stSelect', [SelectDirective])


	function T3Controller($scope, $http, Downloader, $log) {
		var t3 = this
		var items = $scope.taxonomy.data.itemData
		t3.allSelected = false
		t3.selectedCount = 0

		function toggleSelectAll() {
			t3.allSelected = !t3.allSelected
			for (var i = 0; i < items.length; i++) {
				items[i].isSelected = t3.allSelected
			};

			t3.selectedCount = t3.allSelected ? items.length : 0
		}

		function toggleSelect(row) {
			row.isSelected = !row.isSelected
			if (row.isSelected) {
				t3.selectedCount++
			} else {
				t3.selectedCount--
			}

		}

		function downloadXls() {
			if (t3.selectedCount <= 0) {
				return
			}

			download('xlsx')
		}

		function downloadCsv() {
			if (t3.selectedCount <= 0) {
				return
			}
			download('csv')
		}

		function download(type) {
			var downloadRequest = {
				type: type
			}
			downloadRequest.uriList = getFileList()
			var fileName = $scope.getPage() + '.' + downloadRequest.type;
			Downloader.downloadFile(downloadRequest,fileName)
			
		}

		function getFileList() {
			var uriList = []
			for (var i = 0; i < items.length; i++) {
				if (items[i].isSelected) {
					uriList.push(items[i].url)
				}
			}
			return uriList
		}
		
		angular.extend(t3, {
			toggleSelectAll: toggleSelectAll,
			toggleSelect: toggleSelect,
			downloadXls: downloadXls,
			downloadCsv: downloadCsv
		})
		
	}
	
	function T3ChartController($scope, $location, $log, Downloader, Taxonomy, Chart) {
        var ctrl = this
        var dataPath = '/data' + $scope.taxonomy.data.headline
        Taxonomy.load(dataPath, function(timeseries) {
            ctrl.timeseries = timeseries
            ctrl.chartConfig = Chart.getSparkline()
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
                    if ((getLast(array) - getFirst(array)) < 10) {
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
                    var first = getFirst(getAllValues())
                    var last = getLast(getAllValues())
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
                    
                    ctrl.showYearly = isNotEmpty(data.years)
                    ctrl.showMonthly = isNotEmpty(data.months)
                    ctrl.showQuarterly = isNotEmpty(data.quarters)

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
                        console.log('ctrl.renderChart: ' + ctrl.renderChart)
                    }
                    
                    // Set the chart label to match the period type, e.g. 'years' 
                    ctrl.chartConfig.options.subtitle.text = ctrl.activeChart
                }

                //Format data into high charts compatible format
                function formatData(timeseriesValues) {
                    var data = {
                        values: [],
                        years: []
                    }
                    
                    if (timeseriesValues.length > 10) {
                    	timeseriesValues = timeseriesValues.slice(Math.max(timeseriesValues.length - 10, 1))
                    }
                    
                    var current
                    var i
                    for (i = 0; i < timeseriesValues.length; i++) {
                        current = timeseriesValues[i]
                        data.values.push(enrichData(current, i))
                        data.years.push(current.year)
                    }
                    toUnique(data.years)
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

                function toUnique(a) { //array,placeholder,placeholder
                    var b = a.length;
                    var c
                    while (c = --b) {
                        while (c--) {
                            a[b] !== a[c] || a.splice(c, 1);
                        }
                    }
                }


            }


            function getFirst(array) {
                if (isNotEmpty(array)) {
                    return array[0]
                }
            }


            function getLast(array) {
                if (isNotEmpty(array)) {
                    return array[array.length - 1]
                }
            }

            function isNotEmpty(array) {
                return (array && array.length > 0)
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

        })

	}
	

	function T3RepeatItemController($scope, $location, $log, Downloader, Taxonomy, Chart) {
		var scopedTimeseries = $scope.taxonomy.data.items[$scope.$index]
		console.log('scopedTimeseries: ' + scopedTimeseries)
        var ctrl = this
        var dataPath = '/data' + $scope.taxonomy.data.items[$scope.$index]
        console.log('T3RepeateItemController loading: ' + dataPath + ' for uri: ' + JSON.stringify($scope.taxonomy.data.items[$scope.$index]))
        var promise = Taxonomy.loadWithoutCallback(dataPath)
        console.log('T3RepeateItemController received: ' + JSON.stringify(promise))
        promise.then(function(payload) {
        	console.log('T3RepeateItemController promise for: ' + dataPath + ' loading payload of: ' + JSON.stringify(payload.data.uri))
            ctrl.timeseries = payload.data
            ctrl.chartConfig = Chart.getSparkline()
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
                    if ((getLast(array) - getFirst(array)) < 10) {
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
                    var first = getFirst(getAllValues())
                    var last = getLast(getAllValues())
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
                    
                    ctrl.showYearly = isNotEmpty(data.years)
                    ctrl.showMonthly = isNotEmpty(data.months)
                    ctrl.showQuarterly = isNotEmpty(data.quarters)

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
                        console.log('ctrl.renderChart: ' + ctrl.renderChart)
                    }

                }

                //Format data into high charts compatible format
                function formatData(timeseriesValues) {
                    var data = {
                        values: [],
                        years: []
                    }
                    
                    if (timeseriesValues.length > 10) {
                    	timeseriesValues = timeseriesValues.slice(Math.max(timeseriesValues.length - 10, 1))
                    }
                    
                    var current
                    var i

                    for (i = 0; i < timeseriesValues.length; i++) {
                        current = timeseriesValues[i]
                        data.values.push(enrichData(current, i))
                        data.years.push(current.year)
                    }
                    toUnique(data.years)
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

                function toUnique(a) { //array,placeholder,placeholder
                    var b = a.length;
                    var c
                    while (c = --b) {
                        while (c--) {
                            a[b] !== a[c] || a.splice(c, 1);
                        }
                    }
                }


            }


            function getFirst(array) {
                if (isNotEmpty(array)) {
                    return array[0]
                }
            }


            function getLast(array) {
                if (isNotEmpty(array)) {
                    return array[array.length - 1]
                }
            }

            function isNotEmpty(array) {
                return (array && array.length > 0)
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

        })

	}
	
	function SelectAllDirective() {
		return {
			restrict: 'A',
			link: function(scope, element, attr) {
				element.bind('click', function() {
					scope.$apply(function() {
						scope.t3.toggleSelectAll()
					})
				})

			}
		}
	}

	function SelectDirective() {
		return {
			restrict: 'A',
			link: function(scope, element, attr) {
				element.bind('click', function() {
					scope.$apply(function() {
						scope.t3.toggleSelect(scope.item)
					})
				})

			}
		}
	}


})()