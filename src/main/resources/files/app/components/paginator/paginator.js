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
    .directive('onsPaginator', ['$location', '$routeParams', Paginator])
    .filter('range', rangeFilter)

  function Paginator($location, $routeParams) {
    return {
      restrict: 'E',
      templateUrl: 'app/components/paginator/paginator.html',
      scope: {
        pageCount: '=',
        maxVisible: '=',
        //if set to true generates static links, to be used with prerender
        static: '=?' 
      },
      controller: PaginatorController,
      controllerAs: 'paginator'
    }

    function PaginatorController($scope) {
      var paginator = this
      var PAGE_PARAM = 'page'
      var maxVisible = $scope.maxVisible || 10
      var currentPage = $scope.currentPage = +$routeParams[PAGE_PARAM] || 1
      paginator.show
      paginator.start
      paginator.end
      init()
      watchPageCount()
      watchMaxVisible()

      function init() {
        paginator.start = getStart()
        paginator.end = getEnd()
        paginator.show = $scope.pageCount > 1
      }

      function watchPageCount() {
        $scope.$watch('pageCount', function() {
            init()
        })
      }

      function watchMaxVisible() {
        $scope.$watch('maxVisible', function(newValue) {
            maxVisible = newValue
            init()
        })
      }


      function getStart() {
        if ($scope.pageCount <= maxVisible) {
          return 1
        }
        var end = getEnd()
        var start = end - maxVisible + 1
        start = start > 0 ? start : 1
        return start
      }

      function getEnd() {
        var max = $scope.pageCount
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
        gotoPage(index)
        init()
      }

      function next() {
        gotoPage(currentPage + 1)
        init()
      }

      function prev() {
        gotoPage(currentPage - 1)
        init()
      }

      function getLinkTarget (index) {
        var target = ''
        target =  $scope.static ? ('/static' + getPath(index)) : ''
        return target
      }

      function gotoPage(page) {
        $location.path(getPath(page))
        currentPage = page
      }

      function getPath(page) {
        var path = $location.path()
        var newPath = ''
        console.log('Path:' + path + ' currentPage:' + currentPage)
        if(path.indexOf('/' + PAGE_PARAM + '/' + currentPage) > -1) {
          console.log("Page available")
          newPath =  path.replace(PAGE_PARAM + '/' + currentPage, PAGE_PARAM + '/' + page)
        } else {
          newPath =  path + '/'  + PAGE_PARAM + '/' + page
        }
        console.log("New path:" + newPath)
        return newPath
      }

      function isCurrent(index) {
        return currentPage === (index)
      }

      function isNextVisible() {
        return (currentPage < $scope.pageCount)
      }

      function isPrevVisible() {
        return (currentPage > 1)
      }


      //Expose functions
      angular.extend(paginator, {
        isPrevVisible: isPrevVisible,
        isNextVisible: isNextVisible,
        isCurrent: isCurrent,
        prev: prev,
        next: next,
        selectPage: selectPage,
        getLinkTarget:getLinkTarget
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

})();