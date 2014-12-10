(function() {

	var onsTemplates = angular.module('onsTemplates')

	onsTemplates
		.directive('onsHeader',
			function() {
				return {
					restrict: 'E',
					templateUrl: 'app/partials/header/header.html'
				}
			}
		)

})();