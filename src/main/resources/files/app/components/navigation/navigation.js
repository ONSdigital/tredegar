(function() {
  'use strict';

  angular.module('onsNavigation', [])
    .directive('onsNav', ['$location',
      navigationDirective
    ])

  function navigationDirective($location) {
    return {
      restrict: 'E',
      scope: {
        navigationData: '='
      },
      link: function(scope) {
        var path = $location.$$path
        var tokens = path.split('/')
        if (tokens.length < 2) {
          scope.location = "/"
        } else {
          scope.location = tokens[1]
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
})()