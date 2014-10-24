// Taxonomy base, loads data and binds it to given scope
'use strict';

(function() {

	var onsTemplates = angular.module('onsTemplates')

	onsTemplates.controller('TaxonomyController', ['$scope', 'TaxonomyService',
		function($scope, TaxonomyService) {
			TaxonomyService.loadData($scope)
		}
	])

	onsTemplates
		.service('TaxonomyService', ['$http', '$log', '$location',
			function DataService($http, $log, $location) {

				var service = this
				var dataPath = '/data'

				function loadData(scope) {
					load(resolvePath(), scope, 'data')
				}

				function loadChildrenData(scope) {
					var data = scope.data
					var children = data.children
					var timeseries = data.timeseries
					loadChildren(children)
					// loadChildren(timeseries)
				}

				function loadChildren(children) {
					if (!children) {
						return
					}
					var path = resolvePath()
					var childPath
					var child
					for (var i = 0; i < children.length; i++) {
						child = children[i]
						childPath = path + child.fileName
						load(childPath, child, 'data')
					}
				}

				function resolvePath() {
					var path = $location.$$path
					path = endsWith(path, '/') ? path : (path + '/')
					path = dataPath + path
					return path
				}

				function endsWith(str, suffix) {
					if (str.length === 0) {
						return false
					}
					return str.indexOf(suffix, str.length - suffix.length) !== -1;
				}

				//Loads and attaches data to given parent element with given property name
				function load(path, parent, propertyName) {
					$http.get(path).success(function(data) {
						$log.debug('Taxonomy Service : Successfully loaded data at ', path, ' ', data)
						parent[propertyName] = data
					}).error(function() {
						$log.error('Taxonomy Service : Failed loading data at ' + path)
					})
				}


				//Converts list into two column rows for easy handling on view
				function convert(children, numberOfItems) {
					var result = []
					var index = 0
					var mod
					var length = (numberOfItems && numberOfItems < children.length) ? numberOfItems : children.length
					for (var i = 0; i < length; i = i + 2) {
						result[index] = []
						result[index][0] = children[i]
						if (i + 1 < length) {
							result[index][1] = children[i + 1]
						}
						index++
					}
					return result
				}

				//Expose public api
				angular.extend(service, {
					loadData: loadData,
					loadChildrenData: loadChildrenData,
					convert: convert
				})

			}
		])

})()