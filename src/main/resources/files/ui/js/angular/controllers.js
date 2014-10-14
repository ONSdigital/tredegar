'use strict';

/* Controllers */

var onsControllers = angular.module('onsControllers', []);

onsControllers.controller('MainCtrl', ['$scope', '$http', '$location',
  function($scope, $http, $location) {
    $scope.loadData = function(path, callback) {
      path = (path) ? ("/" + path) : ""
      path = $location.$$path + path + "?data"
      console.log("Loadint data at " + path)
      $http.get(path).success(callback)
    }

    $scope.getJSON = function(fullPath, callback) {
      console.log("Loadint json at " + fullPath)
      $http.get(fullPath).success(callback)
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

    $scope.getRange = function(num) {
      return new Array(num);
    }
  }

]);

//Use this controller to show and hide large contents
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
    $scope.setUrlParam('page', page)

    search(searchTerm, type, page)

    function search(q, type, pageNumber) {
      var searchString = "?q=" + q + (type ? "&type=" + type : "") + "&page=" + pageNumber
      $scope.getJSON("/search" + searchString, function(data) {
        $scope.searchResponse = data
        $scope.pageCount = Math.ceil(data.numberOfResults / 10)
      })
    }

  }
])

onsControllers.controller('PaginatorCtrl', ['$scope',
  function($scope) {
    var page = $scope.getUrlParam('page')
    $scope.currentPage = page ? (+page) : 1
    $scope.isVisible = function() {
      return ($scope.pageCount > 1)
    }

    $scope.isPreviousVisible = function() {
      return ($scope.currentPage != 1)
    }

    $scope.selectPage = function(index) {
      var page = $scope.currentPage = (index + 1)
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
      return $scope.currentPage === (index + 1)
    }

    $scope.isNextVisible = function() {
      return ($scope.currentPage != $scope.pageCount)
    }

    $scope.getClass = function(index) {
      return $scope.currentPage === (index + 1) ? 'active' : ''
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

onsControllers.controller('TemplateCtrl', ['$scope', '$http', '$location',
  function($scope, $http, $location) {
    $scope.loadData('', function(data) {
      $scope.data = data
    })
  }
])

onsControllers.controller('T1Ctrl', ['$scope', '$http', '$location', 'Page',
  function($scope, $http, $location, Page) {

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
      var level2Path = child.fileName
      var j
      var grandChildren

      $scope.loadData(level2Path, function(childData) {
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
      $scope.loadData(level3Path, function(grandChildData) {
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



onsControllers.controller('T2Ctrl', ['$scope', '$http', '$location', 'Page',
  function($scope, $http, $location, Page) {

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
      var level2Path = child.fileName
      var j
      var grandChildren

      $scope.loadData(level2Path, function(childData) {
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