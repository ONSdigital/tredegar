(function() {
    angular.module('onsTemplates')
        .controller('ArticleCtrl', ['$scope', ArticleController])

    function ArticleController($scope) {
        $scope.header = "Expert Analysis"
        $scope.contentType = "article"
        $scope.sidebar = true
        $scope.sidebarUrl = "app/templates/content/contentsidebar.html"
    }
})();
