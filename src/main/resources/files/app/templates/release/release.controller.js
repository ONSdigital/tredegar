angular.module('onsTemplates')
	.controller('ReleaseCtrl', ['$scope', '$http',
		function($scope, $http) {
			getData("/release.json", function(data) {
				$scope.releaseData = data
			})

			function getData(path, callback) {
				console.log("Loading data at " + path)
				$http.get(path).success(callback)
			}
		}

	])