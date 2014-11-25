(function() {

	'use strict';

	//Contact Us Controller
	angular.module('onsTemplates')
		.controller('ContactUsCtrl', ['$scope', ContactUsController])

	function ContactUsController($scope) {
		$scope.breadcrumb = {
			parent: [],
			current: "Contact Us"
		}
	}

})()
