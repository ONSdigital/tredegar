'use strict';

(function() {

	angular.module('onsTemplates')
		.controller('T3Controller', ['$scope', 'Taxonomy', T3Controller])

	function T3Controller($scope, Taxonomy) {
		var t3 = this
		var data = $scope.taxonomy.data
		var timeseriesCount = data.items.length
		t3.timeseriesDefaultLimit = 5
		t3.loadedTimseriesCount = 0
		t3.allVisible = false
		t3.showToggle = timeseriesCount > t3.timeseriesDefaultLimit
		var loadingMore = false //Loading in progress

		initialize()

		function initialize() {
			loadItem(data.headline) //Load headline
			t3.loadedTimseriesCount-- //Reset timeseries count after headline
				loadItem(data.statsBulletinHeadline) //Load stats bulletins related to headline
			loadItems(data.items, t3.timeseriesDefaultLimit) //Load timeseries
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
									t3.allVisible=true
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

		//View more/less timeseries
		function toggleTimeSeries() {
			var more = hasMore()
			if (t3.allVisible) {
				t3.allVisible = false
				return
			} else {
				t3.allVisible = true
				if (more) {
					loadingMore = true
					loadItems(data.items.slice(t3.loadedTimseriesCount))
				}
			}
		}



		//Expose API
		angular.extend(t3, {
			isLoading: isLoading,
			toggleTimeSeries: toggleTimeSeries
		})
	}

})()