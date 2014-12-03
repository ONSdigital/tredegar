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
        .run(['$rootScope', 'DataLoader', 'PageUtil', run])
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

        $scope.getUrl = function() {
            return PageUtil.getUrl()
        }

        $scope.getTitle = function() {
            return PageUtil.getTitle()
        }
    }

    function run($rootScope, DataLoader, PageUtil, $window) {
        redirectCheck(PageUtil, $window)
        loadNavigation($rootScope, DataLoader)
    }


    //Check if url is properlu hasbanged (#!), or is a facebook reference
    //If page is static on a js enabled browser, redirect to js enabled site
    function redirectCheck(PageUtil, $window) {
        var absoluteLocation = PageUtil.getAbsoluteUrl();
        //This makes FB share work (socialLinks directive)
        if (absoluteLocation.indexOf('?onsfb') != -1) {
            absoluteLocation = absoluteLocation.replace('?onsfb', '#!')
            $window.location.href = absoluteLocation;
        }
        //  else if (absoluteLocation.indexOf('/static') != -1) {
        //     console.log("Redirecting to javascript site")
        //     absoluteLocation = absoluteLocation.replace('/static', '#!')
        //     $window.location.href = absoluteLocation;
        // }
         else if (absoluteLocation.indexOf('#!/') === -1) {
            PageUtil.goToPage('/');
        }
    }


    function loadNavigation($rootScope, DataLoader) {
        DataLoader.load('/navigation')
            .then(function(data) {
                $rootScope.onsNavigation = data
            })
    }


})()