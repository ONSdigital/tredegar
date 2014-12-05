//Route configuration
(function() {

	'use strict';


	angular.module('onsApp')
		.config(['$routeProvider', '$locationProvider', '$httpProvider', RotueConfigration])
		.factory('OnsHttpInterceptor', ['$q', '$rootScope', OnsHttpInterceptor])


	function RotueConfigration($routeProvider, $locationProvider, $httpProvider) {

		$routeProvider.
		when('/aboutus', {
			templateUrl: 'app/templates/about/aboutus.html',
			controller: "AboutCtrl",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('About')
				}]
			}
		}).
		when('/accessibility', {
			templateUrl: 'app/templates/accessibility/accessibility.html',
			controller: "AccessibilityCtrl",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Accessibility')
				}]
			}
		}).
		when('/alpha', {
			templateUrl: 'app/templates/alphapage/alphapage.html',
			controller: "AlphaPageCtlr",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Protoype')
				}]
			}
		}).
		when('/calendar', {
			templateUrl: 'app/templates/calendar/calendar.html',
			controller: "CalendarCtlr",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Release Calendar')
				}]
			}
		}).
		when(':collectionPath*\/collection', {
			templateUrl: 'app/templates/collection/collection.html',
			controller: "CollectionCtrl",
			controllerAs: "collection",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Collections')
				}]
			}
		}).
		when('/contactus', {
			templateUrl: 'app/templates/contact/contactus.html',
			controller: "ContactUsCtrl",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Contact Us')
				}]
			}
		}).
		when('/copyright', {
			templateUrl: 'app/templates/copyright/copyright.html',
			controller: "CopyrightCtrl",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Copyright')
				}]
			}
		}).
		when('/dataversions', {
			templateUrl: 'app/templates/dataversions/dataversions.html',
			controller: "DataversionsCtrl",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Data Versions')
				}]
			}
		}).
		when('/localstats', {
			templateUrl: 'app/templates/localstats/localstats.html',
			controller: "LocalStatsCtlr",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Local Statistics')
				}]
			}
		}).
		when('/methodology', {
			templateUrl: 'app/templates/methodology/methodology.html',
			controller: "MethodologyCtrl",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Methodology')
				}]
			}
		}).
		when('/nationalstats', {
			templateUrl: 'app/templates/nationalstats/nationalstats.html',
			controller: "NationalStatsCtlr",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('National Statistics')
				}]
			}
		}).
		when('/previous', {
			templateUrl: 'app/templates/previoustatic/previoustatic.html',
			controller: "PreviousCtrl",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Previous Versions')
				}]
			}
		}).
		when('/privacy', {
			templateUrl: 'app/templates/privacy/privacy.html',
			controller: "PrivacyCtrl",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Privacy')
				}]
			}
		}).
		when('/search/:searchTerm?', resolveSearch()).
		when('/search/:searchTerm/page/:page', resolveSearch()).
		when('/survey', {
			templateUrl: 'app/templates/survey/survey.html',
			controller: "SurveyCtrl",
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('Survey')
				}]
			}
		}).
		when('/404', {
			templateUrl: '/app/templates/error-pages/error404.html',
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('404')
				}]
			}
		}).
		when('/500', {
			templateUrl: '/app/templates/error-pages/error500.html',
			resolve: {
				title: ['PageUtil', function(PageUtil) {
					PageUtil.setTitle('500')
				}]
			}
		}).
		otherwise(resolveTaxonomyTemplate())

		function resolveTaxonomyTemplate() {
			var routeConfig = {
				resolve: {
					data: ['Taxonomy', 'PageUtil',
						function(Taxonomy, PageUtil) {
							var promise = Taxonomy.loadData()
							promise.then(function(data) {
								PageUtil.setTitle(data.name)
							})
							return promise
						}
					]
				},
				templateUrl: 'app/templates/taxonomy/taxonomy.html',
				controller: 'TaxonomyController',
				controllerAs: 'taxonomy'


			}

			return routeConfig
		}

		function resolveSearch() {
			var routeConfig = {
				resolve: {
					searchResponse: ['PageUtil', 'DataLoader', '$route', search],
					title: ['PageUtil', function(PageUtil) {
						PageUtil.setTitle('Search Results')
					}]
				},
				templateUrl: 'app/templates/search-results/search-results.html',
				controller: 'SearchController'
			}
			return routeConfig
		}

		function search(PageUtil, DataLoader, $route) {
			var params = $route.current.params
			var q = params['searchTerm']
			var type = params['type']
			var pageNumber = params['page']
			var searchString = "?q=" + q + (pageNumber ? "&page=" + pageNumber : "") + getTypesString(type)
				// var searchString = PageUtil.getUrl()
			return DataLoader.load("/search" + searchString)
				.then(function(data) {
					//If cdid search is made go directly to timeseries page for searched cdid
					if (data.type && data.type === 'timeseries') {
						PageUtil.goToPage(data.uri, true)
						return
					}
					return data
				}, function() {
					console.log('Failed loading search results')
				})
		}

		function getTypesString(type) {
			if (!type) {
				return ''
			}
			if (typeof type === 'string') {
				return '&type=' + type
			}

			var typeString = ''
			for (var i = 0; i < type.length; i++) {
				typeString += '&type=' + type[i]
			};
			return typeString
		}

		function setTitle(title, PageUtil) {
			PageUtil.setTitle(title)
		}

		$httpProvider.responseInterceptors.push('OnsHttpInterceptor')

	}

	function OnsHttpInterceptor($q, $rootScope) {

		watchLocation()

		//Delete existing error on succesfull page load
		function watchLocation() {
			$rootScope.$on('$locationChangeStart', function(event) {
				delete $rootScope.error
			})
		}

		return function(promise) {
			// pass success (e.g. response.status === 200) through
			return promise.then(function(response) {
					return response
				},
				// otherwise deal with any error scenarios
				function(response) {
					if (response.status === 500) {
						$rootScope.error = 500
					}
					if (response.status === 404) {
						$rootScope.error = 404
					}
					return $q.reject(response)
				});
		};
	}


})()
