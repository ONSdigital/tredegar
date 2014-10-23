'use strict';

angular.module('onsComponents')
  .directive('onsNav', ['$location',
    function($location) {
      return {
        restrict: 'E',
        scope: {
          navigationData: '='
        },
        link: function(scope) {
          var path = $location.$$path
          var tokens = path.split('/')
          if (tokens[1] === 'home') {
            if (tokens.length < 3) {
              scope.location = "home"
            } else {
              scope.location = tokens[2]
            }
          }
          scope.isCurrentPage = function(page) {
            return scope.location === page
          }

          scope.toggle = function(page) {
            scope.expandedPage = page
          }

          scope.isExpanded = function(page) {
            return scope.expandedPage === page
          }
        },
        templateUrl: 'app/components/navigation/navigation.html'
      }
    }
  ])