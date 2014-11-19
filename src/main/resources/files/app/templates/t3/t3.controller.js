'use strict';

(function() {

	angular.module('onsTemplates')
		.controller('T3Controller', ['$scope', 'Taxonomy', T3Controller])

	function T3Controller($scope, Taxonomy) {
		var t3 = this
		var data = $scope.taxonomy.data

		initialize()

		function initialize() {
			loadItem(data.headline) //Load headline
			loadItem(data.statsBulletinHeadline) //Load stats bulletins related to headline
			loadItems(data.items) //Load timeseries
			loadItems(data.statsBulletins) //Load timeseries
		}


		function loadItems(items) {
			for (var i = 0; i < items.length; i++) {
				loadItem(items[i])
			};
		}

		function loadItem(item) {
			Taxonomy.loadItem(item)
				.then(function(data) {
					if (data.type === "timeseries") {
						item.chartData = Taxonomy.resolveChartData(item)
					}
				}, handleDataLoadError)
		}


		function handleDataLoadError(err) {
			//Handle data load error
		}


		function isLoading() {
			return true
		}

		angular.extend(t3, {
			isLoading: isLoading
		})
	}

})()