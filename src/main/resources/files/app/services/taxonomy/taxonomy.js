(function() {
	'use strict';

	angular.module('onsTaxonomy', [])
		.service('Taxonomy', ['$log', '$location', '$q', 'DataLoader', 'StringUtil', 'ArrayUtil',
			TaxonomyService
		])

	function TaxonomyService($log, $location, $q, DataLoader, StringUtil, ArrayUtil) {

		var service = this
		var dataPath = '/data'
		var bulkDataPath = '/datalist'
		var loadSynchronously = false

		function loadData() {
			return DataLoader.load(resolvePath())
		}

		//Load single item and push data into item as data
		function loadItem(item) {
			// if data is undefined then break out of this method
			if (!item) {
				return
			}
			var path = dataPath + item.uri
			return DataLoader.load(path)
				.then(function(itemData) {
					item.data = itemData
					return itemData
				})
		}

		//Load timeseries data and resolve chart data
		function resolveChartData(timeseries) {
			// if data is undefined then break out of this method
			if (!timeseries) {
				return
			}

			//Resolve which data should appear on sparkline. priority: yearly, quarterly, monthly
			var chartData 
			var data = timeseries.data
			if (ArrayUtil.isNotEmpty(data.years)) {
				chartData = data.years
			} else if (ArrayUtil.isNotEmpty(data.quarters)) {
				chartData = data.quarters
			} else if (ArrayUtil.isNotEmpty(data.months)) {
				chartData = data.months
			} else {
				$log.error("No timeseries data found for ", timeseries.name)
				return
			}
			return chartData
		}

		//Loads given items asynchronously and pushes into given array. Overrides array if exists
		function loadItems(items) {
			if (ArrayUtil.isEmpty(items)) {
				return
			}

			var promises = []

			for (var i = 0; i < items.length; i++) {
				var itemPath = dataPath + items[i].uri
				var promise = loadItem(items[i])
				if (promise) {
					promises.push(promise)
				}

			}

			return promises
		}


		//Bulk loads items with a single call and pushes into given array. Overrides array if exists
		function loadItemsBulk(items) {
			if (ArrayUtil.isEmpty(items)) {
				return
			}

			var request = {
				uriList: []
			}

			for (var i = 0; i < items.length; i++) {
				request.uriList.push(items[i].uri)
			};

			DataLoader.loadPost(bulkDataPath, request).then(function(data) {
				for (var i = 0; i < data.length; i++) {
					items[i].data = data[i]
				};
			})
		}

		function resolvePath() {
			var path = $location.$$path
			path = StringUtil.endsWith(path, '/') ? path : (path + '/')
			path = dataPath + path
			return path
		}

		//Expose public api
		angular.extend(service, {
			loadData: loadData,
			loadItem: loadItem,
			resolveChartData: resolveChartData
		})

	}

})()