'use strict';

(function() {

	angular.module('onsTemplates')
		.controller('T3Controller', ['$scope', 'Taxonomy', T3Controller])

	function T3Controller($scope, Taxonomy) {
		var t3 = this
		var data = $scope.taxonomy.data
		var timeseriesCount = data.items.length
		var timeseriesDefaultLimit = 5
		t3.loadedTimseriesCount = 0
		var loadingMore = false
		initialize()

		function initialize() {
			loadItem(data.headline) //Load headline
			t3.loadedTimseriesCount-- //Reset timeseries count after headline
				loadItem(data.statsBulletinHeadline) //Load stats bulletins related to headline
			loadItems(data.items, timeseriesDefaultLimit) //Load timeseries
			loadItems(data.statsBulletins) //Load timeseries
			loadItems(data.datasets) //Load datasets
		}


		function loadItems(items, limit) {
			limit = limit || items.length
				// Load all items if less than limit
			limit = limit > items.length ? items.length : limit
			for (var i = 0; i < limit; i++) {
				loadItem(items[i])
			};
		}

		function loadItem(item) {
			var promise = Taxonomy.loadItem(item)
			if (promise) {
				promise
					.then(function(data) {
						if (data.type === "timeseries") {
							t3.loadedTimseriesCount++
								if (!hasMore()) {
									loadingMore = false
								}
							item.chartData = Taxonomy.resolveChartData(item)

						}
					}, handleDataLoadError)
			}

		}


		function handleDataLoadError(err) {
			//Handle data load error
		}


		function isLoading() {
			return loadingMore
		}

		function hasMore() {
			return timeseriesCount > t3.loadedTimseriesCount
		}

		function loadAll() {
			loadingMore = true
			loadItems(data.items.slice(t3.loadedTimseriesCount))
		}

		angular.extend(t3, {
			isLoading: isLoading,
			hasMore: hasMore,
			loadAll: loadAll
		})
	}

})()