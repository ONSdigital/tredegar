// Taxonomy base, data is injected. Data is loaded throug route service
(function() {
	'use strict';

	angular.module('onsTemplates')
		.controller('TaxonomyController', ['$scope', 'data', TaxonomyController])
		.controller('T1ChartController', ['$scope', 'Chart', T1ChartController])

	function TaxonomyController($scope, data) {
		var taxonomy = this
		taxonomy.data = data
		prepareBreadcrumb($scope, data)

		function prepareBreadcrumb($scope, data) {
			if (data.level === 't1') {
				return
			}
			$scope.breadcrumb = {}
			$scope.breadcrumb.parent = data.breadcrumb
			$scope.breadcrumb.current = data.name
		}
	}
	
	function T1ChartController($scope, Chart) {
		var scopedTimeseries = $scope.item.data
        var t1 = this
        
        // due to async nature of http load then the headline data may not be available
        // so place a watch upon it
        if (!$scope.item.data) {
        	$scope.$watch('item.data', function() {
        		Chart.buildChart(t1, $scope.item.data, false)
        	})
        } else {
        	Chart.buildChart(t1, $scope.item.data, false)
        }        
	}

})()