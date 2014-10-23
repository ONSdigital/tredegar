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

    this.isVisible=function(key) {
      var pane = toggleables[key];
      return pane ? pane.visible : false
    }
  })
  .directive('toggler', ['toggleService',
    function(toggleService) {
      return {
        restrict: 'A',
        scope:{
          expandLabel:'@',
          collapseLabel:'@'
        },
        link: function(scope, elem, attrs) {
          var ctrl = this
          scope.key = attrs.toggler
          bindClick()

          function bindClick() {
            elem.bind("click", function() {
              toggleService.toggle(attrs.toggler)
              scope.$apply() // Trigger Angular cycle
            })
          }

          scope.isVisible=function(){
            return toggleService.isVisible(scope.key)
          }
        },
        template:'{{isVisible() ?  collapseLabel  : expandLabel}}',
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
        templateUrl: 'app/components/toggler/toggleable.html'
      }
    }
  ]);