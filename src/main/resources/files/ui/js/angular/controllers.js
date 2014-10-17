'use strict';

/* Controllers */

var onsControllers = angular.module('onsControllers', []);


// Main controller that applies to all the pages
onsControllers.controller('MainCtrl', ['$scope', '$http', '$location', '$route', 'anchorSmoothScroll',
  function($scope, $http, $location, $route, anchorSmoothScroll) {
    $scope.getData = function(path, callback) {
      console.log("Loading data at " + path)
      $http.get(path).success(callback)
    }

    $scope.getUrlParam = function(paramName) {
      var params = $location.search()
      return params[paramName]
    }

    $scope.setUrlParam = function(paramName, value) {
      if (value) {
        $location.search(paramName, value)
      } else {
        $location.search(paramName)
      }
    }

    $scope.getPath = function() {
      return $location.$$path
    }

    $scope.getPage = function() {
      var path = $location.$$path
      var lastIndex = path.lastIndexOf('/')
      var parenPath = path.substring(lastIndex + 1, path.length)
      return parenPath
    }

    $scope.getParentPath = function() {
      var path = $location.$$path
      var lastIndex = path.lastIndexOf('/')
      var parenPath = path.substring(0, lastIndex)
      return parenPath

    }

    $scope.getParentOf = function(p) {
      var path = $location.$$path
      var lastIndex = path.lastIndexOf(p)
      var parenPath = path.substring(0, lastIndex -1 )
      return parenPath
    }





    $scope.scrollTo = function(id) {
      $location.hash(id)
      anchorSmoothScroll.scrollTo(id)
    }

    $scope.goToSearch = function(searchTerm) {
      if (!searchTerm) {
        return
      }
      $location.path('/searchresults')
      $location.search('q', searchTerm)
      //Re-initializes controllers. Fixes searching on search results page searching the same term
      $route.reload()

    }


    $scope.goToPage = function(page) {
      if (!page) {
        return
      }
      $location.path(page)
    }

   $scope.search = function(q, type, pageNumber, callback) {
      var searchString = "?q=" + q + (type ? "&type=" + type : "") + "&page=" + pageNumber
      $scope.getData("/search" + searchString, function(data) {
        $scope.searchResponse = data
        $scope.pageCount = Math.ceil(data.numberOfResults / 10)
        callback(data)
      })
    }

    $scope.complete = function(searchTerm) {
      $scope.show=false
      if(searchTerm.length <3) {
        $scope.show=false
        return
      }
      $scope.search(searchTerm, '', 1, function(data){
        $scope.show = true
        $scope.count = data.numberOfResults
      })
    }

    $scope.hide=function() {
      $scope.show = false
    }



  }

]);

// Use this controller to show and hide tabs, see example in releasecalendarfooter.html
onsControllers.controller('TabsCtrl', ['$scope',
  function($scope) {
    $scope.activeTab = 1
    $scope.selectTab = function(tabNumber) {
      $scope.activeTab = tabNumber
    }
    $scope.isSelected = function(tabNumber) {
      return $scope.activeTab === tabNumber
    }
  }
])



//Article Controller
onsControllers.controller('ArticleCtrl', ['$scope',
  function($scope) {
    $scope.header = "Expert Analysis"
    $scope.contentType = "article"
    $scope.sidebar = true
    $scope.sidebarUrl = "templates/contentsidebar.html"
  }
])

//Methodology Controller
onsControllers.controller('MethodologyCtrl', ['$scope',
  function($scope) {
    $scope.header = "Methodology"
    $scope.contentType = "methodology"
    $scope.sidebar = false
  }
])

//Bulletin Controller
onsControllers.controller('BulletinCtrl', ['$scope',
  function($scope) {
    $scope.header = "Statistical Bulletin"
    $scope.contentType = "bulletin"
    $scope.sidebar = true
    $scope.sidebarUrl = "templates/contentsidebar.html"

    $scope.data = {
      breadcrumb: [
        {
          name: "Economy",
          fileName: "economy"
        },
        {
          name: "Inflation and Price Indices",
          fileName: "inflationandpriceindices"
        }
      ],
      name: "Statistical Bulletin",
      fileName: "bulletin"
    }

  }
])


//Collection Controller
onsControllers.controller('CollectionCtrl', ['$scope',
  function($scope) {

    $scope.getData("/collection.json", function(data) {
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
      $scope.getData("/collectiontaxonomyfilesystem" + searchString, function(data) {
        $scope.searchResponse = data
        console.log(data)
        $scope.pageCount = Math.ceil(data.numberOfResults / 10)
      })
    }


    $scope.isLoading = function() {
      return ($scope.searchTerm && !$scope.searchResponse)
    }

  }
])

//Search ctrl, used for search results page
onsControllers.controller('SearchCtrl', ['$scope',
  function($scope) {
    var getParam = $scope.getUrlParam
    var page = getParam('page')
    var searchTerm = $scope.searchTerm = getParam('q')
    var type = $scope.type = getParam('type')

    if (!searchTerm) {
      return
    }

    if (!page) {
      page = 1
    }

    console.log("page is " + page)

    $scope.search(searchTerm, type, page, function(data) {})

    $scope.isLoading = function() {
      return ($scope.searchTerm && !$scope.searchResponse)
    }

  }
])

onsControllers.controller('AutocompleteCtrl', ['$scope',
  function($scope) {
    
  }
])



//Paginator
onsControllers.controller('PaginatorCtrl', ['$scope',
  function($scope) {
    var page = $scope.getUrlParam('page')
    $scope.currentPage = page ? (+page) : 1

    $scope.getStart = function() {
      if ($scope.pageCount <= 10) {
        return 1
      }

      var end = $scope.getEnd()
      var start = end - 9
      return start > 0 ? start : 1
    }

    $scope.getEnd = function() {
      var max = $scope.pageCount
      if ($scope.pageCount <= 10) {
        return max
      }

      //Five page links after current page
      var end = $scope.curentPage + 5
      return end > max ? max : end
    }

    $scope.isVisible = function() {
      return ($scope.pageCount > 1)
    }

    $scope.isPreviousVisible = function() {
      return ($scope.currentPage != 1)
    }

    $scope.selectPage = function(index) {
      var page = $scope.currentPage = (index)
      $scope.setUrlParam('page', page)
    }

    $scope.goToNext = function() {
      var page = $scope.currentPage += 1
      $scope.setUrlParam('page', page)
    }

    $scope.goToPrev = function() {
      var page = $scope.currentPage -= 1
      $scope.setUrlParam('page', page)
    }

    $scope.isCurrentPage = function(index) {
      return $scope.currentPage === (index)
    }

    $scope.isNextVisible = function() {
      return ($scope.currentPage != $scope.pageCount)
    }

    $scope.getClass = function(index) {
      return $scope.currentPage === (index) ? 'active' : ''
    }

  }
])

//Use this controller to show and hide large contents
onsControllers.controller('ContentRevealCtrl', ['$scope',
  function($scope) {
    $scope.showContent = false
    $scope.toggleContent = function() {
      $scope.showContent = !$scope.showContent
    }
  }
])
onsControllers.controller('TemplateCtrl', ['$scope',
  function($scope) {
    $scope.getDataPath = function() {
      var path = $scope.getPath()
      path = "/data" + path.substring(5)
      return path
    }

    $scope.getData($scope.getDataPath(), function(data) {
      $scope.data = data
      if (data.level === 't1') {
        $scope.styleclass = 'footer__license'
      }

      console.log('TemplateCtrl: ' + data)
    })
  }
])


onsControllers.controller('T1Ctrl', ['$scope', 'Page',
  function($scope, Page) {
    Page.setTitle('Home')
    var data = $scope.data
    var children = data.children
    var i
    var child

    for (i = 0; i < children.length; i++) {
      child = children[i]
      //Load level 2 data
      loadChildren(child)
    }

    data.children = convert(data.children)

    console.log(data)

    function loadChildren(child) {
      var level2Path = $scope.getDataPath() + "/" + child.fileName
      var j
      var grandChildren

      $scope.getData(level2Path, function(childData) {
        child.data = childData
        //Load level 3 data for numbers
        // grandChildren = childData.children
        // for (j = 0; j < grandChildren.length && j < 4 ; j++) {
        //   grandChild = grandChildren[j]
        //   // loadGrandChildren(child, grandChild)
        // }
      })
    }

    function loadGrandChildren(child, grandChild) {
      var level3Path = child.fileName + "/" + grandChild.fileName
      $scope.getData(level3Path, function(grandChildData) {
        grandChild.data = grandChildData
      })
    }

    //Converts list into two column rows for easy handling on view
    function convert(children) {
      var result = []
      var index = 0
      var mod

      for (var i = 0; i < children.length; i++) {
        //Load full child data and replace with current child
        mod = i % 2
        if (mod === 0) {
          result[index] = [];
          result[index][0] = children[i]
          result[index][1] = children[i + 1]
          index++
        }
      }
      return result;
    }
  }

]);


onsControllers.controller('T2Ctrl', ['$scope', 'Page',
  function($scope, Page) {

    Page.setTitle('Home')

    var data = $scope.data
    var children = data.children
    var i
    var child

    for (i = 0; i < children.length; i++) {
      child = children[i]
      //Load level 2 data
      loadChildren(child)
    }

    data.thirdChild = children[2]
    data.highlightedChildren = convert(data.children)

    console.log(data)

    function loadChildren(child) {
      var level2Path = $scope.getDataPath() + "/" + child.fileName
      var j
      var grandChildren

      $scope.getData(level2Path, function(childData) {
        child.data = childData
      })
    }

    //Converts list into two column rows for easy handling on view
    function convert(children) {
      var result = []
      var index = 0
      for (var i = 0; i < children.length && i < 4; i++) {
        //Load full child data and replace with current child
        var mod = i % 2
        if (mod === 0) {
          result[index] = [];
          result[index][0] = children[i]
          if (index < 1) {
            result[index][1] = children[i + 1]
          }
          index++
        }
      }
      return result;
    }
  }
]);

onsControllers.controller('ContentCtrl', ['$scope', '$location',
  function($scope, $location) {
    $scope.getData("/" + $scope.getPage() + ".json", function(data) {
      $scope.content = data
    })

    $scope.scroll = function() {
      anchorSmoothScroll.scrollTo($location.hash())
    }

  }
]);



onsControllers.controller('NavCtrl', ['$scope',
  function($scope) {
    var path = $scope.getPath()
    var tokens = path.split('/')
    if (tokens[1] === 'home') {
      if (tokens.length < 3) {
        $scope.location = "home"
      } else {
        $scope.location = tokens[2]
      }
    }

    $scope.getData("/navigation", function(data) {
      $scope.navigation = data
    })

    $scope.isCurrentPage = function(page) {
      return $scope.location === page
    }

    $scope.toggle = function(page) {
      $scope.expandedPage = page
    }

    $scope.isExpanded = function(page) {
      return $scope.expandedPage === page
    }
  }
])

onsControllers.controller('ReleaseCtrl', ['$scope',
  function($scope) {
    $scope.getData("/release.json", function(data) {
      $scope.releaseData = data
    })
  }
])