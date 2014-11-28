//Main ons application code
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
        'onsTooltip',
        'onsAutocomplete',
        'onsDoubleTap'
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
        'ipCookie',
        'onsComponents',
        'onsHelpers',
        'onsTemplates',
        'onsAppConfig'
    ])

    onsApp
        .run(['$rootScope', 'DataLoader', loadNavigation])
        .controller('MainCtrl', ['$scope', 'PageUtil', '$location', '$anchorScroll', MainController])

    function MainController($scope, PageUtil, $location, $anchorScroll) {
        var ctrl = this

        $scope.scrollTo = function(id) {
            $location.hash(id)
            $anchorScroll()
            $location.hash(null)
        }

        $scope.getPath = function() {
            return PageUtil.getPath()
        }

        $scope.getPage = function() {
            return PageUtil.getPage()
        }
    }

    function loadNavigation($rootScope, DataLoader) {
        DataLoader.load('/navigation')
            .then(function(data) {
                $rootScope.onsNavigation = data
            })
    }


})()