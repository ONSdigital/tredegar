(function() {

'use strict';

angular.module('onsComponents')
.directive('markdown', function() {
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

                    htmlText = htmlText.replace(/(<ons-chart\spath="[-A-Za-z0-9+&@#\/%?=~_|!:,.;\(\)*[\]$]+"?\s?\/>)/ig, function(match) {
                        var path = $(match).attr('path');
                        var output = '<div class="chart-container"><iframe frameBorder ="0" scrolling = "no" src="http://localhost:8081/florence/chart.html?path=' + path + '.json"></iframe></div>';
                        //console.log(output);
                        return output; //'[chart path="' + path + '" ]';
                    });


                element
                    .html(htmlText);
            //         .find("pre")
            //         .replaceWith("<p>"+
            //     '<img src="/ui/img/equation.png" alt="sample chart">'+
            // "</p>");
                element
                    .find("blockquote").find("p")
                    .addClass("box--padded--highlight");

                // element
                //     .find("ul").addClass("list--neutral box--cyan--separated-left")
                //     .find("ul").find("li").addClass("big");
                // element
                //     .find("h3")
                //     .addClass("gamma");
            });
        }
    };
});


})();