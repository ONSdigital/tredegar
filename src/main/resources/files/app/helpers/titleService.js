//Use title service to set page title dynamically

'use strict';

(function() {
	var onsHelpers = angular.module('onsHelpers')
	onsHelpers
		.factory('Page', function() {
			var title = 'Office Of National Statistics';
			return {
				title: function() {
					return title
				},
				setTitle: function(newTitle) {
					title = newTitle
				}
			}
		})
})()