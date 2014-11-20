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
       template: '<span ng-if="tip" tabindex="0" ng-class="tipClass" class="tooltips icon-info-circled icon--warm icon--inline tooltipstered"><span ng-bind-html="tip" class=""></span></span>'
     }
   }
 })()