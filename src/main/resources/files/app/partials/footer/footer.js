(function() {

	var onsTemplates = angular.module('onsTemplates')

	onsTemplates
		.directive('onsFooter',
			function() {
				return {
					restrict: 'E',
					templateUrl: 'app/partials/footer/footer.html'
				}
			}
		)

})();