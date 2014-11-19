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
			var q = [] //Promise array
			var promise = DataLoader.load(resolvePath()).then(function(data) {
				service.data = data

				switch (data.level) {
					case 't1':
						q = resolveSections(data)
						if (loadSynchronously) {
							return $q.all(q).then(function() {
								return data
							})
						}
						break
					case 't3':
						q.push(loadItem(data.headline))
						q.push(loadItem(data.statsBulletinHeadline))
						q.push.apply(q, loadItems(data.items))
						q.push.apply(q, loadItems(data.statsBulletins))
						q.push.apply(q, loadItems(data.datasets))
						if (loadSynchronously) {
							return $q.all(q)
								.then(function() {
									return data
								})
						}
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
			var promises = []

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

				promises.push.apply(promises, loadItems(sections[i].items))
			}

			return promises
		}

		//Load single item and push data into item as data
		function loadItem(item) {
			// if data is undefined then break out of this method
			if (!item) {
				return
			}
			var path = dataPath + item.uri
			return DataLoader.load(path).then(function(itemData) {
				item.data = itemData
			})
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
			loadData: loadData
		})

	}

})()