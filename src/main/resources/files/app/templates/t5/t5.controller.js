angular.module('onsTemplates')


.controller('T5Ctrl', ['$scope',
    function($scope) {
        $scope.header = "Time Series";
        // $scope.contentType = "timeseries";
        $scope.sidebar = true;
        $scope.sidebarUrl = "app/templates/t5/t5sidebar.html";
    }
])

.controller('chartController', ['$scope', '$location', '$http',
    function($scope, $location, $http) {
        function getTable(path, callback) {
            // console.log("Loading data at " + path);
            $http.get(path).success(callback);
        }
        var categoriesY = [];
        var seriesDataY = [];
        var categoriesQ = [];
        var seriesDataQ = [];
        var categoriesM = [];
        var seriesDataM = [];
        var reY = new RegExp('^[0-9]{4}$');
        var reQ = new RegExp('^[0-9]{4}.[Q1-4]{2}$');
        var reM = new RegExp('^[0-9]{4}.[A-Z]{3}$');

        getTable("/t5data3.json", function(data) {
            $scope.chart = data;

            // function makeArray (dat) {
            //     for (var i = 0; i < dat.length; i++) {
            //         if (reY.test(dat[i].date)){
            //             categoriesY.push(dat[i].date);
            //             seriesDataY.push(Number(dat[i].value));
            //         }
            //         if (reQ.test(dat[i].date)){
            //             categoriesQ.push(dat[i].date);
            //             seriesDataQ.push(Number(dat[i].value));
            //         }
            //         if (reM.test(dat[i].date)){
            //             categoriesM.push(dat[i].date);
            //             seriesDataM.push(Number(dat[i].value));
            //         }
            //         // console.log(dat);

            //     }
            // }
            // makeArray ($scope.chart.data);
        // });


        $scope.hasData = makeArray ($scope.chart.data);


        $scope.chartData = getData();

        $scope.changeChartType = function(type) {
            $scope.chartData.options.chart.type = type;
            // console.log($scope.chartData)
        };

        $scope.changeChartTime = function(time) {
            if (time === 'year') {
                $scope.chartData.options.xAxis.categories = categoriesY;
                $scope.chartData.options.xAxis.tickInterval = 1;
                $scope.chartData.series[0].data = seriesDataY;
            }
            if (time === 'quarter') {
                $scope.chartData.options.xAxis.categories = categoriesQ;
                $scope.chartData.options.xAxis.tickInterval = 4;
                $scope.chartData.series[0].data = seriesDataQ;
            }
            if (time === 'month') {
                $scope.chartData.options.xAxis.categories = categoriesM;
                $scope.chartData.options.xAxis.tickInterval = 12;
                $scope.chartData.series[0].data = seriesDataM;
            }
        };
        console.log($scope.chart);

        function makeArray (dat) {
            for (var i = 0; i < dat.length; i++) {
                if (reY.test(dat[i].date)){
                    categoriesY.push(dat[i].date);
                    seriesDataY.push(Number(dat[i].value));
                }
                if (reQ.test(dat[i].date)){
                    categoriesQ.push(dat[i].date);
                    seriesDataQ.push(Number(dat[i].value));
                }
                if (reM.test(dat[i].date)){
                    categoriesM.push(dat[i].date);
                    seriesDataM.push(Number(dat[i].value));
                }
                // console.log(dat);

            }
        }

        function getData() {
            var data = {
                options: {
                    chart: {
                        type: 'line',
                    },
                    colors: ['#007dc3', '#409ed2', '#7fbee1', '#007dc3', '#409ed2', '#7fbee1'],

                title: {
                    // text: chartJson.mainMeasure // 'Prices Indices'
                },
                subtitle: {
                    text: ''
                },
                xAxis: {
                    categories: categoriesY,
                    // categories: ['Feb 2013', 'Mar 2013', 'Apr 2013', 'May 2013', 'Jun 2103', 'Jul 2013', 'Aug 2013', 'Sept 2013', 'Oct 2013', 'Nov 2013', 'Dec 2013', 'Jan 2014', 'Feb 2014'],
                    tickInterval: 12,
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
                        text: 'Percentage change'
                    }
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
                        var title = '<b class="title">' + this.x + ': </b><br/>';
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
                            content += '<div class="tiptext"><b>' + val.point.series.chart.series[i].name + "= </b>" + Highcharts.numberFormat(val.y, 2) + '%</div>';
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



                /*

                    Solid
                    ShortDash
                    ShortDot
                    ShortDashDot
                    ShortDashDotDot
                    Dot
                    Dash
                    LongDash
                    DashDot
                    LongDashDot
                    LongDashDotDot

                */

                series: [{
                        name: 'CPI % change',
                        data: seriesDataY,
                        // data: [1.7, 1.9, 2, 2.1, 2.2, 2.7, 2.7, 2.8, 2.9, 2.7, 2.4, 2.8, 2.8],
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

                    } //Erase this line and uncomment below to get multiseries
                    // }, {
                    //     name: ' CPIH % change',
                    //     data: [1.6, 1.8, 1.9, 1.9, 2, 2.5, 2.5, 2.5, 2.7, 2.5, 2.2, 2.6, 2.6],
                    //     marker: {
                    //         symbol: "square",
                    //         states: {
                    //             hover: {
                    //                 fillColor: '#409ed2',
                    //                 radiusPlus: 0,
                    //                 lineWidthPlus: 0
                    //             }
                    //         }
                    //     },
                    //     dashStyle: 'longdash'
                    // }, {
                    //     name: 'RPIJ % change',
                    //     data: [2, 2.1, 2, 2, 1.9, 2.5, 2.6, 2.6, 2.7, 2.5, 2.3, 2.7, 2.6],
                    //     marker: {
                    //         symbol: "diamond",
                    //         states: {
                    //             hover: {
                    //                 fillColor: '#7fbee1',
                    //                 radiusPlus: 0,
                    //                 lineWidthPlus: 0
                    //             }
                    //         }
                    //     },
                    //     dashStyle: 'shortdot'
                    // }, {
                    //     name: 'RPI % change',
                    //     data: [2.7, 2.8, 2.7, 2.6, 2.6, 3.2, 3.3, 3.1, 3.3, 3.1, 2.9, 3.3, 3.2],
                    //     marker: {
                    //         symbol: "triangle",
                    //         states: {
                    //             hover: {
                    //                 fillColor: '#007dc3',
                    //                 radiusPlus: 0,
                    //                 lineWidthPlus: 0
                    //             }
                    //         }
                    //     },
                    //     dashStyle: 'Dot'
                    // }

                    /*
                        ,
                        {
                          name: 'CPI',
                          data: [127.4,126.7,127.5,127,126.9,126.8,126.4,125.8,125.9,126.1,125.9,125.6,125.2],
                          visible:false,
                          marker:{
                            symbol:"circle"
                          }
                        }
                        ,
                        {
                          name: 'CPIH',
                          data: [125.2,124.7,125.3,124.8,124.8,124.7,124.3,123.8,123.8,124,123.8,123.6,123.2],
                          visible:false,
                          marker:{
                            symbol:"square"
                          }
                        }
                        ,
                        {
                          name: 'RPIJ',
                          data: [236.3,235.4,236.2,235.1,234.9,235,234.2,233.2,233.2,233.5,233.2,232.6,231.7],
                          visible:false,
                          marker:{
                            symbol:"diamond"
                          }
                        }
                        ,
                        {
                          name: 'RPI',
                          data: [254.2,252.6,253.4,252.1,251.9,251.9,251,249.7,249.7,250,249.5,248.7,247.6],
                          visible:false,
                          marker:{
                            symbol:"triangle"
                          }
                        }

                    */

                ]
            };
            return data;
        }

    });
}]);
