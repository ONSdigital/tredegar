'use strict';

(function() {
	angular.module('onsDataLoader', [])
		.service('DataLoader', ['$http', '$log', 'DSCacheFactory', DataLoader])


	/*
	Data Loader Service caches each http call to local storage if available, otherwise uses angular cache. see config.js for details
	*/
	function DataLoader($http, $log, DSCacheFactory) {
		var dataLoader = this
		var cache = DSCacheFactory.get('dataCache')

		function load(path, callback) {
			var data = loadFromCache(path)
			if(data && callback) { //Cache hit
				$log.debug('Data Loader : Cached data hit for ', path, ' ', data)
				callback(data)
				return
			}	

			//No hit
			$http.get(path).success(function(data) {
				$log.debug('Data Loader : Successfully loaded data at ', path, ' ', data)
				cache.put(path, data)
				if (callback) {
					callback(data)
				}
			}).error(function() {
				$log.error('Data Loader : Failed loading data at ' + path)
			})

		}

		function loadFromCache(key) {
			return cache.get(key)
		}

		/* Load data using post request
		 */
		function loadPost(path, requestData, callback) {
			$log.debug('Data Loader Post: Loading data at ', path, ' ,requesting ', requestData)

			$http.post(path, requestData).success(function(data) {
				$log.debug('Data Loader Post: Successfully loaded data at ', path, ' ', data)
				if (callback) {
					callback(data)
				}
			}).error(function() {
				$log.error('Data Loader Post : Failed loading data at ' + path)
			})
		}

		//Expose API
		angular.extend(dataLoader, {
			load: load,
			loadPost: loadPost
		})

	}



})()