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
  }

]);

//Use this controller to show and hide large contents
onsControllers.controller('TabsController', ['$scope', function($scope) {
    $scope.tab = 1
    $scope.changeTab = function(tabNumber) {
      $scope.tab = tabNumber
    }
  }
])


//Use this controller to show and hide large contents
onsControllers.controller('ContentRevealCtrl', ['$scope', function($scope) {
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
          if(index < 1) {
            result[index][1] = children[i + 1]
          }
          index++
        }
      }
      return result;
    }
  }

]);