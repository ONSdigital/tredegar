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
        		var absUrl = $location.absUrl().replace('#', '%23');
                return absUrl;
        	}

        	angular.extend($scope, {
        		getAbsoluteUrl:getAbsoluteUrl
        	})
        }
    });
