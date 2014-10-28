(function() {
    angular.module('onsFilters', [])
        .filter('slice', slice)
        .filter('range', range)


    // Slice filter used to get a part of given array, can be used with ng-repeat
    function slice() {
        return function(arr, start, end) {
            return arr.slice(start, end);
        }
    }


    //Range filter used to get an array of numbers starting and ending from given numbers. Can be used with ng-repeat to repeat arbitrary number of times
    function range() {
        return function(input, start, end) {
            var start = parseInt(start)
            var end = parseInt(end)
            for (var i = start; i <= end; i++)
                input.push(i)
            return input
        }
    }
})()