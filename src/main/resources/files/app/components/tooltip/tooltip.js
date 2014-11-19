 (function() {
   'use strict';

   angular.module('onsTooltip', [])
     .directive('onsInfoTooltip', [InfoTooltip])

   function InfoTooltip() {
     return {
       restrict: 'E',
       scope: {
         tipClass: '@',
         tip:'@'
       },
       link: function(scope, elem, attrs) {
        console.log(scope.tip)
       },
       templateUrl: 'app/components/tooltip/tooltip.html'
     }
   }
 })()