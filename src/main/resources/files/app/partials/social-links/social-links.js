'use strict';

angular.module('onsTemplates')
    .directive('socialLinks', function() {
        return {
            restrict: 'E',
            templateUrl: 'app/partials/social-links/social-links.html',
            controller: ['$scope', '$location', SocialLinksController]
        };

        function SocialLinksController($scope,$location) {
        	function getAbsoluteUrl() {
        		var absUrl = encodeURIComponent($location.absUrl());
                return absUrl;
            }

            function getFbAbsoluteUrl() {
                return $location.absUrl().replace('#!', '?onsfb' )
            }

            angular.extend($scope, {
                getAbsoluteUrl:getAbsoluteUrl,
                getFbAbsoluteUrl:getFbAbsoluteUrl
            })
        }
    });
