// Taxonomy base, data is injected. Data is loaded throug route service
(function() {
	'use strict';
	angular.module('onsTemplates')
		.controller('NavigationController', ['$scope', '$route',
			NavigationController
		])

	function NavigationController($scope, $route) {
		var navigation = this
		navigation.hideSearch = false
		if ($route.current.locals) {
			navigation.links = $route.current.locals.navigation
		}

		watchMenu()

		function toggleSearch() {
			navigation.hideSearch = !navigation.hideSearch
			if (!navigation.hideSearch) {
				hideMenu()
			}
		}

		function isMobileMenuVisible() {
			if ($scope.w_nav) {
				return $scope.w_nav.showOnMobile
			}
			return false

		}

		function watchMenu() {
			$scope.$watch('w_nav.showOnMobile', function() {
				if (isMobileMenuVisible()) {
					navigation.hideSearch = true
				}
			})
		}

		function hideMenu() {
			$scope.w_nav.showOnMobile = false
		}
		angular.extend(navigation, {
			toggleSearch: toggleSearch
		})

	}
})()