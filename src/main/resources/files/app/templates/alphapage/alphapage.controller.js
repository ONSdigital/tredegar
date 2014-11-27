(function() {
    'use strict';
    //Aplpha Page Controller
    angular.module('onsTemplates')
        .controller('AlphaPageCtlr', ['$scope', AlphaPageCtlr]);

    function AlphaPageCtlr($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Alpha website"
        };
    }
})();
