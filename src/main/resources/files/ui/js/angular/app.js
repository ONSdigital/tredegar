//Angular code

'use strict';

/* App Module */

var onsApp = angular.module('onsApp', [
  'ngRoute',
  'onsControllers'
]);

onsApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
     when('/',{
        redirectTo: '/home'
      }).
     otherwise({
        templateUrl: 'templates/template.html'
      });
  }]);


// $(document).ready(function() {
//   $('body').addClass('js');

//   $(document).ready(function() {
//     $('.tooltip').tooltipster({
//       'maxWidth': 270
//     });
//   });

//   var placeHolderConfig = {
//     className: 'placeholder-polyfill'
//   };
//   Modernizr.load({
//     test: Modernizr.input.placeholder,
//     nope: [
//             '/ui/css/lib/placeholder_polyfill.min.css',
//             '/ui/js/lib/jquery.placeholder_polyfill.combo.min.js'
//           ]
//   });

// });

// $(document).ready(function(){
//     $('#viewAllStatsBulletins').click(function(){
//         var url = window.location.pathname;
//         var urlTokens = url.split('/');
//         var nameOfBulletins = urlTokens[urlTokens.length - 2];
//         nameOfBulletins = '/collection.html?q=' + nameOfBulletins + '&type=bulletins'
//         window.location.href = nameOfBulletins;
//     });
// });
