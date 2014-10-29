angular.module('onsTemplates')


.controller('T5Ctrl', ['$scope',
    function($scope) {
        $scope.header = "Time Series";
        // $scope.contentType = "timeseries";
        $scope.sidebar = true;
        $scope.sidebarUrl = "/app/templates/t5/t5sidebar.html";
    }
])


.controller('chartController', ['$scope', '$location',
    function($scope, $location) {
        var data = $scope.taxonomy.data;
        var categoriesY = [];
        var seriesDataY = [];
        var categoriesQ = [];
        var seriesDataQ = [];
        var categoriesM = [];
        var seriesDataM = [];
        var reY = new RegExp('^[0-9]{4}$');
        var reQ = new RegExp('^[0-9]{4}.[Q1-4]{2}$');
        var reM = new RegExp('^[0-9]{4}.[A-Z]{3}$');

        $scope.chart = data;
        // console.log($scope.chart);

        makeArray($scope.chart.data);
        $scope.tableValue = makeObj(categoriesY, seriesDataY);


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
                $scope.tableValue = makeObj(categoriesY, seriesDataY);
            }
            if (time === 'quarter') {
                $scope.chartData.options.xAxis.categories = categoriesQ;
                $scope.chartData.options.xAxis.tickInterval = 4;
                $scope.chartData.series[0].data = seriesDataQ;
                $scope.tableValue = makeObj(categoriesQ, seriesDataQ);
            }
            if (time === 'month') {
                $scope.chartData.options.xAxis.categories = categoriesM;
                $scope.chartData.options.xAxis.tickInterval = 12;
                $scope.chartData.series[0].data = seriesDataM;
                $scope.tableValue = makeObj(categoriesM, seriesDataM);
            }
        };

        function makeArray (dat) {
            for (var i = 0; i < dat.length; i++) {
                if (reY.test(dat[i].date)){
                    categoriesY.push(dat[i].date);
                    seriesDataY.push(Number(dat[i].value));
                    $scope.hasYData = true;
                }
                if (reQ.test(dat[i].date)){
                    categoriesQ.push(dat[i].date);
                    seriesDataQ.push(Number(dat[i].value));
                    $scope.hasQData = true;
                }
                if (reM.test(dat[i].date)){
                    categoriesM.push(dat[i].date);
                    seriesDataM.push(Number(dat[i].value));
                    $scope.hasMData = true;
                }
            }
        }

        function makeObj (key, values) {
            var obj = [];
            var x = [];
            for(var i = 0; i<key.length; i++){
                obj[0] = key[i];
                obj[1] = values[i];
                x.push([obj[0], obj[1]]);
            }
            return x;
        }

        function getData() {
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
                        verticalAlign: 'bottom',
                        y: 0,
                        text: 'Image',
                        theme: {
                            // stroke: '#0054aa',
                            fill: '#0054aa',
                            r: 0,
                            states: {
                                hover: {
                                    fill: '#004790'
                                },
                                select: {
                                    // stroke: '#039',
                                    fill: '#004790'
                                }
                            }
                        }
                //     }
                // },
                // exporting: {
                //     buttons: {
                //         contextButton: {
                //             enabled: false
                //         },
                //         exportButton: {
                //             text: 'Image',
                //         }
                    }
                },
                xAxis: {
                    categories: categoriesY,
                    tickInterval: 1,
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
                        text: $scope.chart.units
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
                            content += '<div class="tiptext"><b>' + val.point.series.chart.series[i].name + "= </b>" + Highcharts.numberFormat(val.y, 2) + '</div>';
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
                        name: $scope.chart.name,
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

    }]);


//     });
// }]);
