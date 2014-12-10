/*
Toggler

@author Brn
 */
(function() {

'use strict';

  angular.module('onsToggler', [])
    .service('toggleService', ToggleService)
    .directive('toggler', ['toggleService', Toggler])
    .directive('toggleable', ['toggleService', Toggleable])

  function ToggleService() {
    var service = this
    var toggleables = {};

    function registerToggleable(toggleable) {
      toggleables[toggleable.key] = toggleable;
    }

    function toggle(key) {
      toggleables[key].toggle();
    }

    function isVisible(key) {
      var pane = toggleables[key];
      return pane ? pane.visible : false
    }

    angular.extend(service, {
      registerToggleable: registerToggleable,
      toggle: toggle,
      isVisible: isVisible
    })
  }

  function Toggler(toggleService) {
    return {
      restrict: 'A',
      scope: {
        togglerWidgetVar: '@'
      },
      link: TogglerLink
    }

    function TogglerLink(scope, elem, attrs) {
      var toggler = scope
      toggler.key = attrs.toggler
      bindClick()
      if (scope.togglerWidgetVar) {
        scope.$parent[scope.togglerWidgetVar] = toggler
      }

      function bindClick() {
        elem.bind("click", function() {
          toggleService.toggle(attrs.toggler)
          scope.$apply() // Trigger Angular cycle
        })
      }

      function isVisible() {
        return toggleService.isVisible(toggler.key)
      }

      angular.extend(toggler, {
        isVisible:isVisible
      })
    }

  }

  function Toggleable(toggleService) {
    return {
      restrict: 'A',
      transclude: true,
      scope:{},
      link: function(scope, elem, attrs) {
        scope.toggle = function() {
          scope.visible = !scope.visible;
        }

        scope.key = attrs.toggleable
        toggleService.registerToggleable(scope);
      },
      template: '<div ng-transclude ng-show="visible"></div>'
    }
  }


})();