(function() {

	'use strict';

	angular.module('onsTemplates').
	directive('relatedArticles', function() {
		return {
			restrict: 'E',
			templateUrl: 'app/partials/related-articles/related-articles.html'
		}
	})

})();