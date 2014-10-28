// Taxonomy base, data is injected. Data is loaded throug route service
(function() {
	'use strict';

	angular.module('onsTemplates')
		.controller('TaxonomyController', ['$scope', 'Taxonomy',
			TaxonomyController
		])

	function TaxonomyController($scope, Taxonomy) {
		var taxonomy = this
		Taxonomy.loadData(function(data) {
			taxonomy.data = data
			prepareBreadcrumb($scope, data)
		})
	}

	function prepareBreadcrumb($scope, data) {
		if(data.level === 't1') {
			return
		}
		$scope.breadcrumb = {}
		$scope.breadcrumb.parent = data.breadcrumb
		$scope.breadcrumb.current = data.name
	}

})()