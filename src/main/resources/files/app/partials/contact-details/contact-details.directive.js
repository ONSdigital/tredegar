'use strict';

angular.module('onsTemplates')
	.directive('contactDetails', function() {
		return {
			restrict: 'E',
			templateUrl: 'app/partials/contact-details/contactdetails.html'
		}
	})