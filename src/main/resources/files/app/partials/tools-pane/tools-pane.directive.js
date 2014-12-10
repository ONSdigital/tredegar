(function() {

'use strict';

angular.module('onsTemplates')
	.directive('onsToolsPane', function() {
		return {
			restrict: 'E',
			templateUrl: 'app/partials/tools-pane/tools-pane.html'
		}
	})

})();