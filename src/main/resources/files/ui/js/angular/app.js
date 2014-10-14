//Angular code

'use strict';

/* App Module */

var onsApp = angular.module('onsApp', [
  'ngRoute',
  'onsControllers'
]);

onsApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
    when('/', {
      redirectTo: '/home'
    }).
    when('/searchresults', {
      templateUrl: 'templates/searchresults.html'
    }).
    when('/release', {
      templateUrl: 'templates/release.html'
    }).
    otherwise({
      templateUrl: 'templates/template.html'
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
    for (var i=start; i<=end; i++)
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

onsApp.directive('onsHeader', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/header.html'
  }

})

onsApp.directive('onsHomeHeader', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/homeheader.html'
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

onsApp.directive('breadcrumb', function() {
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

onsApp.directive('topBar', function() {
  return {
    restrict: 'E',
    templateUrl: 'templates/top.html'
  }
})


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