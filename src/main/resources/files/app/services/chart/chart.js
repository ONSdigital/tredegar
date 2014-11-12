(function() {
	'use strict';

	angular.module('onsChart', [])
		.service('Chart', ['$http', '$log', '$location',
			ChartService
		])

	function ChartService($http, $log, $location) {
		var service = this

		function getSparkline() {
	        var data = {
	            options: {
	                chart: {
	                    backgroundColor: null,
	                    borderWidth: 0,
	                    type: 'area',
	                    margin: [0, 0, 0, 0],
	                    width: 140,
	                    height: 100,
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
	                    labels: {
	                        enabled: false
	                    },
	                    title: {
	                        text: null
	                    },
	                    startOnTick: false,
	                    endOnTick: false,
	                    tickPositions: []
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
	                    formatter: function() {
	                        var id = '<div id="custom-tooltip" class="tooltip-left tooltip-right">';
	                        var block = id + "<div class='sidebar' >";
	                        var title = '<b class="title">' + this.points[0].key + ': </b>';
	                        var content = block + "<div class='title'>&nbsp;</div>";

	                        content += "</div>";
	                        content += "<div class='mainText'>";
	                        content += title;

	                        // series names and values
	                        $.each(this.points, function(i, val) {
	                        	content += '<div class="tiptext">' + Highcharts.numberFormat(val.y, 2) + '</div>';
//	                            content += '<div class="tiptext"><i>' + val.point.series.chart.series[i].name + "</i><br/><b>Value: " + Highcharts.numberFormat(val.y, 2) + '</b></div>';
	                        });
	                        content += "</div>";
	                        return content;
	                    },
	                    backgroundColor: null,
	                    borderWidth: 0,
	                    shadow: false,
	                    useHTML: true,
	                    hideDelay: 0,
	                    shared: true,
	                    padding: 0,
	                    positioner: function (w, h, point) {
	                        return { x: point.plotX - w / 2, y: point.plotY - h};
	                    }
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
	                        fillOpacity: 0.25
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

		//Expose public api
		angular.extend(service, {
			getSparkline: getSparkline
		})

	}

})()
