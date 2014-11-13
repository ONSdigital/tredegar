(function() {

    angular.module('onsTemplates')
        .controller('T2Ctrl', ['$scope',
            function($scope) {

                $scope.taxonomy.data.sections = convertToTable($scope.taxonomy.data.sections)
                //Converts list into 3 column arrays for easy handling on view
                function convertToTable(children) {
                    var result = []
                    var index = 0
                    var mod
                    var length = children.length
                    for (var i = 0; i < length; i = i + 3) {
                        result[index] = []
                        result[index][0] = children[i]
                        if (i + 1 < length) {
                            result[index][1] = children[i + 1]
                        }
                        if (i + 2 < length) {
                            result[index][2] = children[i + 2]
                        }
                        index++
                    }
                    return result
                }
            }
        ])
})()
