// Taxonomy base, data is injected. Data is loaded throug route service
(function() {
	'use strict';

	var onsTemplates = angular.module('onsTemplates')
	onsTemplates.controller('TaxonomyController', ['Taxonomy',
		TaxonomyController
	])

	function TaxonomyController(Taxonomy) {
		var taxonomy = this
		Taxonomy.loadData(function(data) {
			taxonomy.data = data
			console.log("Here it is")
			console.log(data)
		})

	}

})()