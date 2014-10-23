angular.module('onsTemplates')
  .controller('T1Controller', ['$scope', 'Page', 'TaxonomyService',
    function T1Controller($scope, Page, TaxonomyService) {
      var ctrl = this
      init()

      function init() {
        Page.setTitle('Home')
        TaxonomyService.loadChildrenData($scope)
        $scope.data.children = TaxonomyService.convert($scope.data.children)
      }
    }
  ])