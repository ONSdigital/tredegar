(function() {
    angular.module('onsTemplates')
        .controller('MethodologyCtrl', ['$scope', MethodologyController])

    function MethodologyController($scope) {
        $scope.header = "Methodology"
        $scope.contentType = "methodology"
        $scope.sidebar = false
    }
})();
