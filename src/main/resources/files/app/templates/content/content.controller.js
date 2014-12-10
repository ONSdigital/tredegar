(function() {

	angular.module('onsTemplates')
		.controller('ContentCtrl', ['$scope', '$location', '$log', 'DataLoader',
			function($scope, $location, $log, DataLoader) {
				$scope.content = $scope.taxonomy.data
				$scope.content.hasRelatedBulletins = false
				loadRelatedBulletins($scope.content)
				$scope.scroll = function() {
					anchorSmoothScroll.scrollTo($location.hash())
				}

				function getData(path, callback) {
					$log.debug("Loading data at " + path)
					DataLoader.load(path, callback)
				}

				function loadRelatedBulletins(data) {
					var dataPath = '/data'
					var bulletins = data.relatedBulletins;
					data.relatedBulletinData = []

					if (bulletins != null) {
						for (var i = 0; i < bulletins.length; i++) {
							$scope.content.hasRelatedBulletins = true
							var bulletin = bulletins[i]
							var relatedBulletinPath = dataPath + bulletin.uri
							DataLoader.load(relatedBulletinPath)
								.then(function(relatedBulletin) {
									$log.debug('Loaded related bulletin: ', relatedBulletinPath, ' ', relatedBulletin)
									data.relatedBulletinData.push(relatedBulletin)
								})
						}
					}
				}

			}
		])
		.directive('onsContent', function() {
			return {
				restrict: 'E',
				templateUrl: 'app/templates/content/content.html'
			}
		})

})();