(function() {
  'use strict';

  angular.module('onsNavigation', [])
    .directive('onsNav', ['$location', '$window', NavigationDirective])
    .directive('onsNavItem', [NavigationItemDirective])
    .directive('onsNavSubItem', [NavigationSubItemDirective])

  function NavigationDirective($location, $window) {
    return {
      restrict: 'E',
      replace: true,
      transclude: true,
      scope: {
        class: '@',
        trigger: '&'
      },
      controller: NavigationController,
      controllerAs: 'navigation',
      templateUrl: 'app/components/navigation/navigationmain.html'
    }

    function NavigationController($scope, $element, $attrs) {
      var navigation = this
      navigation.showMenu = false
      navigation.expandablePanes = []
      resolveScreenType()
      bindResize()

      init()

      function init() {
        var path = $location.$$path
        navigation.location = getPath(path)
      }

      function getPath(path) {
        var tokens = path.split('/')
        if (tokens.length < 2) {
          return "/"
        } else {
          return tokens[1]
        }
      }

      function isCurrentPage(page) {
        var result =  navigation.location === getPath(page)
        return result

      }

      function addPane(pane) {
        navigation.expandablePanes[pane.navLabel] = pane
      }

      function toggleMenu() {
        navigation.showMenu = !navigation.showMenu
      }

      function gotoPage(path) {
        $location.path(path.substr(2))
        navigation.showMenu = false
      }

      //Listens resize event to switch screen type to mobile or desktop
      function bindResize() {
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

      function toggleSearch() {
        $scope.trigger()
      }

      angular.extend(navigation, {
        addPane: addPane,
        toggleMenu: toggleMenu,
        gotoPage: gotoPage,
        toggleSearch: toggleSearch,
        isCurrentPage: isCurrentPage
      })

    }
  }

  function NavigationItemDirective() {
    return {
      restrict: 'E',
      replace: true,
      require: '^onsNav',
      transclude: true,
      scope: {
        navLabel: '@',
        labelClass: '@',
        href: '@',
        expandable: '@',
        class: '@'
      },
      link: NavigationItemLink,
      templateUrl: 'app/components/navigation/navigationitem.html'
    }

    function NavigationItemLink(scope, element, attrs, navigation) {
      scope.expanded = false
      navigation.addPane(scope)

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

      function isCurrentPage() {
        var result = navigation.isCurrentPage(scope.href)
        return result
      }

      angular.extend(scope, {
        toggle: toggle,
        isMobile: isMobile,
        isCurrentPage: isCurrentPage
      })


    }
  }

  function NavigationSubItemDirective() {
    return {
      restrict: 'E',
      replace: true,
      require: '^onsNav',
      transclude: true,
      scope: {
        navLabel: '@',
        href: '@',
        class: '@'
      },
      templateUrl: 'app/components/navigation/navigationsubitem.html'
    }
  }

})()