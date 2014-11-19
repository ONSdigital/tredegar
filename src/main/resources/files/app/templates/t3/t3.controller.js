'use strict';

(function() {

	angular.module('onsTemplates')
		.controller('T3Controller', ['$scope', 'Downloader', T3Controller])
		// .directive('stSelectAll', [SelectAllDirective])
		// .directive('stSelect', [SelectDirective])
		.controller('T3HeadlineChartController', ['$scope', 'Chart', '$rootScope', T3HeadlineChartController])
		.controller('T3TableChartController', ['$scope', 'Chart', '$rootScope', T3TableChartController])

	function T3Controller($scope, Downloader) {
		var t3 = this
		var items = $scope.taxonomy.data.items
		t3.allSelected = false
		t3.selectedCount = 0

		function isLoading() {
			
		}


		//Table element selection not needed anymore since no download buttons on t3 page, below stuff is to be deleted

		// function toggleSelectAll() {
		// 	ctrl.allSelected = !ctrl.allSelected
		// 	for (var i = 0; i < items.length; i++) {
		// 		items[i].isSelected = ctrl.allSelected
		// 	};

		// 	ctrl.selectedCount = ctrl.allSelected ? items.length : 0
		// }

		// function toggleSelect(row) {
		// 	row.isSelected = !row.isSelected
		// 	if (row.isSelected) {
		// 		ctrl.selectedCount++
		// 	} else {
		// 		ctrl.selectedCount--
		// 	}

		// }

		// function downloadXls() {
		// 	if (ctrl.selectedCount <= 0) {
		// 		return
		// 	}

		// 	download('xlsx')
		// }

		// function downloadCsv() {
		// 	if (ctrl.selectedCount <= 0) {
		// 		return
		// 	}
		// 	download('csv')
		// }

		// function download(type) {
		// 	var downloadRequest = {
		// 		type: type
		// 	}
		// 	downloadRequest.uriList = getFileList()
		// 	var fileName = $scope.getPage() + '.' + downloadRequest.type;
		// 	Downloader.downloadFile(downloadRequest,fileName)
		// }

		// function getFileList() {
		// 	var uriList = []
		// 	for (var i = 0; i < items.length; i++) {
		// 		if (items[i].isSelected) {
		// 			uriList.push(items[i].uri)
		// 		}
		// 	}
		// 	return uriList
		// }

		angular.extend(t3, {
			// toggleSelectAll: toggleSelectAll,
			// toggleSelect: toggleSelect,
			// downloadXls: downloadXls,
			// downloadCsv: downloadCsv,
			isLoading:isLoading
		})
		
	}
	
	function T3HeadlineChartController($scope, Chart) {
        var t3 = this
        
        // due to async nature of http load then the headline data may not be available
        // so place a watch upon it
        if (!$scope.taxonomy.data.headline.data) {
        	$scope.$watch('taxonomy.data.headline.data', function() {
        		Chart.buildChart(t3, $scope.taxonomy.data.headline.data, true)
        	})
        } else {
        	Chart.buildChart(t3, $scope.taxonomy.data.headline.data, true)
        } 
	}
	
	// function SelectAllDirective() {
	// 	return {
	// 		restrict: 'A',
	// 		link: function(scope, element, attr) {
	// 			element.bind('click', function() {
	// 				scope.$apply(function() {
	// 					scope.t3.toggleSelectAll()
	// 				})
	// 			})

	// 		}
	// 	}
	// }

	// function SelectDirective() {
	// 	return {
	// 		restrict: 'A',
	// 		link: function(scope, element, attr) {
	// 			element.bind('click', function() {
	// 				scope.$apply(function() {
	// 					scope.t3.toggleSelect(scope.item)
	// 				})
	// 			})

	// 		}
	// 	}
	// }

	function T3TableChartController($scope, Chart) {
		var ctrl = this
		
        // due to async nature of http load then the data may not be available
        // so place a watch upon it
        if (!$scope.item.data) {
        	$scope.$watch('item.data', function() {
        		Chart.buildChart(ctrl, $scope.item.data, false)
        	})
        } else {
        	Chart.buildChart(ctrl, $scope.item.data, false)
        } 
	}
	
	function SelectAllDirective() {
		return {
			restrict: 'A',
			link: function(scope, element, attr) {
				element.bind('click', function() {
					scope.$apply(function() {
						scope.t3.toggleSelectAll()
					})
				})

			}
		}
	}

	function SelectDirective() {
		return {
			restrict: 'A',
			link: function(scope, element, attr) {
				element.bind('click', function() {
					scope.$apply(function() {
						scope.t3.toggleSelect(scope.item)
					})
				})

			}
		}
	}

})()