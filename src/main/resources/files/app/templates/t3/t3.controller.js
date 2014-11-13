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
            ctrl.chartConfig = Chart.getSparkline(true)
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
                    
                    ctrl.showYearly = Chart.isNotEmpty(data.years)
                    ctrl.showMonthly = Chart.isNotEmpty(data.months)
                    ctrl.showQuarterly = Chart.isNotEmpty(data.quarters)

                    if (ctrl.showMonthly) {
                        ctrl.activeChart = 'months'
                        data.months = Chart.formatData(data.months, true)
                    }

                    if (ctrl.showQuarterly) {
                        ctrl.activeChart = 'quarters'
                        data.quarters = Chart.formatData(data.quarters, true)

                    }

                    if (ctrl.showYearly) {
                        ctrl.activeChart = 'years'
                        data.years = Chart.formatData(data.years, true)
                    }

                    if ((ctrl.showMonthly || ctrl.showYearly || ctrl.showQuarterly)) {
                        ctrl.renderChart = true
                        console.log('ctrl.renderChart: ' + ctrl.renderChart)
                    }
                    
//                    var firstPoint = Chart.getFirst(getAllValues())
//                    var lastPoint = Chart.getLast(getAllValues())
//                    ctrl.chartConfig.options.xAxis.title.text = firstPoint.value + ' to ' + lastPoint.value
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
                    
                    ctrl.showYearly = Chart.isNotEmpty(data.years)
                    ctrl.showMonthly = Chart.isNotEmpty(data.months)
                    ctrl.showQuarterly = Chart.isNotEmpty(data.quarters)

                    if (ctrl.showMonthly) {
                        ctrl.activeChart = 'months'
                        data.months = Chart.formatData(data.months, true)
                    }

                    if (ctrl.showQuarterly) {
                        ctrl.activeChart = 'quarters'
                        data.quarters = Chart.formatData(data.quarters, true)

                    }

                    if (ctrl.showYearly) {
                        ctrl.activeChart = 'years'
                        data.years = Chart.formatData(data.years, true)
                    }

                    if ((ctrl.showMonthly || ctrl.showYearly || ctrl.showQuarterly)) {
                        ctrl.renderChart = true
                        console.log('ctrl.renderChart: ' + ctrl.renderChart)
                    }

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