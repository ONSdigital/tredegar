angular.module('onsTemplates')
	.controller('ReleaseCtrl', ['$scope', '$log', 'DataLoader',
		function($scope, $log, DataLoader) {
			DataLoader.load("/release.json")
				.then(function(data) {
					$scope.releaseData = data
				})
		}

	])