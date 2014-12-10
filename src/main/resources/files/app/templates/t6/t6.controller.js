(function() {

    // Code not working in the actual prototype. It was intended to use Google Charts for deploying the graphs.

    angular.module('onsTemplates')


    .controller('T6Ctrl', ['$scope',
        function($scope) {
            $scope.header = "Time Series";
            $scope.contentType = "timeseries";
            $scope.sidebar = true;
            $scope.sidebarUrl = "app/templates/t6/t6sidebar.html";
        }
    ])

    .controller('GoogleChartCtrl', ['$scope', '$location', '$http',
        function($scope, $location, $http) {
            getTable("/googlechart.json", function(data) {
                $scope.chart = data;
            });

            function getTable(path, callback) {
                $http.get(path).success(callback);
            }
        }
    ]);

})();