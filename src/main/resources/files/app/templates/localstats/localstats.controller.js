(function() {
    'use strict';
    //Local Statistics Controller
    angular.module('onsTemplates')
        .controller('LocalStatsCtlr', ['$scope', LocalStatsCtlr]);

    function LocalStatsCtlr($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Local statistics"
        };
    }
})();
