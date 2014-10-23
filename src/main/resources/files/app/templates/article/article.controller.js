angular.module('onsTemplates')
	.controller('ArticleCtrl', ['$scope',
		function($scope) {
			$scope.header = "Expert Analysis"
			$scope.contentType = "article"
			$scope.sidebar = true
			$scope.sidebarUrl = "app/templates/content/contentsidebar.html"
		}
	])