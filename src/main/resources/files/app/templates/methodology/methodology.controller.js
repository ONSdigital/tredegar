angular.module('onsTemplates')
	.controller('MethodologyCtrl', ['$scope',
		function($scope) {
			$scope.header = "Methodology"
			$scope.contentType = "methodology"
			$scope.sidebar = false
		}
	])