'use strict';


var onsTemplates = angular.module('onsTemplates')

onsTemplates
	.controller('DatasetContentCtrl', ['$scope', '$location', '$http',
		function($scope, $location, $http) {
			getData("/" + $scope.getPage() + ".json", function(data) {
				$scope.dataset = data
			})

			function getData(path, callback) {
			     console.log("Loading data at " + path)
			     $http.get(path).success(callback)
			}
		}
	])
//Dataset Controller
onsTemplates.controller('DatasetCtrl', ['$scope',
	function($scope) {
		$scope.header = "Dataset"
		$scope.contentType = "dataset"
		$scope.sidebar = true
		$scope.sidebarUrl = "app/templates/dataset/datasetsidebar.html"
	}
])

//Dataset Timeseries Controller
onsTemplates.controller('Dataset_TimeseriesCtrl', ['$scope',
	function($scope) {
		$scope.header = "Dataset"
		$scope.contentType = "dataset"
		$scope.sidebar = true
		$scope.sidebarUrl = "app/templates/dataset/datasetsidebar_timeseries.html"
	}
])