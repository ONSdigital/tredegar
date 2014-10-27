/*

Accordion component

@author Brn
 */

(function() {

  'use strict';


  //Based on example on https://docs.angularjs.org/guide/directive
  angular.module('onsAccordion', [])
    .directive('onsAccordion', accordionDirective).directive('onsAccordionItem', accordionitemDirective)

  function accordionDirective() {
    return {
      restrict: 'E',
      transclude: true,
      scope: {
        multiple: '@'
      },
      controller: accordionController,
      controllerAs: 'accordion',
      template: '<div ng-transclude></div>'
    }
  }

  function accordionController($scope) {
    var accordion = this
    init()

    function init() {
      accordion.multiple = $scope.multiple === 'true' ? true : false
      accordion.panes = $scope.panes = []
    }

    function addPane(pane) {
      if (accordion.panes.length === 0) {
        this.toggle(pane)
      }
      accordion.panes.push(pane)
    }

    function toggle(pane) {
      // Hide all other  panes if multiple not wanted to be seen
      if (!accordion.multiple) {
        angular.forEach(panes, function(pane) {
          pane.selected = false
        })
      }
      pane.selected = !pane.selected
    }

    //Expose functions
    angular.extend(accordion, {
      toggle: toggle,
      addPane: addPane

    })
  }

  function accordionitemDirective() {
    return {
      require: '^onsAccordion',
      restrict: 'E',
      transclude: true,
      scope: {
        header: '@'
      },
      link: accordionItemLink,
      templateUrl: 'app/components/accordion/accordionitem.html'
    }
  }

  function accordionItemLink(scope, element, attrs, accordionCtrl) {
    var pane = this
    init()

    function init() {
      accordionCtrl.addPane(pane)
      scope.pane = pane
    }

    function toggle() {
      accordionCtrl.toggle(pane)
    }

  }


})()