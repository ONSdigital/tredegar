/*

Accordion component

@author Brn
 */

(function() {

  'use strict';


  //Based on example on https://docs.angularjs.org/guide/directive
  angular.module('onsAccordion', [])
    .directive('onsAccordion', AccordionDirective)
    .directive('onsAccordionItem', AccordionitemDirective)

  function AccordionDirective() {
    return {
      restrict: 'E',
      scope: {
        multiple: '=?'
      },
      controller: AccordionController,
      controllerAs: 'accordion'
    }
  }

  function AccordionController($scope) {
    var accordion = this
    init()

    function init() {
      accordion.multiple = $scope.multiple
      accordion.panes =  []
    }

    function addPane(pane) {
      if (accordion.panes.length === 0) {
        if(!pane.closed) {
            //If closed by default dont toggle
            toggle(pane)
        }
      }
      accordion.panes.push(pane)

    }

    function toggle(pane) {
      // Hide all other  panes if multiple not wanted to be seen
      if (!accordion.multiple) {
        angular.forEach(accordion.panes, function(p) {
          p.selected = false
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

  function AccordionitemDirective() {
    return {
      require: '^onsAccordion',
      restrict: 'E',
      transclude: true,
      scope: {
        header: '@',
        closed: '=?'
      },
      link: AccordionItemLink,
      templateUrl: 'app/components/accordion/accordionitem.html'
    }
  }

  function AccordionItemLink(scope, element, attrs, accordionCtrl) {
    var pane = scope
    init()

    function init() {
      accordionCtrl.addPane(pane)
      scope.pane = pane
    }

    function toggle() {
      accordionCtrl.toggle(pane)
    }

    angular.extend(scope, {
      toggle: toggle
    })

  }


})()
