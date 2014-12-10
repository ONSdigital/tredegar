(function() {

    'use strict';

    //Contact Us Controller
    angular.module('onsTemplates')
        .controller('CopyrightCtrl', ['$scope', ContactUsController])

    function ContactUsController($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Copyright"
        }
    }

})();