(function() {

  'use strict';

  angular.module('onsTable', [])
    .controller('TableController', [TableController])
    .directive('onsTable', TableDirective)
    .directive('onsColumn', ColumnDirective)

  function TableController() {

  }

  function TableDirective() {
    return {
      restrict: 'E',
      transclude: true,
      require: '^TableController',
      templateUrl: 'app/components/table/table.html',
      scope: {
        value: '=',
        class: '@'
      }
    }
  }


  function ColumnDirective() {
    return {
      restrict: 'E',
      transclude: true,
      template: '<div ng-transclude></div>',
      link: ColumnLink
    }
  }

  function ColumnLink(scope, element, attrs) {}

})()