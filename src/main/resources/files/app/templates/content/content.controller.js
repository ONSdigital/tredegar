angular.module('onsTemplates')
	.controller('ContentCtrl', ['$scope', '$location', '$log', 'DataLoader',
		function($scope, $location, $log, DataLoader) {
			$scope.content = $scope.taxonomy.data
			$scope.scroll = function() {
				anchorSmoothScroll.scrollTo($location.hash())
			}

			function getData(path, callback) {
				$log.debug("Loading data at " + path)
				DataLoader.load(path, callback)
			}

		}
	])
	.directive('onsContent', function() {
		return {
			restrict: 'E',
			templateUrl: 'app/templates/content/content.html'
		}
	})