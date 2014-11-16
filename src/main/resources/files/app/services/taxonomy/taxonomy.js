(function() {
	'use strict';

	angular.module('onsTaxonomy', [])
		.service('Taxonomy', ['$log', '$location', 'DataLoader', 'StringUtil', 'ArrayUtil',
			TaxonomyService
		])

	function TaxonomyService($log, $location, DataLoader, StringUtil, ArrayUtil) {

		var service = this
		var dataPath = '/data'
		var bulkDataPath = '/datalist'

		function loadData() {

			var promise = DataLoader.load(resolvePath()).then(function(data) {
				service.data = data

				switch (data.level) {
					case 't1':
						resolveSections(data)
						break
					case 't3':
						data.itemData = []
						data.statsBulletinData = []
						data.keyDatasets = []
						loadItem(data, data.headline, 'headlineData')
						loadItem(data, data.statsBulletinHeadline, 'statsBulletinHeadlineData')
						loadItems(data, data.items, 'itemData')
						loadItems(data, data.statsBulletins, 'statsBulletinData')
						loadItems(data, data.datasets, 'keyDatasets')
						break
					default:
				}

				return data
			})

			return promise
		}

		function resolveSections(data) {
			$log.debug('Taxonomy Service: Resolving sections of ', data.name)
			var level = data.level
			var sections = data.sections

			if (!sections) {
				return
			}
			for (var i = 0; i < sections.length; i++) {
				if (data.level === 't1') {
					// Shorten section names for T1:
					if (sections[i].name.indexOf("Economy") != -1) {
						sections[i].name = "Economy"
					} else if (sections[i].name.indexOf("Business") != -1) {
						sections[i].name = "Business"
					} else if (sections[i].name.indexOf("Employment") != -1) {
						sections[i].name = "Employment"
					} else if (sections[i].name.indexOf("Population") != -1) {
						sections[i].name = "Population"
					}
				}

				loadItems(sections[i], sections[i].items, 'itemData')

			}
		}

		//Load single item and push into container's given variable, overrides if exists
		function loadItem(container, item, varName) {
			var path = dataPath + item
			DataLoader.load(path).then(function(itemData) {
				itemData.url = item
					//Create array if not available
				container[varName] = itemData
			})
		}

		//Loads given items asynchronously and pushes into given array. Overrides array if exists
		function loadItems(container, items, arrayName) {
			if (ArrayUtil.isEmpty(items)) {
				return
			}

			container[arrayName] = []
			for (var i = 0; i < items.length; i++) {
				var item = items[i]
				var itemPath = dataPath + item
				DataLoader.load(itemPath).then(function(itemData) {
					itemData.url = item
					container[arrayName].push(itemData)
				})
			}
		}


		//Bulk loads items with a single call and pushes into given array. Overrides array if exists
		function loadItemsBulk(container, items, arrayName) {
			if (ArrayUtil.isEmpty(items)) {
				return
			}

			var request = {
				uriList: items
			}
			container[arrayName] = []
			DataLoader.loadPost(bulkDataPath, request).then(function(data) {
				for (var i = 0; i < data.length; i++) {
					data[i].url = items[i]
					container.itemData.push(data[i])
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
			loadData: loadData
		})

	}

})()