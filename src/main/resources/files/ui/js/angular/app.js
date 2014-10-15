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
<<<<<<< HEAD
    when('/home/methodology', {
      templateUrl: 'templates/methodology.html',
      controller: 'methodology'
=======
    when('/release', {
      templateUrl: 'templates/release.html'
>>>>>>> 99c5e3aaa7e9d38b54d3e8f97ea1a49906d9a2b2
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
