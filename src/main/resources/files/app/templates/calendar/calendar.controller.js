(function() {
    'use strict';
    //Local Statistics Controller
    angular.module('onsTemplates')
        .controller('CalendarCtlr', ['$scope', LocalStatsCtlr]);

    function LocalStatsCtlr($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Release calendar"
        };
    }
})();
