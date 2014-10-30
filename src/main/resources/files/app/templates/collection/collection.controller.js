//Collection Controller
angular.module('onsTemplates')
  .controller('CollectionCtrl', ['$scope', '$location', '$http',
    function($scope, $location, $http) {
	  console.log('CollectionCtrl invoked')
	  
      getData("/collection.json", function(data) {
        $scope.content = data
      })
	  var url = $location.$$path
	  var lastIndex = url.lastIndexOf('/');
	  var searchTerm = url.substr(0, lastIndex)
	  console.log('Searching for collections from: ' + searchTerm)

      var getParam = $scope.getUrlParam
      var page = getParam('page')
      var contentType = getParam('contentType')
      if (!searchTerm) {
        return
      }
      if (!page) {
        page = 1
      }

      searchCollection(searchTerm, contentType, page)

      function searchCollection(loc, type, pageNumber) {
        var collectionSearchString = "?loc=" + loc + (contentType ? "&contentType=" + contentType : "") + "&page=" + pageNumber
        getData("/collectiontaxonomyfilesystem" + collectionSearchString, function(data) {
          $scope.searchResponse = data
          console.log(data)
          $scope.pageCount = Math.ceil(data.numberOfResults / 10)
        })
      }

      function getData(path, callback) {
           console.log("Loading data at " + path)
           $http.get(path).success(callback)
      }



      $scope.isLoading = function() {
        return ($scope.searchTerm && !$scope.searchResponse)
      }

    }
  ])