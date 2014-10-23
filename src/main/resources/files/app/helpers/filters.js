var helpers = angular.module('onsHelpers');

helpers
    .filter('slice', function() {
        return function(arr, start, end) {
            return arr.slice(start, end);
        }
    })

helpers
    .filter('range', function() {
        return function(input, start, end) {
            var start = parseInt(start)
            var end = parseInt(end)
            for (var i = start; i <= end; i++)
                input.push(i)
            return input
        }
    })