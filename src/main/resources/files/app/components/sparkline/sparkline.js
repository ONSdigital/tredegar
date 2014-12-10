(function() {

'use strict';

	angular.module('onsSparkline', ['highcharts-ng'])
		.directive('onsSparkline', ['$log',
			SparkLine
		])


	function SparkLine($log) {
		return {
			restrict: 'E',
			scope: {
				chartData: '=',
				width: '=',
				height: '=',
				headline: '=?'
			},
			controller: SparkLineController,
			controllerAs: 'sparkline',
			templateUrl: 'app/components/sparkline/sparkline.html'
		}

		function SparkLineController($scope, $element, $window, $log) {
			var sparkline = this
			sparkline.visible = false
			sparkline.chartConfig = getSparkline($scope.width, $scope.height)
			watchData()
			if($scope.headline) {
				resolveScreenSize()				
			}
			watchWidth()
			
			function watchData() {
				$scope.$watch('chartData', function() {
					if ($scope.chartData) {
						buildChart(sparkline.chartConfig, $scope.chartData, $scope.headline)
						sparkline.visible = true
					} else {
						sparkline.visible = false
					}
				})
			}

			function watchWidth() {
				// support for responsive behaviour for t3 headline data sparkline
		        if($scope.headline) {
		          angular.element($window).bind('resize', function() {
		        	$log.debug('Resized:' + $window.innerWidth)
		        	resolveScreenSize()
		              $scope.$apply()
		          })
		        }
			}

			function resolveScreenSize() {
				  // due to there being multiple sparklines on t3 then occassionally the resize lifecycle throws a
		        	  // [Uncaught TypeError: Cannot read property 'chart' of undefined] - this has not resulted in any issues		        	  
		              if ($window.innerWidth < 900) {
		            	  sparkline.chartConfig.options.chart.width = 175
		              } else {
		            	  sparkline.chartConfig.options.chart.width = 350		            	
		              }
		          	  $log.debug('chart width set to: ' + sparkline.chartConfig.options.chart.width)
			}

			function buildChart(chartConfig, timeseries, isHeadline) {
			
				prepareData()

				function prepareData() {
					chartConfig.series[0].data = formatData(timeseries).values
					chartConfig.options.xAxis.tickInterval = tickInterval(timeseries.length);

					function tickInterval(length) {
						if (length <= 20) {
							return 1;chartConfig
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

					//Format data into high charts compatible format
					function formatData(timeseries) {
						var data = {
							values: []
						}

						var current
						var i

						for (i = 0; i < timeseries.length; i++) {
							current = timeseries[i]
							data.values.push(enrichData(current))
						}
						return data
					}

					function enrichData(timeseries) {
						timeseries.y = +timeseries.value //Cast to number
						timeseries.name = timeseries.date //Appears on x axis
						return timeseries
					}
				}
			}
		}
	}



	function getSparkline(width, height) {

		var data = {
			options: {
				chart: {
					backgroundColor: null,
					borderWidth: 0,
					type: 'area',
					margin: [0, 0, 0, 0],
					width: width,
					height: height ,
					style: {
						overflow: 'visible'
					},
					skipClone: true
				},
				title: {
					text: ''
				},
				subtitle: {
					text: '',
					y: 110
				},
				credits: {
					enabled: false
				},
				xAxis: {
					categories: [],
					labels: {
						formatter: function() {
							if (this.isFirst) {
								return this.value
							}
							if (this.isLast) {
								return this.value
							}
						},
						step: 1
					},
					tickLength: 0,
					lineColor: 'transparent'
				},
				yAxis: {
					endOnTick: false,
					startOnTick: false,
					labels: {
						enabled: false
					},
					title: {
						text: null
					},
					tickPositions: [0]
				},
				legend: {
					enabled: false
				},
				tooltip: {
					enabled: false
				},
				plotOptions: {
					series: {
						animation: false,
						lineWidth: 1,
						shadow: false,
						states: {
							hover: {
								lineWidth: 1
							}
						},
						marker: {
							radius: 1,
							states: {
								hover: {
									radius: 2
								}
							}
						},
						fillOpacity: 0.25,
						enableMouseTracking: false,
					},
					column: {
						negativeColor: '#910000',
						borderColor: 'silver'
					}
				},
				exporting: {
					enabled: false
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