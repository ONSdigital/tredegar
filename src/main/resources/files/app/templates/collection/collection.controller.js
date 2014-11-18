//Collection Controller
angular.module('onsTemplates')
  .controller('CollectionCtrl', ['$scope', '$location', '$log', 'DataLoader', 'PageUtil',
    function($scope, $location, $log, DataLoader, PageUtil) {
      $log.debug('CollectionCtrl invoked')

      DataLoader.load("/collection.json")
        .then(function(data) {
          $scope.content = data
        })
      var url = $location.$$path
      var lastIndex = url.lastIndexOf('/');
      var searchTerm = url.substr(0, lastIndex)
      $log.debug('Searching for collections from: ' + searchTerm)

      var page = PageUtil.getUrlParam('page')
      var contentType = PageUtil.getUrlParam('contentType')
      if (!searchTerm) {
        return
      }
      if (!page) {
        page = 1
      }

      searchCollection(searchTerm, contentType, page)

      function searchCollection(loc, type, pageNumber) {
        console.log("searching for "+ loc + " " + type + " " + pageNumber )
        var collectionSearchString = "?loc=" + loc + (contentType ? "&contentType=" + contentType : "") + "&page=" + pageNumber
        DataLoader.load("/collectiontaxonomyfilesystem" + collectionSearchString).then(function(data) {
          $scope.searchResponse = data
          $scope.pageCount = Math.ceil(data.numberOfResults / 10)
        })
      }



      $scope.isLoading = function() {
        return ($scope.searchTerm && !$scope.searchResponse)
      }

    }
  ])