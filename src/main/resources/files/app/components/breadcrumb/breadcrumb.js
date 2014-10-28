(function() {

'use strict';

angular.module('onsComponents')
	.directive('onsBreadcrumb', ['$location',
		BreadCrumbDirective
	])


	function BreadCrumbDirective($location) {
		return {
			scope: {
				data: '='
			},
			restrict: 'E',
			controller: function($scope) {
				$scope.getPath = function(){
					return $location.$$path
				}
				
				$scope.getParentOf = function(p) {
				  var path = $location.$$path
				  var lastIndex = path.lastIndexOf(p)
				  var parenPath = path.substring(0, lastIndex -1 )
				  return parenPath
				}
			},
			templateUrl: 'app/components/breadcrumb/breadcrumb.html'
		}
	}


})()