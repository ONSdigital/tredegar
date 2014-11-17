'use strict';

angular.module('onsTemplates')
	.directive('errorSearchBox', ['$location', '$route',
		function($location, $route) {
			return {
				restrict: 'E',
				replace:true,
				templateUrl: 'app/partials/searchbox/searchbox.html',
				controller: function($scope, $window) {
					$scope.goToSearch = function(searchTerm) {
						if (!searchTerm) {
							return
						}
						$location.path('/search')
						$location.search('q', searchTerm)

						//Clear page parameter if any
						$location.search('page', null)
						//Re-initializes controllers. Fixes searching on search results page when searching the same term
						var searchUrl = $location.$$protocol + '://' + $location.$$host + ':' + $location.$$port + '/#!' + $location.$$path + '?q=' + searchTerm 
						$window.location.href = searchUrl
					}
				}
			}
		}
	])