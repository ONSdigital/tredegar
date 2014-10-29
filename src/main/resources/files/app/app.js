//Main ons applucation code
'use strict';

(function() {

    // Components are to be injected to onsComponents module
    var onsComponents = angular.module('onsComponents', ['onsAccordion', 'onsNavigation', 'onsPaginator',  'highcharts-ng'])

    //Filters, services and other helpers are to be injected to onsHelpers module
    var onsHelpers = angular.module('onsHelpers', ['onsFilters', 'onsTaxonomy'])

    //Template related components are to be registered to onsTemplates module
    var onsTemplates = angular.module('onsTemplates', [])

    /* App Module */
    var onsApp = angular.module('onsApp', [
        'ngRoute',
        'ngSanitize',
        'onsComponents',
        'onsHelpers',
        'onsTemplates',
        'googlechart',
        'smart-table'
    ])

    onsApp
        .config(['$routeProvider', '$locationProvider',
            function($routeProvider, $locationProvider) {
                $routeProvider.
                when('/article', {
                    templateUrl: 'app/templates/article/article.html',
                    controller: 'ArticleCtrl'
                }).
                when('/economy/inflationandpriceindices/collection', {
                    templateUrl: 'app/templates/collection/collection.html',
                    controller: "CollectionCtrl"
                }).
                when('/contactus', {
                    templateUrl: 'app/templates/contact/contactus.html',
                    controller: "ContactUsController"
                }).
                when('/dataset', {
                    templateUrl: 'app/templates/dataset/Dataset_Excelcrosssection.html',
                    controller: "DatasetCtrl"
                }).
                when('/dataset_timeseries', {
                    templateUrl: 'app/templates/dataset/Dataset_Excel_Time_Series.html',
                    controller: "DatasetCtrl"
                }).
                when('/economy/inflationandpriceindices/bulletin', {
                    templateUrl: 'app/templates/bulletin/bulletin.html',
                    controller: 'BulletinCtrl'
                }).
                when('/methodology', {
                    templateUrl: 'app/templates/methodology/methodology.html',
                    controller: 'MethodologyCtrl'
                }).
                when('/release', {
                    templateUrl: 'app/templates/release/release.html'
                }).
                when('/search', {
                    templateUrl: 'app/templates/search-results/search-results.html',
                    controller: 'SearchCtrl'
                }).
                when('/t6', {
                    templateUrl: 'app/templates/t6/t6.html',
                    controller: "T6Ctrl"
                }).
                otherwise({
                    resolve: {
                        // https://stackoverflow.com/questions/15975646/angularjs-routing-to-empty-route-doesnt-work-in-ie7
                        // Here we ensure that our route has the document fragment (#), or more specifically that it has #/ at a minimum.
                        // If accessing the base URL without a trailing '/' in IE7 it will execute the otherwise route instead of the signin
                        // page, so this check will ensure that '#/' exists and if not redirect accordingly which fixes the URL.
                        redirectCheck: ['$location',
                            function($location) {
                                var absoluteLocation = $location.absUrl();
                                if (absoluteLocation.indexOf('#!/') === -1) {
                                    $location.path('/');
                                }
                            }
                        ]
                        // ,
                        // theData: ['Taxonomy',
                        //     function(Taxonomy) {
                        //         Taxonomy.loadData(function(data) {
                        //             return data
                        //         });
                        //     }
                        // ]

                    },
                    templateUrl: 'app/templates/taxonomy/taxonomy.html',
                    controller: 'TaxonomyController',
                    controllerAs: 'taxonomy'
                })

            }
        ])

    onsApp
        .controller('MainCtrl', ['$scope', '$log', '$http', '$location', 'PathService', '$anchorScroll',
            function($scope, $log, $http, $location, PathService, $anchorScroll) {
                var ctrl = this

                loadNavigation()

                function loadNavigation() {
                    $http.get("/navigation").success(function(data) {
                        $log.debug('Main Ctrl: Loading navigation data')
                        $scope.navigation = data
                        $log.debug('Main Ctrl: navigation data loaded successfully ', data)
                    })
                }

                $scope.getPath = function() {
                    return PathService.getPath()
                }

                $scope.getPage = function() {
                    return PathService.getPage()
                }

                $scope.getUrlParam = function(paramName) {
                    var params = $location.search()
                    return params[paramName]
                }

                $scope.scrollTo = function(id) {
                    $location.hash(id)
                    $anchorScroll()
                }
            }
        ])

})()
