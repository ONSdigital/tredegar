(function() {
    'use strict';
    //Survey Controller
    angular.module('onsTemplates')
        .controller('SurveyCtrl', ['$scope', SurveyCtlr]);

    function SurveyCtlr($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Survey"
        };
    }
})();
