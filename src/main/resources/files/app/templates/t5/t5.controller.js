angular.module('onsTemplates')


.controller('T5Ctrl', ['$scope',
    function($scope) {
        $scope.header = "Time Series";
        $scope.sidebar = true;
        $scope.sidebarUrl = "/app/templates/t5/t5sidebar.html";
    }
])


.controller('chartController', ['$scope', '$location',
    function($scope, $location) {
        var data = $scope.taxonomy.data;
        var categoriesY = [];
        var categoriesYnum = [];
        var seriesDataY = [];
        var categoriesQ = [];
        var categoriesQnum = [];
        var seriesDataQ = [];
        var categoriesM = [];
        var categoriesMnum = [];
        var seriesDataM = [];
        var reY = new RegExp('^[0-9]{4}$');
        var reQ = new RegExp('^[0-9]{4}.[Q1-4]{2}$');
        var reM = new RegExp('^[0-9]{4}.[A-Z]{3}$');

        $scope.chart = data;

        makeArray($scope.chart.data);
        // Year by default
        $scope.tableValue = makeObj(categoriesY, seriesDataY, categoriesYnum);

        function makeArray (dat) {
            for (var i = 0; i < dat.length; i++) {
                if (reY.test(dat[i].date)){
                    categoriesY.push(dat[i].date);
                    categoriesYnum.push(Number(dat[i].date));
                    seriesDataY.push(Number(dat[i].value));
                    $scope.hasYData = true;
                }
                if (reQ.test(dat[i].date)){
                    categoriesQ.push(dat[i].date);
                    categoriesQnum.push(QtoNum(dat[i].date));
                    seriesDataQ.push(Number(dat[i].value));
                    $scope.hasQData = true;
                }
                if (reM.test(dat[i].date)){
                    categoriesM.push(dat[i].date);
                    categoriesMnum.push(MtoNum(dat[i].date));
                    seriesDataM.push(Number(dat[i].value));
                    $scope.hasMData = true;
                }
            }
        }

        /* ****************************************
        WARNING!!!!
        THIS WILL FAIL IF THE JSON FORMAT CHANGES
        ******************************************* */
        function QtoNum(quarter) {
            return Number(quarter.replace(' Q', '.'));
        }


        /* ****************************************
        WARNING!!!!
        THIS WILL FAIL IF THE JSON FORMAT CHANGES
        ******************************************* */
        function MtoNum(months) {
            var month = months.match(/ [A-Z]{3}/);
            if (month[0] === ' JAN'){
                return Number(months.replace(month[0], '.01'));
            }
            if (month[0] === ' FEB'){
                return Number(months.replace(month[0], '.02'));
            }
            if (month[0] === ' MAR'){
                return Number(months.replace(month[0], '.03'));
            }
            if (month[0] === ' APR'){
                return Number(months.replace(month[0], '.04'));
            }
            if (month[0] === ' MAY'){
                return Number(months.replace(month[0], '.05'));
            }
            if (month[0] === ' JUN'){
                return Number(months.replace(month[0], '.06'));
            }
            if (month[0] === ' JUL'){
                return Number(months.replace(month[0], '.07'));
            }
            if (month[0] === ' AUG'){
                return Number(months.replace(month[0], '.08'));
            }
            if (month[0] === ' SEP'){
                return Number(months.replace(month[0], '.09'));
            }
            if (month[0] === ' OCT'){
                return Number(months.replace(month[0], '.10'));
            }
            if (month[0] === ' NOV'){
                return Number(months.replace(month[0], '.11'));
            }
            if (month[0] === ' DEC'){
                return Number(months.replace(month[0], '.12'));
            }
        }

        function makeObj (key, values, number) {
            var obj = [];
            var x = [];
            for(var i = 0; i<key.length; i++){
                obj[0] = key[i];
                obj[1] = values[i];
                obj[2] = number[i];
                x.push({"date":obj[0], "values":obj[1], "number":obj[2]});
            }
            return x;
        }

        $scope.chartData = getData();

        //If true shows graph, else table.
        $scope.chartTable = true;

        $scope.changeChartType = function(type) {
            if (type === "line") {
                $scope.chartTable = true;
            }
            if (type === "table") {
                $scope.chartTable = false;
            }
        };

        //Year, Quarter or month
        $scope.yqm = 0;

        $scope.changeChartTime = function(time) {
            if (time === 'year') {
                $scope.chartData.options.xAxis.categories = categoriesY;
                $scope.chartData.options.xAxis.tickInterval = tickInterval(categoriesY.length);
                $scope.chartData.series[0].data = seriesDataY;
                $scope.tableValue = makeObj(categoriesY, seriesDataY, categoriesYnum);
                $scope.yqm = 0;
            }
            if (time === 'quarter') {
                $scope.chartData.options.xAxis.categories = categoriesQ;
                $scope.chartData.options.xAxis.tickInterval = tickInterval(categoriesQ.length);
                $scope.chartData.series[0].data = seriesDataQ;
                $scope.tableValue = makeObj(categoriesQ, seriesDataQ, categoriesQnum);
                $scope.yqm = 1;
            }
            if (time === 'month') {
                $scope.chartData.options.xAxis.categories = categoriesM;
                $scope.chartData.options.xAxis.tickInterval = tickInterval(categoriesM.length);
                $scope.chartData.series[0].data = seriesDataM;
                $scope.tableValue = makeObj(categoriesM, seriesDataM, categoriesMnum);
                $scope.yqm = 2;
            }
        };

        //Defines the intervals in xAxis according to data
        function tickInterval(categories) {
            var tick;
            if (categories <= 20) {
                tick = 1;
            }
            if (categories > 20 && categories <= 80) {
                tick = 4;
            }
            if (categories > 80) {
                tick = 12;
            }
            if (categories > 240) {
                tick = 48;
            }
            if (categories > 480) {
                tick = 96;
            }
            if (categories > 960) {
                tick = 192;
            }
            return tick;
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
                            fill: '#0054aa',
                            r: 0,
                            states: {
                                hover: {
                                    fill: '#004790'
                                },
                                select: {
                                    fill: '#004790'
                                }
                            }
                        }
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
                        $.each(this.points, function(i, val){
                            content += '<div class="tiptext"><i>' + val.point.series.chart.series[i].name + "</i><br/><b>Value: " + Highcharts.numberFormat(val.y, 2) +'</b></div>' ;
                        });
                        content+= "</div>";
                        return content;
                    },



                    //     // series names and values
                    //     $.each(this.points, function(i, val) {
                    //         content += '<div class="tiptext"><b>' + val.point.series.chart.series[i].name + "= </b>" + Highcharts.numberFormat(val.y, 2) + '</div>';
                    //     });
                    //     content += "</div>";
                    //     return content;
                    // },

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
                    }
                ]
            };
            return data;
        }
    }
]);


//     });
// }]);
