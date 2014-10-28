(function() {

	'use strict';

	//Contact Us Controller
	angular.module('onsTemplates')
		.controller('ContactUsController', ['$scope', ContactUsController])

	function ContactUsController($scope) {
		$scope.breadcrumb = {
			parent: [],
			current: "Contact Us"
			

		}
	}

})()