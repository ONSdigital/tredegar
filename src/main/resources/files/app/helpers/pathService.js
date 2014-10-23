'use strict';

(function() {
	var onsHelpers = angular.module('onsHelpers')
	onsHelpers
		.service('PathService', ['$location',
			function($location) {
				var service = this

				function getPath() {
					return $location.$$path
				}

				function getPage() {
					var path = $location.$$path
					var lastIndex = path.lastIndexOf('/')
					var parenPath = path.substring(lastIndex + 1, path.length)
					return parenPath
				}

				angular.extend(service, {
					getPath: getPath,
					getPage: getPage
				})

			}
		])
})()