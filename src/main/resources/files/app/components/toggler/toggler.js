'use strict';

/*
Toggler

@author Brn
 */

angular.module('onsComponents')
  .service('toggleService', function() {
    var toggleables = {};

    this.registerToggleable = function(toggleable) {
      toggleables[toggleable.key] = toggleable;
    }

    this.toggle = function(key) {
      toggleables[key].toggle();
    }
  })
  .directive('toggler', ['toggleService',
    function(toggleService) {
      return {
        restrict: 'A',
        link: function(scope, elem, attrs) {
          var ctrl = this
          bindClick()

          function bindClick() {
            elem.bind("click", function() {
              toggleService.toggle(attrs.toggler)
              scope.$apply() // Trigger Angular cycle
            })
          }
        },
        controller: function($scope) {
          this.isVisible=function(key) {
            var toggleable = toggleService.toggleables[key]
            alert(toggleable)
            toggleable ? toggleable.visible : false
          }
        },
        controllerAs: 'ToggleCtrl'
      }
    }
  ])
  .directive('toggleable', ['toggleService',
    function(toggleService) {
      return {
        restrict: 'A',
        transclude: true,
        scope: {
          show: '@'
        },
        link: function(scope, elem, attrs) {
          scope.toggle = function() {
            scope.visible = !scope.visible;
          }

          scope.visible = (scope.show === 'true')
          scope.key = attrs.toggleable
          toggleService.registerToggleable(scope);
        },
        templateUrl: 'app/components/toggler/toggler.html'
      }
    }
  ]);