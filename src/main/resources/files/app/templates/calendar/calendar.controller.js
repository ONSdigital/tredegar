(function() {
    'use strict';
    //Local Statistics Controller
    angular.module('onsTemplates')
        .controller('LoaderCtrl', ['$scope', '$log', 'DataLoader', LoaderCtrl])
        .controller('CalendarCtlr', ['$scope', '$log', CalendarCtlr]);

    function LoaderCtrl($scope, $log, DataLoader) {
        DataLoader.load("/data/releases")
            .then(function(data) {
                $log.debug('Loaded data')
                $scope.calendarData = data;
            });
    }

    function CalendarCtlr($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Release calendar"
        };
    }
})();
