(function() {


  //Collection Controller
  angular.module('onsTemplates')
    .controller('CollectionCtrl', ['$scope', '$location', '$log', 'DataLoader', 'PageUtil', CollectionController])

  function CollectionController($scope, $location, $log, DataLoader, PageUtil) {
    $log.debug('CollectionCtrl invoked')

    var url = $location.$$path
    var lastIndex = url.lastIndexOf('/');
    var searchTerm = url.substr(0, lastIndex)
    var page = PageUtil.getUrlParam('page')
    page = page || 1
    var contentType = PageUtil.getUrlParam('contentType')
    if (!searchTerm || !contentType) {
      return
    }
    $scope.contentType = resolveContentType(contentType)

    searchCollection(searchTerm, contentType, page)
    loadContent(searchTerm)

    //loads t3 data related
    function loadContent(searchTerm) {
      DataLoader.load('/data' + searchTerm)
        .then(function(data) {
          $scope.content = data
        })
    }

    function searchCollection(loc, type, pageNumber) {
      $log.debug('Searching for collections from: ' + searchTerm)
      var collectionSearchString = "?loc=" + loc + (contentType ? "&contentType=" + contentType : "") + "&page=" + pageNumber
      DataLoader.load("/collectiontaxonomyfilesystem" + collectionSearchString).then(function(data) {
        $scope.searchResponse = data
        $scope.pageCount = Math.ceil(data.numberOfResults / 10)
      })
    }

    function resolveContentType(contentType) {
      switch (contentType) {
        case 'bulletins':
          return 'Statistical bulletins'
        default:
          //Cpitilize first letter
          return contentType.substr(0,1).toUpperCase() + contentType.substr(1,contentType.length -1)
      }
    }


    $scope.isLoading = function() {
      return ($scope.searchTerm && !$scope.searchResponse)
    }

  }


})()