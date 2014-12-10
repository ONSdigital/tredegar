//Progress placeholder. Currently only shows "Loading..." text
(function() {
	'use strict';

	angular.module('onsLoading', [])
		.directive('onsLoading', [Loading])

	function Loading() {
		return {
			restrict: 'E',
			replace: true,
			scope: {
				condition: '=?',
				class: '@'
			},
			link: function(scope) {
				if (typeof scope.condition === 'undefined') {
					scope.condition = true
				}
			},
			template: ' <div style="display:flex" ng-class="class" ng-if="condition" ><img style="margin:auto" width="14" height="14" src="/ui/img/loading.gif" width:"32" height="32" alt="Loading..." class="media__img"></div>'
		}
	}


})();