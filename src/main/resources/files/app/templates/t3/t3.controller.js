(function() {

	angular.module('onsTemplates')
		.controller('T3Controller', ['$scope', '$http', 'Downloader', T3Controller])
		.controller('T3HeadlineChartController', ['$scope', 'Chart', '$rootScope', T3HeadlineChartController])
		.controller('T3TableChartController', ['$scope', 'Chart', '$rootScope', T3TableChartController])
		.directive('stSelectAll', [SelectAllDirective])
		.directive('stSelect', [SelectDirective])

	function T3Controller($scope, $http, Downloader, $log) {
		var t3 = this
		var items = $scope.taxonomy.data.itemData
		t3.allSelected = false
		t3.selectedCount = 0

		function toggleSelectAll() {
			t3.allSelected = !t3.allSelected
			for (var i = 0; i < items.length; i++) {
				items[i].isSelected = t3.allSelected
			};

			t3.selectedCount = t3.allSelected ? items.length : 0
		}

		function toggleSelect(row) {
			row.isSelected = !row.isSelected
			if (row.isSelected) {
				t3.selectedCount++
			} else {
				t3.selectedCount--
			}

		}

		function downloadXls() {
			if (t3.selectedCount <= 0) {
				return
			}

			download('xlsx')
		}

		function downloadCsv() {
			if (t3.selectedCount <= 0) {
				return
			}
			download('csv')
		}

		function download(type) {
			var downloadRequest = {
				type: type
			}
			downloadRequest.uriList = getFileList()
			console.log(JSON.stringify(downloadRequest))
			var fileName = $scope.getPage() + '.' + downloadRequest.type;
			Downloader.downloadFile(downloadRequest,fileName)
			
		}

		function getFileList() {
			var uriList = []
			for (var i = 0; i < items.length; i++) {
				if (items[i].isSelected) {
					uriList.push(items[i].uri)
				}
			}
			return uriList
		}
		
		angular.extend(t3, {
			toggleSelectAll: toggleSelectAll,
			toggleSelect: toggleSelect,
			downloadXls: downloadXls,
			downloadCsv: downloadCsv
		})
		
	}
	
	function T3HeadlineChartController($scope, Chart) {
        var t3 = this

        // due to async nature of http load then the headline data may not be available
        // so place a watch upon it
        if (!$scope.taxonomy.data.headlineData) {
        	$scope.$watch('taxonomy.data.headlineData', function() {
        		Chart.buildChart(t3, $scope.taxonomy.data.headlineData, true)
        	})
        } else {
        	Chart.buildChart(t3, $scope.taxonomy.data.headlineData, true)
        } 
	}
	

	function T3TableChartController($scope, Chart) {
		var ctrl = this
		var scopedTimeseries = $scope.taxonomy.data.itemData[$scope.$index]
		Chart.buildChart(ctrl, scopedTimeseries, false)
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