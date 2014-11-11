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
    .directive('onsPaginator', ['$location', PaginatorDirective])
    .filter('range', rangeFilter)

  function PaginatorDirective($location) {
    return {
      restrict: 'E',
      templateUrl: 'app/components/paginator/paginator.html',
      scope: {
        pageCount: '@',
        maxVisible: '@'
      },
      controller: PaginatorController,
      controllerAs: 'paginator'
    }

    function PaginatorController($scope) {
      var PAGE_PARAM = 'page'
      var paginator = this
      var maxVisible = $scope.maxVisible || 10
      var currentPage = +$location.search()[PAGE_PARAM] || 1
      var pageCount = $scope.pageCount
      var show = paginator.show = pageCount > 1

      function getStart() {
        if (pageCount <= maxVisible) {
          return 1
        }
        var end = getEnd()
        var start = end - maxVisible + 1
        start = start > 0 ? start : 1
        return start
      }

      function getEnd() {
        var max = pageCount
        if (max <= maxVisible) {
          return max
        }

        //Half of the pages are visible after current page
        var end = currentPage + Math.ceil(maxVisible / 2)
        end = (end > max) ? max : end
        end = (end < maxVisible) ? maxVisible : end
        return end
      }

      function selectPage(index) {
        currentPage = (index)
        $location.search(PAGE_PARAM, index)
      }

      function next() {
        var page = currentPage += 1
        $location.search(PAGE_PARAM, page) //Set page
      }

      function prev() {
        var page = currentPage -= 1
        $location.search(PAGE_PARAM, page) //Set page
      }

      function isCurrent(index) {
        return currentPage === (index)
      }

      function isNextVisible() {
        return (currentPage != pageCount)
      }

      function isPrevVisible() {
        return (currentPage != 1)
      }


      //Expose functions
      angular.extend(paginator, {
        isPrevVisible: isPrevVisible,
        isNextVisible: isNextVisible,
        isCurrent: isCurrent,
        prev: prev,
        next: next,
        selectPage: selectPage,
        getStart: getStart,
        getEnd: getEnd
      })

    }

  }

  //Range filter returns an array of numbers starting from given start and ending with given end
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