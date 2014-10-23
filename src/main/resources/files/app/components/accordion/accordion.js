'use strict';

/*

Accordion component

@author Brn
 */


//Based on example on https://docs.angularjs.org/guide/directive
angular.module('onsComponents')
  .directive('onsAccordion', function() {
    return {
      restrict: 'E',
      transclude: true,
      scope: {
        multiple: '@'
      },
      controller: function($scope) {
        var multiple = $scope.multiple === 'true' ? true : false
        var panes = $scope.panes = []

        this.toggle = function(pane) {
          // Hide all other  panes if multiple not wanted to be seen
          if (!multiple) {
            angular.forEach(panes, function(pane) {
              pane.selected = false;
            })
          }
          pane.selected = !pane.selected;
        }

        this.addPane = function(pane) {
          if (panes.length === 0) {
            this.toggle(pane)
          }
          panes.push(pane)
        };

      },
      template: '<div ng-transclude></div>'
    }
  }).directive('onsAccordionItem', function() {
    return {
      require: '^onsAccordion',
      restrict: 'E',
      transclude: true,
      scope: {
        header: '@'
      },
      link: function(scope, element, attrs, accordionCtrl) {
        accordionCtrl.addPane(scope)
        scope.pane = scope
        scope.toggle = accordionCtrl.toggle
      },
      templateUrl: 'app/components/accordion/accordion-item.html'
    }
  })