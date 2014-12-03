//Route configuration
(function() {

	'use strict';


	angular.module('onsApp')
		.config(['$routeProvider', '$locationProvider', '$httpProvider', RotueConfigration])
		.factory('OnsHttpInterceptor', ['$q', '$location', OnsHttpInterceptor])


	function RotueConfigration($routeProvider, $locationProvider, $httpProvider) {

		$routeProvider.
		when('/about', {
			templateUrl: 'app/templates/about/about.html',
			controller: "AboutCtrl",
		}).
		when('/alpha', {
			templateUrl: 'app/templates/alphapage/alphapage.html',
			controller: "AlphaPageCtlr",
		}).
		when('/calendar', {
			templateUrl: 'app/templates/calendar/calendar.html',
			controller: "CalendarCtlr",
		}).
		when(':collectionPath*\/collection', {
			templateUrl: 'app/templates/collection/collection.html',
			controller: "CollectionCtrl",
			controllerAs: "collection",
		}).
		when('/contactus', {
			templateUrl: 'app/templates/contact/contactus.html',
			controller: "ContactUsCtrl",
		}).
		when('/copyright', {
			templateUrl: 'app/templates/copyright/copyright.html',
			controller: "CopyrightCtrl",
		}).
		when('/dataversions', {
			templateUrl: 'app/templates/dataversions/dataversions.html',
			controller: "DataversionsCtrl",
		}).
		when('/localstats', {
			templateUrl: 'app/templates/localstats/localstats.html',
			controller: "LocalStatsCtlr",
		}).
		when('/methodology', {
			templateUrl: 'app/templates/methodology/methodology.html',
			controller: "MethodologyCtrl",
		}).
		when('/nationalstats', {
			templateUrl: 'app/templates/nationalstats/nationalstats.html',
			controller: "NationalStatsCtlr",
		}).
		when('/previous', {
			templateUrl: 'app/templates/previoustatic/previoustatic.html',
			controller: "PreviousCtrl",
		}).
		when('/privacy', {
			templateUrl: 'app/templates/privacy/privacy.html',
			controller: "PrivacyCtrl",
		}).
		when('/release', {
			templateUrl: 'app/templates/release/release.html',
		}).
		when('/search', {
			resolve: {
				searchResponse: ['PageUtil', 'DataLoader', search]
			},
			templateUrl: 'app/templates/search-results/search-results.html',
			controller: 'SearchController'
		}).
		when('/survey', {
			templateUrl: 'app/templates/survey/survey.html',
			controller: "SurveyCtrl",
		}).
		when('/404', {
			templateUrl: '/app/templates/error-pages/error404.html',
		}).
		when('/500', {
			templateUrl: '/app/templates/error-pages/error500.html',
		}).
		otherwise(resolveTaxonomyTemplate())

		function resolveTaxonomyTemplate() {
			var routeConfig = {
				resolve: {
					data: ['Taxonomy',
						function(Taxonomy) {
							return Taxonomy.loadData()
						}
					]
				},
				templateUrl: 'app/templates/taxonomy/taxonomy.html',
				controller: 'TaxonomyController',
				controllerAs: 'taxonomy'


			}

			return routeConfig
		}

		function search(PageUtil, DataLoader, $log) {
			var q = PageUtil.getUrlParam('q')
				// var type = PageUtil.getUrlParam('type')
				// var pageNumber = PageUtil.getUrlParam('page')
				// var searchString = "?q=" + q + getTypeString(type) + (pageNumber ? "&page=" + pageNumber : "")
			var searchString = PageUtil.getUrl()
			return DataLoader.load("/search" + searchString)
				.then(function(data) {
					//If cdid search is made go directly to timeseries page for searched cdid
					if (data.type && data.type === 'timeseries') {
						PageUtil.goToPage(data.uri, true)
						return
					}
					return data
				}, function() {
					$log.error('Failed loading search results')
				})
		}

		$httpProvider.responseInterceptors.push('OnsHttpInterceptor')

	}

	function OnsHttpInterceptor($q, $location) {
		return function(promise) {
			// pass success (e.g. response.status === 200) through
			return promise.then(function(response) {
					return response
				},
				// otherwise deal with any error scenarios
				function(response) {
					if (response.status === 500) {
						$location.url('/500')
					}
					if (response.status === 404) {
						$location.url('/404')
					}
					return $q.reject(response)
				});
		};
	}


})()