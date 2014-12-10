(function() {

'use strict';

  /*
  Tabs component
  @author Brn
   */

  angular.module('onsTabs', [])
    .directive('onsTabs', Tabs)
    .directive('onsTabControl', TabControl)
    .directive('onsTab', Tab)

  function Tabs() {
    return {
      restrict: 'A',
      scope: {
        tabsActiveVar: "@"
      },
      controller: TabsController,
      controllerAs: 'tabs'
    }

    function TabsController($scope) {

      var tabs = this
      var panes = tabs.panes = {} //Tab panes
      var numberOfPanes = this.numberOfPanes = 0


      function select(key) {
        for (var p in panes) { //Hide all panes
          if (panes.hasOwnProperty(p)) {
            panes[p].selected = false;
          }
        }

        var selectedPane = panes[key]
        selectedPane.selected = true; //Show selected pane

        if ($scope.tabsActiveVar) {
          $scope.$parent[$scope.tabsActiveVar] = selectedPane.key
        }
      }

      function isSelected(key) {
        alert (key)
        var pane = panes[key]
        if (pane) {
          return pane.selected
        }
        return false
      }



      function registerTabPane(pane) {
        panes[pane.key] = pane
        if (numberOfPanes === 0) {
          select(pane.key)
        }
        numberOfPanes++
      }

      //Expose functions
      angular.extend(tabs, {
        registerTabPane: registerTabPane,
        select: select,
        isSelected: isSelected
      })
    }
  }


  function TabControl() {
    return {
      require: '^onsTabs',
      restrict: 'A',
      scope: {},
      link: function(scope, element, attrs, tabsController) {
        scope.key = attrs.onsTabControl
        if (!scope.key) {
          throw 'tab control key can not be empty'
        }

        bindClick()

        function bindClick() {
          element.bind("click", function() {
            scope.$apply(function() { // Trigger Angular digest cycle to process changed values
              tabsController.select(scope.key)
            })
          })
        }
      }
    }
  }

  //Represents tab pane within tabs directive
  function Tab() {
    return {
      require: '^onsTabs',
      restrict: 'A',
      scope: {},
      transclude: true,
      replace: true,
      link: function(scope, element, attrs, tabsController) {
        scope.key = attrs.onsTab
        if (!scope.key) {
          throw 'tab key can not be empty"'
        }
        tabsController.registerTabPane(scope)
      },
      template: '<div ng-transclude ng-show="selected"> </div>'
    }
  }

})();
