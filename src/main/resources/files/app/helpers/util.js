'use strict';

(function() {
	var onsUtils = angular.module('onsUtils', [])
	onsUtils
		.factory('PageUtil', PageUtil)
		.factory('ArrayUtil', ArrayUtil)
		.factory('StringUtil', StringUtil)

	function PageUtil() {
		var pageUtil = this
		var title = 'Office Of National Statistics';

		function title() {
			alert('Reading title')
			return title;
		}

		function setTitle(newTitle) {
			title = newTitle
		}

		angular.extend(pageUtil, {
			title:title,
			setTitle:setTitle
		})

		return pageUtil
	}

	function ArrayUtil() {

		var arrayUtil = this

		//get first element of array
		function getFirst(array) {
			if (isNotEmpty(array)) {
				return array[0]
			}
		}

		//get last element of given array
		function getLast(array) {
			if (isNotEmpty(array)) {
				return array[array.length - 1]
			}
		}

		//Check if arrray is not empty
		function isNotEmpty(array) {
			return (array && array.length > 0)
		}


		function isEmpty(array) {
			return (!array || array.length === 0)
		}

		//Remove duplicate values in given array
		function toUnique(a) { //array,placeholder,placeholder
			var b = a.length;
			var c
			while (c = --b) {
				while (c--) {
					a[b] !== a[c] || a.splice(c, 1);
				}
			}
		}

		angular.extend(arrayUtil, {
			getLast:getLast,
			getFirst:getFirst,
			isNotEmpty:isNotEmpty,
			isEmpty:isEmpty,
			toUnique:toUnique
		})

		return arrayUtil

	}

	function StringUtil() {

		var stringUtil = this

		//String startsWith util function
		function startsWith(str, prefix) {
			if (!str || str.length === 0) {
				return false
			}
			return str.indexOf(prefix,0) === 0;
		}

		//String endsWith util function
		function endsWith(str, suffix) {
			if (!str || str.length === 0) {
				return false
			}
			return str.indexOf(suffix, str.length - suffix.length) !== -1;
		}

	
		angular.extend(stringUtil, {
			startsWith:startsWith,
			endsWith:endsWith
		})

		return stringUtil
	}

})()