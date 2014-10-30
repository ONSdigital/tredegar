// Taxonomy base, data is injected. Data is loaded throug route service
(function() {
	'use strict';
	angular.module('onsTemplates')
		.controller('NavigationController', ['$scope',
			NavigationController
		])

	function NavigationController($scope) {
		var ctrl = this
		ctrl.hideSearch=true

		function toggleSearch() {
			ctrl.hideSearch = !ctrl.hideSearch
		}

		angular.extend(ctrl,{
			toggleSearch:toggleSearch
		})

	}
})()