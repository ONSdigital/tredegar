// Taxonomy base, data is injected. Data is loaded throug route service
(function() {
	'use strict';
	angular.module('onsTemplates')
		.controller('NavigationController', ['$scope',
			NavigationController
		])

	function NavigationController($scope) {
		var ctrl = this
		ctrl.hideSearch=false
		watchMenu()

		function toggleSearch() {
			ctrl.hideSearch = !ctrl.hideSearch
			if(!ctrl.hideSearch) {
				hideMenu()
			}
		}

		function isMobileMenuVisible() {
			if($scope.w_nav) {
				return $scope.w_nav.showOnMobile	
			}
			return false
			
		}

		function watchMenu(){
			$scope.$watch('w_nav.showOnMobile', function(){
				if(isMobileMenuVisible()) {
					ctrl.hideSearch=true
				}
			})
		}

		function hideMenu() {
			$scope.w_nav.showOnMobile = false	
		}
		angular.extend(ctrl,{
			toggleSearch:toggleSearch
		})

	}
})()