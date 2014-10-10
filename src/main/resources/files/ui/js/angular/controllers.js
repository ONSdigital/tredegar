'use strict';

/* Controllers */

var onsControllers = angular.module('onsControllers', []);

onsControllers.controller("MainCtrl", function ($scope) {

});


onsControllers.controller('NavigationCtrl', ['$scope',
  function($scope) {
    // $scope.phones = Phone.query();
    // $scope.orderProp = 'age';
  }
]);


onsControllers.controller('TemplateCtrl', ['$scope', '$http', '$location', 
  function($scope, $http, $location) {
    $http.get($location.$$path + "?data").success(function(data) {
         console.log($location)
         $scope.data = data;
         $scope.path = $location.$$path;
     });
  }
]);


onsControllers.controller('ChildCtrl', ['$scope', '$http', '$location', 
  function($scope, $http, $location) {
  $scope.getChild = function(fileName) {
          var url = $scope.path + "/" + fileName + "?data";
          console.log(url);
          var childData = $http.get(url).success(function(child) {
            return child;
          });
        console.log(childData)
        return childData;
      }
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