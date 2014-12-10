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
				
				$scope.getParentOf = function(index) {
				  var path = $location.$$path
				  var uri = ''
					for (var i = 0; i < index; i++) {
						uri += '/' + $scope.data.parent[i].fileName
					}
					return uri
					console.log(index + uri)
				  
				}

			},
			templateUrl: 'app/components/breadcrumb/breadcrumb.html'
		}
	}


})();