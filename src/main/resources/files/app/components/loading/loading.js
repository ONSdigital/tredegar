//Progress placeholder. Currently only shows "Loading..." text
(function() {
	'use strict';

	angular.module('onsLoading', [])
		.directive('onsLoading', [Loading])

	function Loading() {
		return {
			restrict: 'E',
			replace:true,
			scope: {
				condition: '=?',
				class: '@'
			},
			link: function(scope) {
				if(typeof scope.condition === 'undefined') {
					scope.condition = true
				}
			},
			template: '<span ng-class="class" ng-if="condition">Loading...</span>'
		}
	}


})()