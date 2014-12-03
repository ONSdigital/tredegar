(function() {
    'use strict';
    //Aplpha Page Controller
    angular.module('onsTemplates')
        .controller('AccessibilityCtrl', ['$scope', AccessibilityCtrl]);

    function AccessibilityCtrl($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Accesibility"
        };
    }
})();
