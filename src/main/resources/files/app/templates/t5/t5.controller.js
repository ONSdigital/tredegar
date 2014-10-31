angular.module('onsTemplates')
.controller('T5Ctrl', ['$scope', 'Downloader',
    function($scope, Downloader) {
        
        var t5 = this
        $scope.header = "Time Series";
        $scope.sidebar = true;
        $scope.sidebarUrl = "/app/templates/t5/t5sidebar.html";


        function downloadXls() {
            download('xlsx')
        }

        function downloadCsv() {
            download('csv')
        }

        function download(type) {
            var downloadRequest = {
                type: type
            }
            downloadRequest.uriList = [$scope.getPath()]
            var fileName = $scope.getPage() + '.' + downloadRequest.type;
            Downloader.downloadFile(downloadRequest,fileName)
            
        }

        angular.extend(t5, {
            downloadXls:downloadXls,
            downloadCsv:downloadCsv
        })

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
        $scope.tableValue = makeTableObj(categoriesY, seriesDataY, categoriesYnum);

        $scope.graphValue = makeGraphValue(categoriesY, seriesDataY);

        function makeGraphValue(x, y) {
            return [x , y];
        }

        console.log($scope.graphValue[0][0]);

        //Takes data from json file and transforms it in an array to be read by Highcharts
        function makeArray(jsonData) {
            // Will make the buttons for y,q,m appear or not
            $scope.hasYData = false;
            $scope.hasQData = false;
            $scope.hasMData = false;
            for (var i = 0; i < jsonData.length; i++) {
                if (reY.test(jsonData[i].date)) {
                    categoriesY.push(jsonData[i].date);
                    categoriesYnum.push(Number(jsonData[i].date));
                    seriesDataY.push(Number(jsonData[i].value));
                    $scope.hasYData = true;
                }
                if (reQ.test(jsonData[i].date)) {
                    categoriesQ.push(jsonData[i].date);
                    categoriesQnum.push(QtoNum(jsonData[i].date));
                    seriesDataQ.push(Number(jsonData[i].value));
                    $scope.hasQData = true;
                }
                if (reM.test(jsonData[i].date)) {
                    categoriesM.push(jsonData[i].date);
                    categoriesMnum.push(MtoNum(jsonData[i].date));
                    seriesDataM.push(Number(jsonData[i].value));
                    $scope.hasMData = true;
                }
            }
        }

        /* ****************************************
        WARNING!!!!
        THIS WILL FAIL IF THE DATA FORMAT CHANGES
        ******************************************* */
        // helper to sort table by quarter
        function QtoNum(quarter) {
            return Number(quarter.replace(' Q', '.'));
        }


        /* ****************************************
        WARNING!!!!
        THIS WILL FAIL IF THE DATA FORMAT CHANGES
        ******************************************* */
        // helper to sort table by month
        function MtoNum(months) {
            var month = months.match(/ [A-Z]{3}/i);
            if (month[0].toUpperCase() === ' JAN') {
                return Number(months.replace(month[0], '.01'));
            }
            if (month[0].toUpperCase() === ' FEB') {
                return Number(months.replace(month[0], '.02'));
            }
            if (month[0].toUpperCase() === ' MAR') {
                return Number(months.replace(month[0], '.03'));
            }
            if (month[0].toUpperCase() === ' APR') {
                return Number(months.replace(month[0], '.04'));
            }
            if (month[0].toUpperCase() === ' MAY') {
                return Number(months.replace(month[0], '.05'));
            }
            if (month[0].toUpperCase() === ' JUN') {
                return Number(months.replace(month[0], '.06'));
            }
            if (month[0].toUpperCase() === ' JUL') {
                return Number(months.replace(month[0], '.07'));
            }
            if (month[0].toUpperCase() === ' AUG') {
                return Number(months.replace(month[0], '.08'));
            }
            if (month[0].toUpperCase() === ' SEP') {
                return Number(months.replace(month[0], '.09'));
            }
            if (month[0].toUpperCase() === ' OCT') {
                return Number(months.replace(month[0], '.10'));
            }
            if (month[0].toUpperCase() === ' NOV') {
                return Number(months.replace(month[0], '.11'));
            }
            if (month[0].toUpperCase() === ' DEC') {
                return Number(months.replace(month[0], '.12'));
            }
        }

        // Creates an object for angular table control
        function makeTableObj(key, values, number) {
            var obj = [];
            var x = [];
            for (var i = 0; i < key.length; i++) {
                obj[0] = key[i];
                obj[1] = values[i];
                obj[2] = number[i];
                x.push({
                    "date": obj[0],
                    "values": obj[1],
                    "number": obj[2]
                });
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

        //year (0, by Default), quarter (1) or month (2)
        $scope.yqm = 0;
        $scope.changeTime = function(time) {
            if (time === 'year') {
                $scope.tableValue = makeTableObj(categoriesY, seriesDataY, categoriesYnum);
                $scope.graphValue = makeGraphValue(categoriesY, seriesDataY);
                $scope.yqm = 0;
                $scope.chartData.options.xAxis.categories = $scope.graphValue[0];
                $scope.chartData.options.xAxis.tickInterval = tickInterval(categoriesY.length);
                $scope.chartData.series[0].data = $scope.graphValue[1];
            }
            if (time === 'quarter') {
                $scope.tableValue = makeTableObj(categoriesQ, seriesDataQ, categoriesQnum);
                $scope.graphValue = makeGraphValue(categoriesQ, seriesDataQ);
                $scope.yqm = 1;
                $scope.chartData.options.xAxis.categories = $scope.graphValue[0];
                $scope.chartData.options.xAxis.tickInterval = tickInterval(categoriesQ.length);
                $scope.chartData.series[0].data = $scope.graphValue[1];
            }
            if (time === 'month') {
                $scope.tableValue = makeTableObj(categoriesM, seriesDataM, categoriesMnum);
                $scope.graphValue = makeGraphValue(categoriesM, seriesDataM);
                $scope.yqm = 2;
                $scope.chartData.options.xAxis.categories = $scope.graphValue[0];
                $scope.chartData.options.xAxis.tickInterval = tickInterval(categoriesM.length);
                $scope.chartData.series[0].data = $scope.graphValue[1];
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
                        categories: $scope.graphValue[0],
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
                    name: $scope.chart.name,
                    data: $scope.graphValue[1],
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
    }
]);
