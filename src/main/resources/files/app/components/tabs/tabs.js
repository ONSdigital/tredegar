'use strict';

(function() {

  /*
  Tabs component
  @author Brn
   */

  angular.module('onsTabs', [])
    .directive('onsTabs', TabsDirective)
    .directive('onsTabControl', TabControlDirective)
    .directive('onsTab', TabDirective)

  function TabsDirective() {
    return {
      restrict: 'A',
      transclude: true,
      replace: true,
      scope: {
        keyVar: "@"
      },
      controller: TabsController,
      controllerAs: 'tabs',
      template: '<div ng-transclude></div>'
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
        
        if ($scope.keyVar) {
          $scope.$parent[$scope.keyVar] = selectedPane.key
        }
      }

      function isSelected(key) {
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


  function TabControlDirective() {
    return {
      require: '^onsTabs',
      restrict: 'A',
      transclude: true,
      replace: true,
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
      },
      template: '<div ng-transclude></div>'
    }
  }

  //Represents tab pane within tabs directive
  function TabDirective() {
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

})()