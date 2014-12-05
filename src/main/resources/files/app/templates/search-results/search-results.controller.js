//Search ctrl, used for search results page
"use strict";

(function() {

  angular.module("onsTemplates")
    .controller("SearchController", ['$scope', '$rootScope', '$window', '$log', 'PageUtil', 'StringUtil', 'searchResponse', '$routeParams', SearchController])


  function SearchController($scope, $rootScope, $window, $log, PageUtil, StringUtil, searchResponse, $routeParams) {
    var searchTerm = $scope.searchTerm = $routeParams.searchTerm
    var page = $routeParams.page
    if (!searchTerm) {
      $scope.noSearchTerm = true
      return
    }

    if (!searchResponse || !searchResponse.contentSearchResult.results) {
      $rootScope.error = 500
      return
    }

    $scope.searchResponse = searchResponse
    var type = $scope.type = $routeParams.type
    var filters = {}
    var departmentHandoff = [
      ["dfe", "education", "gcse", "a level", "degree", "nvq", "school", "college", "university", "national curriculum", "qualification", "teacher training", "pupil absence", "exclusions", "school workforce", "key stage"],
      ["bis", "business", "apprenticeship", "building", "construction", "higher education", "trade union", "enterprise"],
      ["decc", "energy", "solar", "coal", "oil", "gas", "electricity", "fuel poverty", "renewables", "greenhouse gas"],
      ["defra", "agriculture", "farming", "air quality", "emissions", "recycling", "food", "conservation", "biodiversity", "animals", "wildlife", "ecosystem"],
      ["hmrc", "tax", "benefit", "paye", "national insurance", "vat", "tabacco"],
      ["ho", "arrests", "asylum", "immigration", "firearm", "crime", "visa", "police", "terrorism", "detention"],
      ["dclg", "revenue expenditure", "local authority", "council tax", "homelessness", "social housing", "help to buy"],
      ["dcms", "charitable giving"],
      ["dft", "bus", "vehicle", "driver", "rider", "freight", "vehicle licensing", "road", "biofuel", "road casualties", "traffic, congestion", "road conditions"],
      ["dwp", "national insurance", "social fund", "child maintenance", "lone parents", "child support agency", "benefit fraud", "universal credit"],
      ["dh", "abortion"],
      ["fc", "woodland", "timber", "forestry", "nursery"],
      ["mod", "defence", "armed forces", "military", "veterans", "reserves", "war pensions"],
      ["moj", "criminal justice", "prison", "offender", "civil justice", "knife possession", "legal aid", "youth custody", "probation", "reoffence", "asbo", "prison population"],
      ["nhs", "a&e", "dementia", "waiting times", "hospital", "critical care", "inpatient", "outpatient", "ambulance"],
      ["tis", "insolvency"],
      ["wg", "wales", "welsh"],
      ["sg", "scotland", "scottish"],
      ["nie", "northern ireland", "irish"]
    ];
    var relatedDepartments = {
      dfe: {
        departmentName: "the Department for Education",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-education",
        departmentLogo: "ui/img/dfe.png"
      },
      bis: {
        departmentName: "the Department for Business, Innovation & Skills",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-business-innovation-skills",
        departmentLogo: "ui/img/bis.png"
      },
      decc: {
        departmentName: "the Department of Energy & Climate Change",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-of-energy-climate-change",
        departmentLogo: "ui/img/decc.png"
      },
      defra: {
        departmentName: "the Department for Environment, Food & Rural Affairs",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-environment-food-rural-affairs",
        departmentLogo: "ui/img/defra.png"
      },
      hmrc: {
        departmentName: "HM Revenue & Customs",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=hm-revenue-customs",
        departmentLogo: "ui/img/hmrc.png"
      },
      ho: {
        departmentName: "the Home Office",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=home-office",
        departmentLogo: "ui/img/ho.png"
      },
      dclg: {
        departmentName: "the Department for Communities & Local Government",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-communities-and-local-government",
        departmentLogo: "ui/img/dclg.png"
      },
      dcms: {
        departmentName: "the Department for Culture, Media & Sports",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-culture-media-sport",
        departmentLogo: "ui/img/dcms.png"
      },
      dft: {
        departmentName: "the Department for Transport",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-transport",
        departmentLogo: "ui/img/dft.png"
      },
      dwp: {
        departmentName: "the Department for Work & Pensions",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-work-pensions",
        departmentLogo: "ui/img/dwp.png"
      },
      dh: {
        departmentName: "the Department of Health",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-of-health",
        departmentLogo: "ui/img/dh.png"
      },
      fc: {
        departmentName: "the Forestry Commission",
        departmentLink: "http://www.forestry.gov.uk/",
        departmentLogo: "ui/img/fc.png"
      },
      mod: {
        departmentName: "the Ministry of Defence",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=home-office",
        departmentLogo: "ui/img/mod.png"
      },
      moj: {
        departmentName: "the Ministry of Justice",
        departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=ministry-of-defence",
        departmentLogo: "ui/img/moj.png"
      },
      nhs: {
        departmentName: "the NHS",
        departmentLink: "https://www.gov.uk/government/statistics?departments%5B%5D=national-health-service",
        departmentLogo: "ui/img/nhs.png"
      },
      tis: {
        departmentName: "the Insolvency Service",
        departmentLink: "https://www.gov.uk/government/statistics?departments%5B%5D=insolvency-service",
        departmentLogo: "ui/img/tis.png"
      },
      wg: {
        departmentName: "the Welsh Government",
        departmentLink: "http://wales.gov.uk",
        departmentLogo: "ui/img/wg.png"
      },
      sg: {
        departmentName: "the Scottish Government",
        departmentLink: "http://www.scotland.gov.uk/Topics/Statistics",
        departmentLogo: "ui/img/sg.png"
      },
      nie: {
        departmentName: "the Northern Ireland Executive",
        departmentLink: "http://www.northernireland.gov.uk/",
        departmentLogo: "ui/img/nie.png"
      },
    }

    resolvePaginatorLinkCount()
    watchResize()

    initialize(searchTerm, type, page)

    function initialize(q, type, pageNumber) {
      $scope.pageCount = Math.ceil((searchResponse.contentSearchResult.numberOfResults) / 10)
      $scope.totalCount = searchResponse.contentSearchResult.numberOfResults
      if (searchResponse.homeSearchResult) {
        $scope.totalCount += searchResponse.homeSearchResult.results.length
      }
      resolveFilters(type)
      $scope.searchTermOneTime = q
      if (type) {
        $scope.filterOn = true
      } else {
        $scope.filterOn = false
      }
      resolveRelatedDepartment(q)
      resolveSectionsForDisplay()
    }

    function resolveFilters(type) {
      if (type) {
        if (typeof type === 'string') {
          filters[type] = true
        } else {
          for (var i = 0; i < type.length; i++) {
            filters[type[i]] = true
          };
        }
      }
    }

    function resolveSectionsForDisplay() {
      if (($scope.totalCount > 0 || ($scope.totalCount === 0 && $scope.filterOn)) && !searchResponse.suggestionBasedResult) {
        $scope.showSearchResults = true;
      }

      if (($scope.totalCount > 0 || ($scope.totalCount === 0 && $scope.filterOn)) && searchResponse.suggestionBasedResult) {
        $scope.showSuggestedSearchResults = true;
      }

      if ($scope.totalCount === 0 && !$scope.filterOn) {
        $scope.showZeroResultsFound = true;
        $scope.showTimeseriesSearchSuggest = resolveTimeseriesSuggestion()
      }

      if ($scope.totalCount > 0 || $scope.filterOn) {
        if (filters['timeseries'] || isPrerender()) {
          $scope.showFilters = false;
        } else {
          $scope.showFilters = true;
        }
      }
    }

    //Decides timeseries serach should be suggested or not
    function resolveTimeseriesSuggestion() {
      if (filters['timeseries'] || isPrerender()) {
        return false
      } else {
        if (searchResponse.timeseriesCount > 0) {
          return true
        }
        return false
      }
    }

    function reLoad() {
      // if the results are generated from an autocorrect suggest then
      // we need to reset the query parameter to the suggestion
      if (searchResponse.suggestionBasedResult) {
        PageUtil.goToPage('/search/' + searchResponse.suggestion)
      } else {
        PageUtil.goToPage('/search/' + searchTerm)
      }
      PageUtil.setUrlParam('type', resolveTypes(filters))
    }

    function toggleFilter(type) {
      filters[type] = !filters[type]
      reLoad()
    }

    function resolveTypes(filters) {
      var activeFilters = []
      for (type in filters) {
        if (filters[type]) {
          activeFilters.push(type)
        }
      };
      var result = activeFilters.length > 0 ? activeFilters : null
      console.log(result)
      return result
    }

    function isActive(type) {
      var result = filters[type]
      return result
    }

    function resolveRelatedDepartment(searchTerm) {
      $log.debug("Resolving related department for " + searchTerm)
      var departmentCode
      searchTerm = searchTerm.toLowerCase()

      for (var i = 0; i < departmentHandoff.length; i++) {
        for (var x = 0; x < departmentHandoff[i].length; x++) {
          if (departmentHandoff[i][x] === searchTerm) {
            departmentCode = departmentHandoff[i][0]
            $scope.relatedDepartment = relatedDepartments[departmentCode]
            $log.debug("Department resolved as " + departmentCode)
            return
          }
        }
      }
      $scope.relatedDepartment = undefined
    }

    function watchResize() {
      angular.element($window).bind('resize', function() {
        resolvePaginatorLinkCount()
        $scope.$apply()
      })
    }

    //Resolve max paginator visible link counts depending on screen size
    function resolvePaginatorLinkCount() {
      if ($window.innerWidth < 500) {
        $scope.paginatorLinks = 5
      } else if ($window.innerWidth < 600) {
        $scope.paginatorLinks = 6
      } else if ($window.innerWidth < 700) {
        $scope.paginatorLinks = 7
      } else if ($window.innerWidth < 920) {
        $scope.paginatorLinks = 8
      } else if ($window.innerWidth < 980) {
        $scope.paginatorLinks = 9
      } else {
        $scope.paginatorLinks = 10
      }
    }

    function isShowLozenge(item) {
      return item.type != 'home' && item.type != 'timeseries'
    }

    function isPrerender() {
      return PageUtil.isPrerender()
    }

    function link(url) {
      if (StringUtil.startsWith(url, '/')) {
        return '#!' + url
      } else {
        return url
      }
    }

    //Expose API
    angular.extend($scope, {
      toggleFilter: toggleFilter,
      isActive: isActive,
      isShowLozenge: isShowLozenge,
      isPrerender: isPrerender,
      link: link
    })
  }

})()