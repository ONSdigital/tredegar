(function() {
	'use strict';

	angular.module('onsTaxonomy', [])
		.service('Taxonomy', ['$http', '$log', '$location',
			TaxonomyService
		])

	function TaxonomyService($http, $log, $location) {

		var service = this
		var dataPath = '/data'

		function loadData(callback) {
			load(resolvePath(), function(data) {
				service.data = data
				
				if(data.level === 't1' || data.level === 't2') {
					resolveSections(data)
				} else if(data.level === 't3') {
					data.itemData = []
					loadItems(data, data.items)
					loadHeadline(data)
				}
				if (callback) {
					callback(service.data)
				}
			})
		}

		function resolveSections(data) {
			$log.debug('Taxonomy Service: Resolving sections of ', data.name)
			var level =  data.level
			var sections =  data.sections
			
			if (!sections) {
				return
			}
			for (var i = 0; i < sections.length; i++) {
				sections[i].itemData = []
				loadItems(sections[i], sections[i].items)
			}

		}

		function loadItems(section, items) {
			if (!items) {
				return
			}
			for (var i = 0; i < items.length; i++) {
				loadItem(section, items[i])
			}
		}

		function loadItem(section, item) {
			var path = dataPath + item
			load(path, function(data) {
				data.url = item
				section.itemData.push(data)
			})
		}


		function loadHeadline(data) {
			var timeseriesPath = dataPath + data.headline
			load(timeseriesPath, function(timeseries) {
				timeseries.url = data.headline
				data.headlineData = timeseries
			})
		}

		//Loads and attaches data to given parent element with given property name
		function load(path, callback) {
			var result = $http.get(path).success(function(data) {
				$log.debug('Taxonomy Service : Successfully loaded data at ', path, ' ', data)
				callback(data)
			}).error(function() {
				$log.error('Taxonomy Service : Failed loading data at ' + path)
			})
		}

		function resolvePath() {
			var path = $location.$$path
			path = endsWith(path, '/') ? path : (path + '/')
			path = dataPath + path
			return path
		}

		function endsWith(str, suffix) {
			if (str.length === 0) {
				return false
			}
			return str.indexOf(suffix, str.length - suffix.length) !== -1;
		}


		//Expose public api
		angular.extend(service, {
			loadData: loadData
		})

	}

})()