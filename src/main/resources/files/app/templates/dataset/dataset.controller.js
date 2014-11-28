(function() {

	'use strict';

	angular.module('onsTemplates')
		.controller('DatasetController', ['$scope', '$http', 'Downloader', DatasetController])

	function DatasetController($scope, $http, Downloader) {
		var ctrl = this
		ctrl.sidebarUrl = "app/templates/dataset/datasetsidebar.html"
		var data = ctrl.data = $scope.taxonomy.data
		ctrl.timeseries = false
		ctrl.timeseriesItem
		ctrl.externalUrls = []

		initialize()
		
		function getFileSize() {
			angular.forEach(data.download, function(download, i) {
				console.log('loading filesize for: ' + download.title)
				if (download.xls) {
					if (download.xls === '#') {
					} else {
						var downloadRequest = {
								type: 'external'
						}
						downloadRequest.uriList = [download.xls]
						var promise = $http.post('/filesize', downloadRequest)
						promise.then(function(payload) {
							$scope.taxonomy.data.download[i].xlsFilesize = payload.data
						})
					}
				}
				
				if (download.csv) {
					if (download.csv === '#') {
					} else {
						var downloadRequest = {
								type: 'external'
						}
						downloadRequest.uriList = [download.csv]
						var promise = $http.post('/filesize', downloadRequest)
						promise.then(function(payload) {
							$scope.taxonomy.data.download[i].csvFilesize = payload.data
						})
					}
				}	
			})
		}	
		
		function initialize() {
			var download = data.download
			for (var i = 0; i < download.length; i++) {
				if (download[i].cdids) {
					ctrl.timeseries = true
					ctrl.timeseriesItem = download[i]
					break
				}
			}
			
			getFileSize()
		}

		function downloadXls() {
			download('xlsx');
		}

		function downloadCsv() {
			download('csv');
		}

		function download(type) {
			var downloadRequest = {
				type: type,
				cdidList: ctrl.timeseriesItem.cdids
			};

			var fileName = ctrl.timeseriesItem.title + '.' + downloadRequest.type;
			Downloader.downloadFile(downloadRequest, fileName);
		}

		//Expose functions
		angular.extend(ctrl, {
			downloadXls: downloadXls,
			downloadCsv: downloadCsv
		})

	}

})()