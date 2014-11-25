//Main ons applucation code
'use strict';

(function() {

    // Components are to be injected to onsComponents module
    var onsComponents = angular.module('onsComponents', [
        'onsAccordion',
        'onsNavigation',
        'onsPaginator',
        'onsTabs',
        'highcharts-ng',
        'angularLoadingBar',
        'onsToggler',
        'onsLoading',
        'onsSparkline',
	   'onsTooltip'
    ])

    //Filters, services and other helpers are to be injected to onsHelpers module
    var onsHelpers = angular.module('onsHelpers', [
        'onsFilters',
        'onsTaxonomy',
        'onsDownloader',
        'onsUtils',
        'onsDataLoader',
        'angular-data.DSCacheFactory'
    ])
    //Template related components are to be registered to onsTemplates module
    var onsTemplates = angular.module('onsTemplates', [])

    /* App Module */
    var onsApp = angular.module('onsApp', [
        'ngRoute',
        'ngSanitize',
        'onsComponents',
        'onsHelpers',
        'onsTemplates'
    ])

    onsApp
        .config(['$routeProvider', '$locationProvider', '$httpProvider',
            function($routeProvider, $locationProvider, $httpProvider) {


                $routeProvider.
                when(':collectionPath*\/collection', {
                    templateUrl: 'app/templates/collection/collection.html',
                    controller: "CollectionCtrl",
                    resolve: {
                        navigation: ['DataLoader', getNavigatinLinks]
                    }
                }).
                when('/contactus', {
                    templateUrl: 'app/templates/contact/contactus.html',
                    controller: "ContactUsController",
                    resolve: {
                        navigation: ['DataLoader', getNavigatinLinks]
                    }
                }).
                when('/dataversions', {
                    templateUrl: 'app/templates/dataversions/dataversions.html',
                    controller: "DataversionsCtrl",
                    resolve: {
                        navigation: ['DataLoader', getNavigatinLinks]
                    }
                }).
                when('/localstats', {
                    templateUrl: 'app/templates/localstats/localstats.html',
                    controller: "LocalStatsCtlr",
                    resolve: {
                        navigation: ['DataLoader', getNavigatinLinks]
                    }
                }).
                when('/release', {
                    templateUrl: 'app/templates/release/release.html',
                    resolve: {
                        navigation: ['DataLoader', getNavigatinLinks]
                    }
                }).
                when('/search', {
                    templateUrl: 'app/templates/search-results/search-results.html',
                    controller: 'SearchController',
                    resolve: {
                        navigation: ['DataLoader', getNavigatinLinks]
                    }
                }).
                when('/404', {
                    templateUrl: '/app/partials/error-pages/error404.html',
                    resolve: {
                        navigation: ['DataLoader', getNavigatinLinks]
                    }
                }).
                when('/500', {
                    templateUrl: '/500.html',
                    resolve: {
                        navigation: ['DataLoader', getNavigatinLinks]
                    }
                }).
                otherwise(resolveTaxonomyTemplate())

                function resolveTaxonomyTemplate() {
                    var routeConfig = {
                        resolve: {
                            // https://stackoverflow.com/questions/15975646/angularjs-routing-to-empty-route-doesnt-work-in-ie7
                            // Here we ensure that our route has the document fragment (#!), or more specifically that it has #!/ at a minimum.
                            // If accessing the base URL without a trailing '/' in IE7 it will execute the otherwise route instead of the signin
                            // page, so this check will ensure that '#!/' exists and if not redirect accordingly which fixes the URL.
                            redirectCheck: ['$location',
                                function($location) {
                                    var absoluteLocation = $location.absUrl();
                                    if (absoluteLocation.indexOf('#!/') === -1) {
                                        $location.path('/');
                                    }
                                }
                            ],
                            data: ['Taxonomy',
                                function(Taxonomy) {
                                    return Taxonomy.loadData()
                                }
                            ],
                            navigation: ['DataLoader', getNavigatinLinks]
                        },
                        templateUrl: 'app/templates/taxonomy/taxonomy.html',
                        controller: 'TaxonomyController',
                        controllerAs: 'taxonomy'


                    }

                    return routeConfig

                }

                function getNavigatinLinks(DataLoader) {
                    return DataLoader.load('/navigation')
                }


                // TODO: add interceptor to capture 404 scenarios, pending confirmation of requirement
                //                $httpProvider.responseInterceptors.push('OnsHttpInterceptor')

            }
        ])

    onsApp
        .controller('MainCtrl', ['$scope','PageUtil', '$location', '$anchorScroll',
            function($scope, PageUtil, $location, $anchorScroll) {
                var ctrl = this

                $scope.scrollTo = function(id) {
                    $location.hash(id)
                    $anchorScroll()
                    $location.hash(null)
                }

                $scope.getPath = function(){
                    return PageUtil.getPath()
                }

                $scope.getPage = function(){
                    return PageUtil.getPage()
                }
            }
        ])

    onsApp.factory('OnsHttpInterceptor', function($q, $location) {
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
    });

})()
