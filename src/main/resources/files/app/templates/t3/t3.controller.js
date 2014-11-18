(function() {

	angular.module('onsTemplates')
		.controller('T3Controller', ['$scope', 'Downloader', T3Controller])
		.directive('stSelectAll', [SelectAllDirective])
		.directive('stSelect', [SelectDirective])


	function T3Controller($scope, Downloader) {
		var ctrl = this
		var items = $scope.taxonomy.data.itemData
		ctrl.allSelected = false
		ctrl.selectedCount = 0

		function toggleSelectAll() {
			ctrl.allSelected = !ctrl.allSelected
			for (var i = 0; i < items.length; i++) {
				items[i].isSelected = ctrl.allSelected
			};

			ctrl.selectedCount = ctrl.allSelected ? items.length : 0
		}

		function toggleSelect(row) {
			row.isSelected = !row.isSelected
			if (row.isSelected) {
				ctrl.selectedCount++
			} else {
				ctrl.selectedCount--
			}

		}

		function downloadXls() {
			if (ctrl.selectedCount <= 0) {
				return
			}

			download('xlsx')
		}

		function downloadCsv() {
			if (ctrl.selectedCount <= 0) {
				return
			}
			download('csv')
		}

		function download(type) {
			var downloadRequest = {
				type: type
			}
			downloadRequest.uriList = getFileList()
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

		angular.extend(ctrl, {
			toggleSelectAll: toggleSelectAll,
			toggleSelect: toggleSelect,
			downloadXls: downloadXls,
			downloadCsv: downloadCsv
		})


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