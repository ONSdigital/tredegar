'use strict';

var onsTemplates = angular.module('onsTemplates')

onsTemplates.controller('DatasetContentCtrl', ['$scope', '$location', '$log', 'DataLoader',
		function($scope, $location, $log, DataLoader) {
			DataLoader.load("data" + $scope.getPath()).then(function(data) {
				$scope.dataset = data
			})
		}
	])
	// Dataset Controller
onsTemplates.controller('DatasetCtrl', ['$scope', function($scope) {
	$scope.header = "Dataset"
	$scope.contentType = "dataset"
	$scope.sidebar = true
	$scope.sidebarUrl = "app/templates/dataset/datasetsidebar.html"
}])

// Dataset Timeseries Controller
onsTemplates.controller('Dataset_TimeseriesCtrl', ['$scope', function($scope) {
	$scope.header = "Dataset"
	$scope.contentType = "dataset"
	$scope.sidebar = true
	$scope.sidebarUrl = "app/templates/dataset/datasetsidebar_timeseries.html"
}])