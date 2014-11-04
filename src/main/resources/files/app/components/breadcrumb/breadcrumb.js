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
				  // look for all occurrences ("g") of this char sequence
				  var pathFilter = new RegExp(p, "g")
				  // cater for no match ([])
				  var countP = (path.match(pathFilter) || []).length;

				  var indexPoint
				  if (countP > 1) {
					  // if the char sequence occurs twice then make educated guess that we should go for first occurrence
					  indexPoint = path.indexOf(p)
				  } else {
					  // otherwise go to the last occurrence
					  indexPoint = path.lastIndexOf(p)
				  }

				  var parentPath = path.substring(0, indexPoint -1 )
				  return parentPath
				}
			},
			templateUrl: 'app/components/breadcrumb/breadcrumb.html'
		}
	}


})()