'use strict';

angular.module('onsComponents')
.directive('markdown', function($http) {
    var converter = new Showdown.converter();
    return {
        restrict: 'A',
        scope: {
            link: '@'
        },
        template: '<p></p>',
        link: function(scope, element, attrs) {
            attrs.$observe('link', function(link) {
                var htmlText = converter.makeHtml(link);
                element
                    .html(htmlText);
            //         .find("pre")
            //         .replaceWith("<p>"+
            //     '<img src="/ui/img/equation.png" alt="sample chart">'+
            // "</p>");
                element
                    .find("blockquote").find("p")
                    .addClass("box--padded--highlight");
                element
                    .find("ul").addClass("list--neutral box--cyan--separated-left")
                    .find("ul").find("li").addClass("big");
            });
        }
    };
});
