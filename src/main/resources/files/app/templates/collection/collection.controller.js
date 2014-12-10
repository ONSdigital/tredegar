(function() {


  //Collection Controller
  angular.module('onsTemplates')
    .controller('CollectionCtrl', ['$location', '$log', 'DataLoader', 'PageUtil', CollectionController])

  function CollectionController($location, $log, DataLoader, PageUtil) {
    $log.debug('CollectionCtrl invoked')

    var collection = this
    var url = $location.$$path
    var lastIndex = url.lastIndexOf('/');
    var searchTerm = url.substr(0, lastIndex)
    var page = PageUtil.getUrlParam('page')
    page = page || 1
    var contentType = PageUtil.getUrlParam('contentType')
    if (!searchTerm || !contentType) {
      return
    }
    collection.contentType = resolveContentType(contentType)

    searchCollection(searchTerm, contentType, page)
    loadContent(searchTerm)

    //loads t3 data related
    function loadContent(searchTerm) {
      DataLoader.load('/data' + searchTerm)
        .then(function(data) {
          collection.content = data
        })
    }

    function searchCollection(loc, type, pageNumber) {
      $log.debug('Searching for collections from: ' + searchTerm)
      var collectionSearchString = "?loc=" + loc + (contentType ? "&contentType=" + contentType : "") + "&page=" + pageNumber
      DataLoader.load("/collectiontaxonomyfilesystem" + collectionSearchString).then(function(data) {
        collection.searchResponse = data
        collection.pageCount = Math.ceil(data.numberOfResults / 10)
      })
    }

    function resolveContentType(contentType) {
      switch (contentType) {
        case 'bulletins':
          return 'statistical bulletins'
        default:
          return contentType
      }
    }

     function isLoading() {
      return (collection.searchTerm && collection.searchResponse)
    }

    angular.extend(collection, {
      isLoading:isLoading
    })

  }


})();