(function() {
	'use strict';

	angular.module('onsTaxonomy', [])
		.service('Taxonomy', ['$http', '$log', '$location',
			TaxonomyService
		])

	function TaxonomyService($http, $log, $location) {

		var service = this
		var dataPath = '/data'

		function loadData(callback) {
			load(resolvePath(), function(data) {
				service.data = data
				resolveChildren()
				if (callback) {
					callback(service.data)
				}
			})
		}

		function resolveChildren() {
			var level = service.data.level
			var children = service.data.children
			if (level === 't3') {
				return
			}

			loadChildrenData()
			if (level === 't1') {
				$log.debug('Taxonomy Service: Converting children to table for t1')
				service.data.children = convertToTable(children)
			} else if (level === 't2') {
				$log.debug('Taxonomy Service: Converting children to table for t2')
				service.data.highlightedChildren = convertToTable(children, 3)
			}
		}

		function loadChildrenData() {
			$log.debug('Taxonomy Service: Loading children data')
			var data = service.data
			var children = data.children
			var timeseries = data.timeseries
			loadChildren(children)
		}

		function loadChildren(children) {
			if (!children) {
				return
			}
			for (var i = 0; i < children.length; i++) {
				loadChild(children[i])
			}
		}

		function loadChild(child) {
			var path = resolvePath()
			var childPath = path + child.fileName
			load(childPath, function(data) {
				child.data = data
			})
		}

		//Loads and attaches data to given parent element with given property name
		function load(path, callback) {
			var result = $http.get(path).success(function(data) {
				$log.debug('Taxonomy Service : Successfully loaded data at ', path, ' ', data)
				callback(data)
			}).error(function() {
				$log.error('Taxonomy Service : Failed loading data at ' + path)
			})
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


		//Converts list into two dimensonal array for easy handling on view
		function convertToTable(children, numberOfItems) {
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
			convertToTable: convertToTable
		})

	}

})()