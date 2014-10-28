(function() {

  angular.module('onsTemplates')
    .controller('BulletinCtrl', ['$scope', BulletinController])

  function BulletinController($scope) {
    $scope.header = "Statistical Bulletin"
    $scope.contentType = "bulletin"
    $scope.sidebar = true
    $scope.sidebarUrl = "app/templates/content/contentsidebar.html"

    $scope.breadcrumb = {
      parent: [{
        name: "Economy",
        fileName: "economy"
      }, {
        name: "Inflation and Price Indices",
        fileName: "inflationandpriceindices"
      }],
      current: "Statistical Bulletin",
    }

  }
})()