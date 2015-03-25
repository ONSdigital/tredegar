(function() {

'use strict';
	angular.module('onsDataLoader', [])
		.service('DataLoader', ['$rootScope', '$http', '$log', '$q', 'DSCacheFactory', '$location', DataLoader])

	/*
	Data Loader Service caches each http call to local storage if available, otherwise uses angular cache. see config.js for details
	*/
	function DataLoader($rootScope, $http, $log, $q, DSCacheFactory, $location) {
		var dataLoader = this
		var cache = DSCacheFactory.get('dataCache')

		function load(path) {
			var deferred
			var cachedData; // = loadFromCache(path)

			if (cachedData) { //Cache hit
				$log.debug('Data Loader : Cached data hit for ', path, ' ', cachedData.data)
				deferred = $q.defer()
				deferred.resolve(cachedData.data)
				return deferred.promise
			} else { //No cache hit, load
				return $http.get(path, {cache:false})
					.then(function(data) {
							$log.debug('Data Loader : Successfully loaded data at ', path, ' ', data.data)
								/*Store http response rather than actual data, 
								since http default caching will cache http response and autocomplete uses http service default cache 
								which is the same cache (dataCache)
								*/
							storeToCache(path, data)
							return data.data
						},
						function(err) {
							$log.error('Data Loader : Failed loading data at ')
							$log.error(err)
							throw err
						})
			}
		}

		function storeToCache(path, data) {
			try {
				//Skip caching if localhost and cache disabled
				if ($location.host() === 'localhost') {
					if ($rootScope.onsAlphaConfiguration.disableCacheOnLocal) {
						return
					}
				}

				cache.put(path, data)
			} catch (err) {
				$log.error('Failed storing data to cache: [' + err + ']' );
			}

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



})();