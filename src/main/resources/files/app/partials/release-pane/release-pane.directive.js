'use strict';

angular.module('onsTemplates')
.directive('releasePane', function() {
  return {
    restrict: 'E',
    templateUrl: 'app/partials/release-pane/release-pane.html'
  }
})