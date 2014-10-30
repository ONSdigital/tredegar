(function() {

	angular.module('onsTemplates')
		.controller('T3Controller', ['$scope', T3Controller])
		.directive('stSelectAll', [SelectAllDirective])

	function T3Controller($scope) {
		var ctrl = this
		var items = $scope.taxonomy.data.itemData
		ctrl.allSelected = false

		function toggleSelectAll() {
			ctrl.allSelected = !ctrl.allSelected
			for (var i = 0; i < items.length; i++) {
				items[i].isSelected=ctrl.allSelected
			};
		}

		angular.extend(ctrl, {
			toggleSelectAll : toggleSelectAll
		})

	
	}

	function SelectAllDirective($scope) {
		return {
			restrict: 'A',
			require: '^stTable',
			link: function(scope, element, attr, ctrl) {
				console.log("T3: ") 
				console.log(scope.t3)
				element.bind('click', function() {
					scope.$apply(function() {
						scope.t3.toggleSelectAll()
					})
				})

			}
		}
	}


})()