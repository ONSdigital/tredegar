(function() {

	//Enable hashbang
	angular.module('onsApp')
		.config(['$routeProvider', '$locationProvider',
			function($routeProvider, $locationProvider) {
				$locationProvider.html5Mode(false).hashPrefix('!');
			}
		])
})()