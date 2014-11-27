/*
	Angularized and modified version of double tap to go (used in cx pattern library)
	By Osvaldas Valutis, www.osvaldas.info
	Available for use under the MIT License
*/

(function() {

	'use strict';

	angular.module('onsDoubleTap', [])
		.directive('onsDoubleTap', ['$document', '$window', '$log',
			DoubleTapToGo
		])

	function DoubleTapToGo($document, $window, $log) {
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

			var t = false;
			element.bind("click", function(event) {
				if (element[0] != t[0]) {
					event.preventDefault();
					t = element
				}
			});
			element.bind("click touchstart MSPointerDown", function(event) {
				var r = true,
				i = angular.element(event.target).parents();
				for (var s = 0; s < i.length; s++)
					if (i[s] == t[0]) {
						r = false;
					}
				if (r) {
					t = false
				}
			})
		}
	}


})()