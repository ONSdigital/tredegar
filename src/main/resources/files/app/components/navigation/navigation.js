  'use strict';

  (function() {

    angular.module('onsNavigation', [])
      .directive('onsNav', ['$location', '$window', NavDirective])
      .directive('onsNavItems', [NavItemsDirective])
      .directive('onsNavItem', [NavItemDirective])

    function NavDirective($location, $window) {
      return {
        restrict: 'A',
        replace: true,
        transclude: true,
        scope: {},
        controller: NavigationController,
        controllerAs: 'navigation',
        template: '<div ng-transclude></div>'
      }

      function NavigationController($scope, $element, $attrs) {
        var navigation = this
        navigation.showMenu = false
        navigation.items = []
        resolveScreenType()
        listenResize()
        init()

        function init() {
          var path = $location.$$path
          navigation.rootPath = getRootPath(path)
        }

        function getRootPath(path) {
          var tokens = path.split('/')
          if (tokens.length < 2) {
            return "/"
          } else {
            return tokens[1]
          }
        }

        function getCurrentPage() {
          return $location.$$path
        }

        function isCurrentRoot(page) {
          var result = navigation.rootPath === getRootPath(page)
          return result
        }


        function isCurrentPage(page) {
          var result = getCurrentPage() === page.split('#!')[1]
          return result
        }

        function registerItem(item) {
          navigation.items.push(item)
        }

        function toggleMenu() {
          navigation.showMenu = !navigation.showMenu
          if (navigation.showMenu) {
            $scope.hideSearch = true
          }
        }

        //Listens resize event to switch screen type to mobile or desktop
        function listenResize() {
          angular.element($window).bind('resize', function() {
            resolveScreenType()
            $scope.$apply()
          })
        }

        function resolveScreenType() {
          if ($window.innerWidth < 800) {
            navigation.mobile = true
          } else {
            navigation.mobile = false
          }
        }

        angular.extend(navigation, {
          registerItem: registerItem
        })
      }
    }

    function NavItemsDirective() {
      return {
        restrict: 'E',
        require: '^onsNav',
        transclude: true,
        scope: {
          class: '@',
          itemList: '='
        },
        controller: NavItemsController,
        controllerAs: 'navItem',
        link: NavItemsLink,
        template: '<ul ng-class="class" ng-transclude></ul>'
      }

      function NavItemsLink(scope, element, attrs, navigation) {
        console.log(scope.itemList)
        navigation.registerItem(scope)
      }

      function NavItemsController($scope) {
        var navItems = this
        navItems.subItems = []

        function registerSubItem(subItem) {
          navItems.subItems.push(subItem)
        }

        function toggle() {
          if (isMobile()) {
            scope.expanded = !scope.expanded
          } else {
            navigation.gotoPage(scope.href)
          }
        }

        function isMobile() {
          return navigation.mobile
        }

        function isCurrentRoot() {
          var result = navigation.isCurrentRoot(scope.href)
          return result
        }


        angular.extend(navItems, {
          registerSubItem: registerSubItem
        })
      }
    }

    function NavItemDirective() {
      return {
        restrict: 'E',
        require: ['^onsNav', '^onsNavItems'],
        transclude: true,
        replace: true,
        scope: {
          href: '@',
          class: '@'
        },
        link: NavigationSubItemLink,
        template: '<li ng-class="class"> <a ng-transclude href="{{href}}"></a> </li>'
      }

      function NavigationSubItemLink(scope, element, attrs, controllers) {
        var navigation = controllers[0]
        var navItems = controllers[1]
        navItems.registerSubItem(scope)

        function isCurrentPage() {
          var result = navigation.isCurrentPage(scope.href)
          return result
        }

        angular.extend(scope, {
          isCurrentPage: isCurrentPage
        })

      }
    }

  })()