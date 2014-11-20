(function() {
	'use strict';

	angular.module('onsTemplates')
		.controller('T1Controller', ['$scope', 'Taxonomy', T1Controller])

	function T1Controller($scope, Taxonomy) {
		var t1 = this
		var sections = $scope.taxonomy.data.sections

		initialize()

		function initialize() {
			for (var i = 0; i < sections.length; i++) {
				for (var j = 0; j < sections[i].items.length; j++) {
					// if (sections[i].name.indexOf("Economy") != -1) {
					// 	sections[i].name = "Economy"
					// } else if (sections[i].name.indexOf("Business") != -1) {
					// 	sections[i].name = "Business"
					// } else if (sections[i].name.indexOf("Employment") != -1) {
					// 	sections[i].name = "Employment"
					// } else if (sections[i].name.indexOf("Population") != -1) {
					// 	sections[i].name = "Population"
					// }

					loadItemData(sections[i].items[j])
				};
			};

		}

		function loadItemData(item) {
			Taxonomy.loadItem(item)
				.then(function(data) {
					item.chartData = Taxonomy.resolveChartData(item)
				}, handleDataLoadError)
		}

		function handleDataLoadError(err) {

		}

	}

})()