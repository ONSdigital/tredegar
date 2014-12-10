(function() {

	'use strict';
	angular.module('onsTemplates')
		.directive('contactDetails', ContactDetails)


	function ContactDetails() {
		return {
			restrict: 'E',
			templateUrl: 'app/partials/contact-details/contactdetails.html'
		}
	}
})();