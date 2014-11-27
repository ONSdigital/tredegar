(function() {
    'use strict';
    //National Statistics Controller
    angular.module('onsTemplates')
        .controller('NationalStatsCtlr', ['$scope', NationalStatsCtlr]);

    function NationalStatsCtlr($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "National Statistics"
        };
    }
})();
