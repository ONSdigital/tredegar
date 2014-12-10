(function() {

    'use strict';

    //Contact Us Controller
    angular.module('onsTemplates')
        .controller('MethodologyCtrl', ['$scope', ContactUsController])

    function ContactUsController($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Methodology"
        }
    }

})();