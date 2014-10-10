'use strict';

/* Controllers */

var onsControllers = angular.module('onsControllers', []);

onsControllers.controller('MainCtrl', ['$scope', '$http',
  function($scope, $http) {
    $scope.loadData = function(url) {
      $http.get(url + "?data").success(function(data) {
        console.log(data)
        $scope.data = data
      })
    }
  }
]);


onsControllers.controller('TemplateCtrl', ['$scope', '$http', '$location',
  function($scope, $http, $location) {
    $scope.loadData($location.$$path)
  }
]);


onsControllers.controller('T1Ctrl', ['$scope', '$http', '$location',
  function($scope, $http, $location) {
    //Convert children into a two dimensional array for eash handling on view
    var children = $scope.data.children
    var tempChildren = []
    console.log("Children Before")
    console.log(children)
    for (var i = 0; i < children.length; i++) {
      var mod = i % 2
      if (mod == 0) {
        tempChildren[i] = [];    
        tempChildren[i][0] = children[i]
        tempChildren[i][1] = children[i + 1]
      }
    }
    $scope.data.children = tempChildren
    console.log("Children After")
    console.log($scope.data.children)


  }

]);

onsControllers.controller('FooterCtrl', ['$scope',
  function($scope) {
    /*$scope.phone = Phone.get({
      phoneId: $routeParams.phoneId
    }, function(phone) {
      $scope.mainImageUrl = phone.images[0];
    });

    $scope.setImage = function(imageUrl) {
      $scope.mainImageUrl = imageUrl;
    }*/
  }
]);