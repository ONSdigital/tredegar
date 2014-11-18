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
		var scopedTimeseries = $scope.item
        var t1 = this
        Chart.buildChart(t1, scopedTimeseries, false)
	}

})()