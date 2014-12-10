  (function() {

  'use strict';


    angular.module('onsNavigation', [])
      .directive('onsNav', ['$window', '$rootScope', Nav])
      .directive('onsNavItem', ['$location', 'StringUtil', NavItem])
      .directive('onsNavCollapseClass', [NavCollapseClass])
      .directive('onsNavExpandClass', [NavExpandClass])
      .directive('onsNavMobile', [NavMobile]) //Show on mobile
      .directive('onsNavDesktop', [NavDesktopDirective]) //Show on desktop

    /*
    Adds responsive navigation functionality with expandable menus on mobile
    */
    function Nav($window, $rootScope) {
      return {
        restrict: 'A',
        scope: {
          navWidgetVar: '@',
          activeClass: '@navActiveClass'
        },
        controller: NavController,
        controllerAs: 'navigation',
      }

      function NavController($scope, $element, $attrs) {
        var navigation = this
        navigation.activeClass = $scope.activeClass
        navigation.showOnMobile = false
        navigation.mobile = false //Screen type
        var items = []
        init()

        function init() {
          resolveScreenType()
          listenResize()
          watchLocation()
            //Injected to parent scope to be used as a widget.
          if ($scope.navWidgetVar) {
            $scope.$parent[$scope.navWidgetVar] = navigation
          }

        }

        function watchLocation() {
          $rootScope.$on('$locationChangeSuccess', function(event) {
            for (var i = 0; i < items.length; i++) {
              items[i].resolveClass()
              items[i].expanded = false
            };
          })
        }

        function toggleMobileMenu() {
          navigation.showOnMobile = !navigation.showOnMobile
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

        function registerItem(item) {
          items.push(item)
        }

        angular.extend(navigation, {
          toggleMobileMenu: toggleMobileMenu,
          registerItem: registerItem

        })
      }
    }

    function NavItem($location, StringUtil) {
      return {
        restrict: 'A',
        require: '^onsNav',
        scope: {
          expandable: '=?'
        },
        controller: NavItemController,
        controllerAs: 'navItem',
        link: NavItemLink
      }

      function NavItemLink(scope, element, attrs, navigation) {
        scope.url = attrs.onsNavItem || ''
        scope.currentPage = false
        scope.expanded = false
        initialize()

        function initialize() {
          navigation.registerItem(scope)
          bindClick()
          resolveClass()
        }

        function resolveClass() {
          if (isCurrentPage()) {
            element.addClass(navigation.activeClass)
          } else {
            element.removeClass(navigation.activeClass)
          }
        }

        function bindClick() {
          element.bind("click", function(e) {
            if (!scope.expandable) {
              //Prevent link clicks propogated to navigation link
              e.stopPropagation();
            } else {
              if (isMobile()) {
                scope.expanded = !scope.expanded
                scope.$apply() // Trigger Angular digest cycle to process changed values
              }
            }
          })
        }

        function isMobile() {
          return navigation.mobile
        }

        function isCurrentPage() {
          return (scope.url && (StringUtil.startsWith($location.$$path, scope.url)))
        }

        angular.extend(scope, {
          resolveClass: resolveClass
        })
      }

      function NavItemController($scope) {
        var navItem = this

        function isExpanded() {
          return $scope.expanded
        }

        angular.extend(navItem, {
          isExpanded: isExpanded
        })
      }
    }

    function NavCollapseClass() {
      return {
        restrict: 'A',
        require: '^onsNavItem',
        scope: {},
        link: NavCollapseClassLink
      }

      function NavCollapseClassLink(scope, element, attrs, expandable) {
        var collapseClass = scope.collapseClass = attrs.onsNavCollapseClass
        if (collapseClass) {
          watchExpand()
        }

        function watchExpand() {
          scope.$watch(expandable.isExpanded, function() {
            if (expandable.isExpanded()) {
              element.removeClass(collapseClass)
            } else {
              element.addClass(collapseClass)
            }
          })
        }
      }
    }

    function NavExpandClass() {
      return {
        restrict: 'A',
        require: '^onsNavItem',
        link: NavExpandClassLink
      }

      function NavExpandClassLink(scope, element, attrs, navItem) {
        var expandClass = scope.expandClass = attrs.onsNavExpandClass
        if (expandClass) {
          watchExpand()
        }

        function watchExpand() {
          scope.$watch(navItem.isExpanded, function(newValue) {
            if (newValue) {
              element.addClass(expandClass)
            } else {
              element.removeClass(expandClass)
            }
          })
        }
      }
    }

    /*Shows element only on mobile screens*/
    function NavMobile() {
      return {
        restrict: 'A',
        require: '^onsNav',
        link: NavMobileLink,
        transclude: true,
        template: '<span ng-transclude ng-show="isMobile()"></span>'
      }

      function NavMobileLink(scope, element, attrs, navigation) {

        function isMobile() {
          return navigation.mobile
        }

        angular.extend(scope, {
          isMobile: isMobile
        })
      }
    }


    /*Shows element only on desktop screens*/
    function NavDesktopDirective() {
      return {
        restrict: 'A',
        require: '^onsNav',
        link: NavDesktopLink,
        transclude: true,
        template: '<span ng-transclude ng-show="isDesktop()"></span>'
      }

      function NavDesktopLink(scope, element, attrs, navigation) {

        function isDesktop() {
          return !navigation.mobile
        }

        angular.extend(scope, {
          isDesktop: isDesktop
        })
      }
    }

  })();