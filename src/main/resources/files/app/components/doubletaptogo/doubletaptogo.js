/*
	Angularized and modified version of double tap to go (used in cx pattern library)
	By Osvaldas Valutis, www.osvaldas.info
	Available for use under the MIT License
*/

(function() {

	'use strict';

	angular.module('onsDoubleTap', [])
		.service('DoubleTapService', DoubleTapService)
		.directive('onsDoubleTap', ['$document', '$window', '$log', 'DoubleTapService',
			DoubleTapToGo
		])

	function DoubleTapService() {
		var service = this
		var items = [];

		function registerDoubleTapItem(item) {
			items.push(item)
		}

		function clearTaps() {
			for (var i = 0; i < items.length; i++) {
				items[i].tapped = false
			}
		}

		angular.extend(service, {
			registerDoubleTapItem: registerDoubleTapItem,
			clearTaps:clearTaps
		})

	}

	function DoubleTapToGo($document, $window, $log, DoubleTapService) {
		return {
			restrict: 'A',
			link: DoubleTapToGoLink
		}


		function DoubleTapToGoLink(scope, element) {

			var window = $window
			var document = $document
			var navigator = $window.navigator
			if (!("ontouchstart" in window) && !navigator.msMaxTouchPoints && !navigator.userAgent.toLowerCase().match(/windows phone os 7/i)) {
				return
			}

			DoubleTapService.registerDoubleTapItem(scope)

			scope.tapped = false
			element.bind("click", function(event) {
				if (element[0] != scope.tapped[0]) {
					event.preventDefault();
					DoubleTapService.clearTaps();//Clear all other double tap items tap mark
					scope.tapped = element
				} else {
					scope.tapped = false
				}
			});
			// element.bind("click touchstart MSPointerDown", function(event) {
			// 	var r = true,
			// 	i = angular.element(event.target).parents();
			// 	for (var s = 0; s < i.length; s++)
			// 		if (i[s] == scope.tapped[0]) {
			// 			r = false;
			// 		}
			// 	if (r) {
			// 		scope.tapped = false
			// 	}
			// })
		}
	}


})()