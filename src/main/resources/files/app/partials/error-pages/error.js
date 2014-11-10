var onsTemplates = angular.module('onsTemplates')

onsTemplates
  .directive('onsErrors',
    function() {
      return {
        restrict: 'E',
        templateUrl: 'app/partials/error-pages/error404.html'
      }
    }
)