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
					data.statsBulletinData = []
					data.keyDatasets = []
					loadItems(data, data.items)
					loadHeadline(data)
					loadStatsBulletinHeadline(data)
					loadStatsBulletins(data)
					loadDatasets(data)
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
				if(data.level === 't1') {
					// Shorten section names for T1:
					if (sections[i].name.indexOf("Economy")!=-1) {
						sections[i].name="Economy"
					} else if (sections[i].name.indexOf("Business")!=-1) {
						sections[i].name="Business"
					} else if (sections[i].name.indexOf("Employment")!=-1) {
						sections[i].name="Employment"
					} else if (sections[i].name.indexOf("Population")!=-1) {
						sections[i].name="Population"
					} 
				}
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
		
		function loadStatsBulletinHeadline(data) {
			var statsBulletinPath = dataPath + data.statsBulletinHeadline
			load(statsBulletinPath, function(bulletin) {
				bulletin.url = data.statsBulletinHeadline
				data.statsBulletinHeadlineData = bulletin
				$log.debug('Loaded data.statsBulletinHeadlineData: ', statsBulletinPath, ' ', bulletin)
			})
		}
		
		function loadStatsBulletins(data) {
			var bulletins = data.statsBulletins;
			
			for (var i = 0; i < bulletins.length; i++) {
				var bulletin = bulletins[i]
				var statsBulletinPath = dataPath + bulletin
				$log.debug('statsBulletinPath: ' + statsBulletinPath)
				load(statsBulletinPath, function(statsBulletin) {
					$log.debug('Loaded stats bulletin: ', statsBulletinPath, ' ', statsBulletin)
					data.statsBulletinData.push(statsBulletin)
				})
			}
		}	
		
		function loadDatasets(data) {
			var datasets = data.datasets;
			
			for (var i = 0; i < datasets.length; i++) {
				var dataset = datasets[i]
				var datasetPath = dataPath + dataset
				$log.debug('datasetPath: ' + datasetPath)
				load(datasetPath, function(keyDataset) {
					$log.debug('Loaded keyDataset: ', datasetPath, ' ', keyDataset)
					data.keyDatasets.push(keyDataset)
				})
			}
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
			loadData: loadData,
			load: load
		})

	}

})()