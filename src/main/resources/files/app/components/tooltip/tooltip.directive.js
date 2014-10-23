 'use strict';
 angular.module('onsComponents')
   .directive('tooltip',
     function() {
       return {
         restrict: 'A',
         transclude: true,
         scope: {
          tooltipClass: '@'
         },
         link: function(scope, elem, attrs) {
           scope.tip = attrs.tooltip
         },
         templateUrl: 'app/components/tooltip/tooltip.html'
       }
     }
 )