(function() {
    'use strict';
    //Local Statistics Controller
    angular.module('onsTemplates')
        .controller('LocalStatsController', ['$scope', LocalStatsController]);

    function LocalStatsController($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Local Statistics"
        };
    }
})();
