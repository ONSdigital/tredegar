angular.module('onsTemplates')
  .controller('T2Controller', ['$scope', 'Page', 'TaxonomyService',
    function($scope, Page, TaxonomyService) {
      init()

      function init() {
        Page.setTitle('Home')
        TaxonomyService.loadChildrenData($scope)
        $scope.data.highlightedChildren = TaxonomyService.convert($scope.data.children, 3)
      }

    }
  ])