//Collection Controller
angular.module('onsTemplates')
  .controller('CollectionCtrl', ['$scope', '$http',
    function($scope, $http) {

      getData("/collection.json", function(data) {
        $scope.content = data
      })

      var getParam = $scope.getUrlParam
      var page = getParam('page')
      var searchTerm = getParam('loc')
      var type = $scope.type = getParam('type')
      if (!searchTerm) {
        return
      }
      if (!page) {
        page = 1
      }

      search(searchTerm, type, page)

      function search(loc, type, pageNumber) {
        var searchString = "?loc=" + loc + (type ? "&type=" + type : "") + "&page=" + pageNumber
        getData("/collectiontaxonomyfilesystem" + searchString, function(data) {
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