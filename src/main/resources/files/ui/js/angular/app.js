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


/*Custom Directives*/
onsApp.directive('onsFooter', function() {
  return {
    restrict : 'E',
    templateUrl : 'templates/footer.html'
  } 

})

onsApp.directive('onsHeader', function() {
  return {
    restrict : 'E',
    templateUrl : 'templates/header.html'
  } 

})

onsApp.directive('onsHomeHeader', function() {
  return {
    restrict : 'E',
    templateUrl : 'templates/homeheader.html'
  } 

})

onsApp.directive('paginator', function() {
  return {
    restrict : 'E',
    templateUrl : 'templates/paginator.html'
  } 
})



onsApp.factory('Page', function() {
  var title = 'default';
  return {
    title: function() {
      return title;
    },
    setTitle: function(newTitle) {
      title = newTitle
    }
  };
});
