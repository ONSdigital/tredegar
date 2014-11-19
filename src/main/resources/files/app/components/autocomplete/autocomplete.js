(function() {
	'use strict';

	angular.module('onsAutocomplete', [])
		.directive('onsAutocomplete', Autocomplete)


	function Autocomplete() {
		return {
			restrict: 'E',
			scope: {},
			controller: AutocompleteController,
			controllerAs: 'autocomplete'
		}

		function AutocompleteController() {
			alert("hey")
		}
	}


})()