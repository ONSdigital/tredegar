'use strict';

(function() {
	angular.module('onsDataLoader', [])
		.service('DataLoader', ['$rootScope', '$http', '$log', '$q', 'DSCacheFactory', '$location', DataLoader])

	/*
	Data Loader Service caches each http call to local storage if available, otherwise uses angular cache. see config.js for details
	*/
	function DataLoader($rootScope, $http, $log, $q, DSCacheFactory, $location) {
		var dataLoader = this
		var cache = DSCacheFactory.get('dataCache')

		function load(path) {
			var deferred = $q.defer()
			var data = loadFromCache(path)

			if (data) { //Cache hit
				$log.debug('Data Loader : Cached data hit for ', path, ' ', data)
				deferred.resolve(data)
			} else { //No cache hit, load
				$http.get(path).success(function(data) {
					$log.debug('Data Loader : Successfully loaded data at ', path, ' ', data)
					storeToCache(path,data)
					deferred.resolve(data)
				}).error(function(err) {
					$log.error('Data Loader : Failed loading data at ' + path)
					$log.error(err)
					deferred.reject(err)
				})
			}


			return deferred.promise
		}

		function storeToCache(path,data) {
			//Skip caching if localhost and cache disabled
			if ($location.host() === 'localhost') {
				if ($rootScope.onsAlphaConfiguration.disableCacheOnLocal) {
					return
				}
			}

			cache.put(path, data)
			
		}

		function loadFromCache(key) {
			var data
			
			if ($location.host() === 'localhost') {
				if ($rootScope.onsAlphaConfiguration.disableCacheOnLocal) {
					return 
				}
			} 
			
			data = cache.get(key)
			return data
		}

		/* Load data using post request
		 */
		function loadPost(path, requestData, options) {
			$log.debug('Data Loader Post: Loading data at ', path, ' ,requesting ', requestData)

			var deferred = $q.defer()

			$http.post(path, requestData, options).success(function(data) {
				$log.debug('Data Loader Post: Successfully loaded data at ', path, ' ', data)
				deferred.resolve(data)
			}).error(function(err) {
				$log.error('Data Loader Post : Failed loading data at ' + path)
				deferred.resolve(err)
			})

			return deferred.promise
		}

		//Expose API
		angular.extend(dataLoader, {
			load: load,
			loadPost: loadPost
		})

	}



})()