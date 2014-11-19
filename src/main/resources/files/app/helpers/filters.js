(function() {
    angular.module('onsFilters', [])
        .filter('slice', slice)
        .filter('range', range)
        .filter('rangeDate', rangeDate)
        .filter('join', join)
        .filter('charLimit', charLimit)


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

    //Joins given text array or property of object array with given seperator if any
    function join() {
        return function(list, seperator, propertyName) {
            seperator = seperator || '';
            if (!propertyName) {
                return list.join(seperator)
            }

            result = '';
            for (var i = 0; i < list.length - 1; i++) {
                result += list[i][propertyName] + seperator
            }

            result += list[i][propertyName] // No seperator after last element
            return result
        }
    }

      //Limits character number to given number, attaches overflow text if given
    function charLimit() {
        return function(text, limit, overflowText) {
            text = text || ''
            limit = limit || text.length
            overflowText = overflowText|| ''

            if(text.length <= limit) {
                return text
            }            

            return text.substr(0, limit) + overflowText
        }
    }

    function rangeDate() {
        return function(input, min, max, property) {
            var results = [];
            var item;
            var value;
            for (var i = 0; i < input.length; i++) {
                item = input[i];
                value = item[property];
                if (min && max) {
                    if (value >= min && value <= max) {
                        results.push(item);
                    }
                } else if (min) {
                    if (value >= min) {
                        results.push(item);
                    }
                } else if (max) {
                    if (value <= max) {
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