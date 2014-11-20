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
       templateUrl: 'app/components/tooltip/tooltip.html'
     }
   }
 })()