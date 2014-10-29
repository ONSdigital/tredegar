(function() {

    angular.module('onsTemplates')
        .controller('T2Ctrl', ['$scope',
            function($scope) {

                $scope.taxonomy.data.highlightedSections = convertToTable($scope.taxonomy.data.sections, 3) 

                //Converts list into two dimensonal array for easy handling on view
                function convertToTable(children, numberOfItems) {
                    var result = []
                    var index = 0
                    var mod
                    var length = (numberOfItems && numberOfItems < children.length) ? numberOfItems : children.length
                    for (var i = 0; i < length; i = i + 2) {
                        result[index] = []
                        result[index][0] = children[i]
                        if (i + 1 < length) {
                            result[index][1] = children[i + 1]
                        }
                        index++
                    }
                    return result
                }

            }
        ])
})()