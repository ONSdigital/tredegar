'use strict';

(function() {

	angular.module('onsChart', [])
		.service('Chart', ['$http', '$log', '$location',
			ChartService
		])

	function ChartService($http, $log, $location) {
		var service = this
		
	    function getChart() {
	        var data = {
	            options: {
	                chart: {
	                    type: 'line',
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
	                        text: ""
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
		

		function getSparkline(isHeadlineData) {
			var widthValue
			var heightValue
			if (isHeadlineData) {
				widthValue = 140
				heightValue = 100
			} else {
				widthValue = 50
				heightValue = 50
			}
			
	        var data = {
	            options: {
	                chart: {
	                    backgroundColor: null,
	                    borderWidth: 0,
	                    type: 'area',
	                    margin: [0, 0, 0, 0],
	                    width: widthValue,
	                    height: heightValue,
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
	                	tickLength: 0
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
	                exporting: {enabled: false}
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
		
        function isNotEmpty(array) {
            return (array && array.length > 0)
        }
        
        function getLast(array) {
            if (isNotEmpty(array)) {
                return array[array.length - 1]
            }
        }
        
        function getFirst(array) {
            if (isNotEmpty(array)) {
                return array[0]
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
        
        function monthVal(mon) {
            switch (mon.slice(0, 3).toUpperCase()) {
                case 'JAN':
                    return 1
                case 'FEB':
                    return 2
                case 'MAR':
                    return 3
                case 'APR':
                    return 4
                case 'MAY':
                    return 5
                case 'JUN':
                    return 6
                case 'JUL':
                    return 7
                case 'AUG':
                    return 8
                case 'SEP':
                    return 9
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
        
        function toUnique(a) { //array,placeholder,placeholder
            var b = a.length;
            var c
            while (c = --b) {
                while (c--) {
                    a[b] !== a[c] || a.splice(c, 1);
                }
            }
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
            toUnique(data.years)
            return data
        }        

		//Expose public api
		angular.extend(service, {
			getChart: getChart,
			getSparkline: getSparkline,
			isNotEmpty: isNotEmpty,
			getFirst: getFirst,
			getLast: getLast,
			quarterVal: quarterVal,
			monthVal: monthVal,
			toUnique: toUnique,
			enrichData: enrichData,
			formatData: formatData
		})

	}

})()
