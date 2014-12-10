(function() {

	'use strict';

	angular.module('onsTemplates')
		.directive('latestBulletins', function() {
			return {
				restrict: 'E',
				templateUrl: 'app/partials/latest-bulletins/latest-bulletins.html'
			}
		})

})();