'use strict';

/*

Tabs component

@author Brn
 */


//Based on example on https://docs.angularjs.org/guide/directive
angular.module('onsComponents')
  .directive('onsTabs', function() {
    return {
      restrict: 'E',
      transclude: true,
      scope: {},
      controller: function($scope) {
        var panes = $scope.panes = []

        $scope.select = function(pane) {
          angular.forEach(panes, function(pane) {
            pane.selected = false;
          });
          pane.selected = true;
        }

        this.addPane = function(pane) {
          if (panes.length === 0) {
            $scope.select(pane)
          }
          panes.push(pane)
        };

      },
      templateUrl: 'app/components/tabs/tabs.html'
    }
  }).directive('onsTab', function() {
    return {
      require: '^onsTabs',
      restrict: 'E',
      transclude: true,
      scope: {
        title: '@'
      },
      link: function(scope, element, attrs, contentCtrl) {
        contentCtrl.addPane(scope)
      },
      templateUrl: 'app/components/tabs/pane.html'
    }
  })