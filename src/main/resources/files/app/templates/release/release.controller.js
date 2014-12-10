(function() {

	angular.module('onsTemplates')
		.controller('ReleaseCtrl', ['$scope', '$log', 'Taxonomy', '$location',
			function($scope, $log, Taxonomy, $location) {
				$scope.releaseData = $scope.taxonomy.data;
				console.log($scope.releaseData)
			}
		])


})();