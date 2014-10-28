
/*
Paginator component is an element directive. 
It simply adds "page=<currentPage>" to the URL as get parameter. Does not load remote data or hide any DOM components. 
Remote server should return appropriate data with page parameter given.
Paginator will not render any data. Data should be rendered by some other mechanism (controller).

Default max visible link number is 10, pass max-visible to change visible number of links.

pageCount field is mandatory

@author Bren
 */

(function() {

'use strict';

angular.module('onsPaginator', [])
  .directive('onsPaginator', [ '$location', PaginatorDirective])
  .filter('range',rangeFilter )

  function PaginatorDirective($location) {
      return {
        restrict: 'E',
        templateUrl: 'app/components/paginator/paginator.html',
        scope: {
          pageCount: '@',
          maxVisible: '@'
        },
        link: function(scope, element, attrs) {

          //TODO: Cleanup
          
          var pageParam = 'page'
          var maxVisible = scope.maxVisible = scope.maxVisible || 10
          scope.currentPage = +$location.search()[pageParam] || 1 
            
          scope.getStart = function() {
            if (scope.pageCount <= maxVisible) {
              return 1
            }
            var end = scope.getEnd()
            var start = end - maxVisible + 1
            start =  start > 0 ? start : 1
            return start
          }
          scope.getEnd = function() {
            var max = scope.pageCount
            if (max <= maxVisible) {
              return max
            }

            //Half of the pages are visible after current page
            var end = scope.currentPage + Math.ceil(maxVisible / 2)
            end = (end > max) ? max : end
            end = (end < maxVisible) ? maxVisible : end
            return end
          }
          scope.isVisible = function() {
            return (scope.pageCount > 1)
          }
          scope.isPreviousVisible = function() {
            return (scope.currentPage != 1)
          }
          scope.selectPage = function(index) {
            var page = scope.currentPage = (index)
            $location.search(pageParam, page)
          }
          scope.goToNext = function() {
            var page = scope.currentPage += 1
            $location.search(pageParam, page)
          }
          scope.goToPrev = function() {
            var page = scope.currentPage -= 1
            $location.search(pageParam, page)
          }
          scope.isCurrentPage = function(index) {
            return scope.currentPage === (index)
          }
          scope.isNextVisible = function() {
            return (scope.currentPage != scope.pageCount)
          }
          scope.getClass = function(index) {
            return scope.currentPage === (index) ? 'active' : ''
          }

        }

      }
    }

    function rangeFilter() {
    return function(input, start, end) {
      var start = parseInt(start)
      var end = parseInt(end)
      for (var i = start; i <= end; i++)
        input.push(i)
      return input
    }
  }

})()