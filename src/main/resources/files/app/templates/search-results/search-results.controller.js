//Search ctrl, used for search results page
'use strict';

angular.module('onsTemplates')
  .controller('SearchCtrl', ['$scope', '$location', '$http',
    function($scope, $location, $http) {
      var page = getUrlParam('page')
      var searchTerm = $scope.searchTerm = getUrlParam('q')
      var type = $scope.type = getUrlParam('type')

      if (!searchTerm) {
        return
      }

      if (!page) {
        page = 1
      }

      search(searchTerm, type, page, function(data) {
        $scope.searchTermOneTime = searchTerm
      })

      $scope.isLoading = function() {
        return ($scope.searchTerm && !$scope.searchResponse)
      }

      function search(q, type, pageNumber, callback) {
        var searchString = "?q=" + q + (type ? "&type=" + type : "") + "&page=" + pageNumber
        getData("/search" + searchString, function(data) {
          $scope.searchResponse = data
          $scope.pageCount = Math.ceil(data.numberOfResults / 10)
          callback(data)
        })
      }

      function getData(path, callback) {
        console.log("Loading data at " + path)
        $http.get(path).success(callback)
      }

      function getUrlParam(paramName) {
        var params = $location.search()
        return params[paramName]
      }


    }
  ])