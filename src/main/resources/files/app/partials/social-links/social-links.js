'use strict';

angular.module('onsTemplates')
    .directive('socialLinks', function() {
        return {
            restrict: 'E',
            templateUrl: 'app/partials/social-links/social-links.html'
        };
    });
