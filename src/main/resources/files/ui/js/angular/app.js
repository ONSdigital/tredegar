//Angular code

'use strict';

/* App Module */

var onsApp = angular.module('onsApp', [
    'ngRoute',
    'onsControllers'
]);

onsApp.config(['$routeProvider','$locationProvider',
    function($routeProvider, $locationProvider) {
		//$locationProvider.hashPrefix('!');
//			 .html5Mode(false)
        $routeProvider.
        when('/', {
            redirectTo: '/home'
        }).
        when('/searchresults', {
            templateUrl: 'templates/searchresults.html',
            controller: 'SearchCtrl'
        }).
        when('/methodology', {
            templateUrl: 'templates/methodology.html',
            controller: 'MethodologyCtrl'
        }).
        when('/article', {
            templateUrl: 'templates/article.html',
            controller: 'ArticleCtrl'
        }).
        when('/home/economy/inflationandpriceindices/bulletin', {
            templateUrl: 'templates/bulletin.html',
            controller: 'BulletinCtrl'
        }).
        when('/bulletin', {
            redirectTo: '/home/economy/inflationandpriceindices/bulletin'
        }).
        when(':home*\/bulletins', {
            templateUrl: 'templates/bulletin.html',
            controller: 'BulletinCtrl'
        }).
        when('/release', {
            templateUrl: 'templates/release.html'
        }).
        when('/collection', {
            templateUrl: 'templates/collection.html',
            controller: "CollectionCtrl"
        }).
        otherwise({
            templateUrl: 'templates/template.html',
            controller: 'TemplateCtrl'
        });
    }
]);


/*Filters for ng-repeat*/

onsApp.filter('slice', function() {
    return function(arr, start, end) {
        return arr.slice(start, end);
    };
});

onsApp.filter('range', function() {
    return function(input, start, end) {
        var start = parseInt(start);
        var end = parseInt(end);
        for (var i = start; i <= end; i++)
            input.push(i);
        return input;
    };
});


/*Custom Directives*/
onsApp.directive('onsFooter', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/footer.html'
  }

})

onsApp.directive('releaseCalendarFooter', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/releasecalendarfooter.html'
  }

})

onsApp.directive('onsHeader', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/header.html'

  }
})

onsApp.directive('paginator', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/paginator.html'
  }
})

onsApp.directive('onsNav', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/onsnav.html'
  }
})

onsApp.directive('onsBreadcrumb', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/breadcrumb.html'
  }
})

onsApp.directive('searchBox', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/searchbox.html'
  }
})

onsApp.directive('autoComplete', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/autocomplete.html',
    controller: 'AutocompleteCtrl'
  }
})

onsApp.directive('topBar', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/top.html'
  }
})

onsApp.directive('onsContent', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/content.html'
  }
})

onsApp.directive('relatedArticles', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/relatedarticles.html'
  }
})

onsApp.directive('contactDetails', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/contactdetails.html'
  }
})

onsApp.directive('tools', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/tools.html'
  }
})

onsApp.directive('latestBulletins', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/latestbulletins.html'
  }
})

onsApp.directive('stopEvent', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attr) {
            element.bind('click', function (e) {
              e.stopPropagation();
            });
        }
    };
});

onsApp.directive('markdown', function($http) {
    var converter = new Showdown.converter();
    return {
        restrict: 'A',
        scope: {
            link: '@'
        },
        link: function(scope, element, attrs) {
            attrs.$observe('link', function(link) {
                var htmlText = converter.makeHtml(link);
                element.html(htmlText);
            });
        }
    };
});

onsApp.factory('Page', function() {
    var title = 'Office Of National Statistics';
    return {
        title: function() {
            return title;
        },
        setTitle: function(newTitle) {
            title = newTitle
        }
    };
});

onsApp.service('anchorSmoothScroll', function(){

    this.scrollTo = function(eID) {

        // This scrolling function
        // is from http://www.itnewb.com/tutorial/Creating-the-Smooth-Scroll-Effect-with-JavaScript

        var startY = currentYPosition();
        var stopY = elmYPosition(eID);
        var distance = stopY > startY ? stopY - startY : startY - stopY;
        if (distance < 100) {
            scrollTo(0, stopY); return;
        }
        var speed = Math.round(distance / 100);
        if (speed >= 20) speed = 20;
        var step = Math.round(distance / 25);
        var leapY = stopY > startY ? startY + step : startY - step;
        var timer = 0;
        if (stopY > startY) {
            for ( var i=startY; i<stopY; i+=step ) {
                setTimeout("window.scrollTo(0, "+leapY+")", timer * speed);
                leapY += step; if (leapY > stopY) leapY = stopY; timer++;
            } return;
        }
        for ( var i=startY; i>stopY; i-=step ) {
            setTimeout("window.scrollTo(0, "+leapY+")", timer * speed);
            leapY -= step; if (leapY < stopY) leapY = stopY; timer++;
        }

        function currentYPosition() {
            // Firefox, Chrome, Opera, Safari
            if (self.pageYOffset) return self.pageYOffset;
            // Internet Explorer 6 - standards mode
            if (document.documentElement && document.documentElement.scrollTop)
                return document.documentElement.scrollTop;
            // Internet Explorer 6, 7 and 8
            if (document.body.scrollTop) return document.body.scrollTop;
            return 0;
        }

        function elmYPosition(eID) {
            var elm = document.getElementById(eID);
            var y = elm.offsetTop;
            var node = elm;
            while (node.offsetParent && node.offsetParent != document.body) {
                node = node.offsetParent;
                y += node.offsetTop;
            } return y;
        }

    }
});

