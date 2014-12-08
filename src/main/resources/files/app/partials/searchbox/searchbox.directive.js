'use strict';

angular.module('onsTemplates')
	.directive('searchBox', ['$location', '$route', 'PageUtil', 'StringUtil',
		function($location, $route, PageUtil, StringUtil) {
			return {
				restrict: 'E',
				replace: true,
				templateUrl: 'app/partials/searchbox/searchbox.html',
				controller: ['$scope', '$rootScope', SearchBox]
			}

			function SearchBox($scope, $rootScope ) {
				
				$scope.autoCompleteLimit = $rootScope.onsAlphaConfiguration.autoCompleteLimit

				clearOnPageChange()

				function clearOnPageChange() {
				  $rootScope.$on('$locationChangeSuccess', function(event) {
				  	//Clear if not going to search results page
				  	if(!StringUtil.startsWith($location.path(),"/search")) {
				    	$scope.searchTerm = ''
				  	}
				  })
				}

				$scope.goToSearch = function(searchTerm) {
					if (!searchTerm) {
						return
					}
					var currentPath = $location.url()
					if(currentPath === ('/search/' +searchTerm)) {
						// Searching the same term when on search results page will not change the url. 
						// That means search is not triggered.Reload ensure search is performed again
						$route.reload()						
					} else {
						$location.path('/search/' + searchTerm)
						$location.search('type', null)
					}

				}

				$scope.getStaticFormAction = function() {
					var staticSearchForm = '/static/search'
					return PageUtil.isPrerender() ? staticSearchForm : ''
				}
			}
		}
	])