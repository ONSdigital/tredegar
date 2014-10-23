angular.module('onsTemplates')
	.controller('ContentCtrl', ['$scope', '$location', '$http',
		function($scope, $location, $http) {
			
			getData("/" + $scope.getPage() + ".json", function(data) {
				$scope.content = data
			})

			$scope.scroll = function() {
				anchorSmoothScroll.scrollTo($location.hash())
			}

			function getData(path, callback) {
			     console.log("Loading data at " + path)
			     $http.get(path).success(callback)
			}

		}
	])
	.directive('onsContent', function() {
		return {
			restrict: 'E',
			templateUrl: 'app/templates/content/content.html'
		}
	})