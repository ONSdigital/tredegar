(function() {

    'use strict';

    //Contact Us Controller
    angular.module('onsTemplates')
        .controller('PreviousCtrl', ['$scope', ContactUsController])

    function ContactUsController($scope) {
        $scope.breadcrumb = {
            parent: [],
            current: "Previous Sample Page"
        }
    }

})();