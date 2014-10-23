angular.module('onsTemplates')
  .controller('BulletinCtrl', ['$scope',
    function($scope) {
      $scope.header = "Statistical Bulletin"
      $scope.contentType = "bulletin"
      $scope.sidebar = true
      $scope.sidebarUrl = "app/templates/content/contentsidebar.html"

      $scope.data = {
        breadcrumb: [{
          name: "Economy",
          fileName: "economy"
        }, {
          name: "Inflation and Price Indices",
          fileName: "inflationandpriceindices"
        }],
        name: "Statistical Bulletin",
        fileName: "bulletin"
      }

    }
  ])