(function() {
        angular.module('onsFilters', [])
            .filter('slice', slice)
            .filter('range', range)
            .filter('rangeDate', rangeDate);


        // Slice filter used to get a part of given array, can be used with ng-repeat
        function slice() {
            return function(arr, start, end) {
                return arr.slice(start, end);
            };
        }


        //Range filter used to get an array of numbers starting and ending from given numbers. Can be used with ng-repeat to repeat arbitrary number of times
        function range() {
            return function(input, start, end) {
                start = parseInt(start);
                end = parseInt(end);
                for (var i = start; i <= end; i++)
                    input.push(i);
                return input;
            };
        }

        function rangeDate() {
            return function(input, minValue, maxValue, property) {
                var results = [];
                var item;
                var value;
                for (var i = 0; i < input.length; i++) {
                    item = input[i];
                    value = item[property];
                    if (minValue && maxValue) {
                        if (value >= minValue && value <= maxValue) {
                            results.push(item);
                        }
                    } else if (minValue) {
                        if (value >= minValue) {
                            results.push(item);
                        }
                    } else if (maxValue) {
                        if (value <= maxValue) {
                            results.push(item);
                        }
                    } else {
                        results.push(item);
                    }
                }
                return results;
            };
    }

})();
