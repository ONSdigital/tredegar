(function() {

  angular.module('onsTemplates')
    .controller('BulletinCtrl', ['$scope', BulletinController])

  function BulletinController($scope) {

    $scope.header = "Statistical bulletin"
    $scope.contentType = "bulletin"
    $scope.sidebar = true
    $scope.sidebarUrl = "app/templates/content/contentsidebar.html"

  }
})();