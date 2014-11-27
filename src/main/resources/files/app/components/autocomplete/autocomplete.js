(function() {
	'use strict';

	angular.module('onsAutocomplete', [])
		.directive('onsAutocomplete', ['$http', '$rootScope', '$log', Autocomplete])


	function Autocomplete($http, $rootScope,  $log) {
		return {
			restrict: 'A',
			transclude: true,
			scope: {
				for: '@',
				minChars: '@', //Min character to trigger auto complete
				searchUrl: '@', //Default is search
				searchParam: '@', //Default is q
				dataVar: '@' //Varilable name to store returned data from server
			},
			link: AutocompleteLink,
			template: '<div id="onsAutocompleteDiv" ng-style="style" ng-transclude ng-show="visible" class="autocomplete"></div>'
		}

		function AutocompleteLink(scope, element, attrs, ctrl, transclude) {
			if (!scope.for) {
				throw 'Input element not sepecified for autocomplete, use for="<id>"'
			}

			var minChars = +(scope.minChars || 3) //default chart number to trigger is 3
			var searchUrl = scope.searchUrl || 'search'
			var searchParam = scope.searchParam || 'q'

			//Attach result data to parent scope with given variable name 
			var dataVar = scope.dataVar || 'data'
			var parent = scope.$parent

			var contentContainer = angular.element('')
			var input = scope.input = angular.element('#' + scope.for)
			var firstTime=true 

			scope.visible = false

			initialize()

			function initialize() {
				setWidth(input.outerWidth())
					//Watch input width change for responsive autocomplete box
				watchInputWidth()
				watchInputValue()

				//Do not hide when autocomplete box clicked
				angular.element('#onsAutocompleteDiv')
					.bind('click', function(event) {
						event.stopPropagation();
					});

				//Show when input clicked
				input
					.bind('click', function(event) {
						search(scope.searchTerm)
						event.stopPropagation();
						scope.$apply()
					});

				//Hide autocomplete if anywhere else is clicked
				angular.element('html')
					.bind('click', function() {
						hide()
						scope.$apply()
					})
			}

			function watchInputValue() {
				scope.$watch(
					function() {
						return input.val();
					},
					function(newValue, oldValue) {
						scope.searchTerm = newValue
						if (isMinimumEntered()) {
							search(newValue)
						} else {
							clearData()
							hide()
						}
					})
			}

			function watchInputWidth() {
				scope.$watch(
					function() {
						return input.outerWidth()
					},
					function(newValue, oldValue) {
						scope.style = {
							width: newValue
						}
					})
			}

			function search(key) {
				//Used to fix autocomplete pops up on search results page on first load
				if(firstTime) {
					firstTime=false
					return
				}
				
				if (!isMinimumEntered()) { 
					return
				}

				var url = searchUrl + '?' + searchParam + '=' + key
					//Attach result data to parent scope with given variable name 
				$log.debug('Autocomplete: Searching for: ', url)
				$http.get(url).success(function(data) {
					setData(data)
					show()
				}).error(function(err) {
					clearData()
					hide()
				})
			}

			function clearData() {
				parent[dataVar] = {}
			}

			function setData(data) {
				parent[dataVar] = data
			}

			function setWidth(width) {
				contentContainer.css({
					width: (width)
				})
			}

			function hide() {
				scope.visible = false
			}

			function show() {
				if (isMinimumEntered()) {
					scope.visible = true
				}
			}

			function isMinimumEntered() {
				return scope.searchTerm.length >= minChars
			}

			angular.extend(scope, {
				hide: hide
			})

		}

	}


})()