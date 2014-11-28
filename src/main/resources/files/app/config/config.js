(function() {

	//Ons alpha global configuration, available throught $rootScope, use $rootScope.onsAlphaConfiguration to read
	var onsAlphaConfiguration = {
		//Disable cache on localhost or not
		disableCacheOnLocal: true,

		//Number of timeseries counts to be loaded by default on t3
		defaultTimeseriesCountOnT3: 5,

		//Autocomplete items limit
		autoCompleteLimit: 5,

		//Debug logs
		debugEnabled: false
	}

	angular.module('onsAppConfig', [])
		.config(['$locationProvider', '$httpProvider', '$logProvider', GeneralConfiguration])
		.run(['$location', '$http', '$rootScope', 'DSCacheFactory', '$cacheFactory', '$log', RunConfiguration])


	function GeneralConfiguration($locationProvider, $httpProvider, $logProvider) {
		//Enable hashbang
		$locationProvider.html5Mode(false).hashPrefix('!')
		$logProvider.debugEnabled(onsAlphaConfiguration.debugEnabled)
	}

	function RunConfiguration($location, $http, $rootScope, DSCacheFactory, $cacheFactory, $log) {
		$rootScope.onsAlphaConfiguration = onsAlphaConfiguration

		configureCache()
		configureGoogleAnalytics()

		function configureGoogleAnalytics() {
			$rootScope.$on('$routeChangeSuccess', function() {
				var path = $location.path()
				ga('send', 'pageview', path);
				$log.debug('Google Analytics pageview sent :', path)
			})
		}

		//Local storage cache, using angular-data.DSCacheFactory
		//Refer to: http://angular-data.pseudobry.com/documentation/guide/angular-cache/storage
		function configureCache() {
			$log.info('Configuring data cache')

			var CACHE_NAME = 'dataCache'

			// Conditionally use Angular cache if local storage not supported
			//TODO: Create a caching design appropriate to 9.30 caching (e.g No caching between 9.30 - 9.31, expire all cache at 9.30) 
			var options = {
				maxAge: 1800000, // Items added to this cache expire after 30 minutes.
				cacheFlushInterval: 10800000, // This cache will clear itself every three hours.
				deleteOnExpire: 'aggressive', // Items will be deleted from this cache right when they expire.
				storageMode: 'localStorage' // This cache will sync itself with `localStorage`.
			};

			if (!hasLocalStorage()) {
				$log.warn('Local storage not supported, using angular cache')
				options.storageImpl = getAngularCache()
			}
			var dataCache = DSCacheFactory('dataCache', options);

			//Skip caching if localhost and cache disabled
			if ($location.host() === 'localhost') {
				if (onsAlphaConfiguration.disableCacheOnLocal) {
					return
				}
			}

			//Set http default cache for all http get calls to be cached
			$http.defaults.cache = dataCache


			//Angular cache only uses session storage which is cleared itself when page is refreshed or browser closed
			function getAngularCache() {
				var cache = $cacheFactory('dataCache');

				function getItem(key) {
					// $log.debug('Angular cache getItem(): ', key)
					return cache.get(key)
				}

				function setItem(key, value) {
					// $log.debug('Angular cache setItem(): ', key, ',', value)
					return cache.put(key, value)
				}

				function removeItem(key) {
					// $log.debug('Angular cache removeItem(): ', key)
					cache.remove(key)
				}

				return localStoragePolyfill = {
					getItem: getItem,
					setItem: setItem,
					removeItem: removeItem
				}

			}

			function hasLocalStorage() {
				try { //Checking local storage fails if exlplicitly disable in some browsers (e.g. chrome),
					if (window.localStorage) {
						return true
					}
				} catch (err) {
					$log.warn('Failed detecting local storage, disabling local storage cache')
				}

				return false

			}

		}
	}

})()