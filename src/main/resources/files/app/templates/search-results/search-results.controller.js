//Search ctrl, used for search results page
"use strict";

(function() {

  angular.module("onsTemplates")
    .controller("SearchController", ['$scope', '$location', '$log', 'DataLoader', 'PageUtil', SearchController])


  function SearchController($scope, $location, $log, DataLoader, PageUtil) {
    var page = PageUtil.getUrlParam("page")
    var searchTerm = $scope.searchTerm = PageUtil.getUrlParam("q")
    var type = $scope.type = PageUtil.getUrlParam("type")
    var departmentHandoff = [
      ["dfe", "education", "gcse", "a level", "degree", "nvq", "school", "college", "university", "national curriculum", "qualification"],
      ["bis", "business", "apprenticeship", "building", "construction", "higher education", "insolvency", "trade union"],
      ["decc", "energy", "solar", "coal", "oil", "gas", "electricity", "fuel poverty"],
      ["defra", "agriculture", "farming", "air quality", "emissions", "recycling", "food"],
      ["hmrc", "tax", "benefit", "paye", "national insurance", "vat"],
      ["ho", "arrests", "asylum", "immigration", "firearm", "crime", "visa", "police", "terrorism"],
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
      }
    }

    if (!searchTerm) {
      return
    }

    if (!page) {
      page = 1
    }

    search(searchTerm, type, page)

    function isLoading() {
      return ($scope.searchTerm && !$scope.searchResponse)
    }

    function search(q, type, pageNumber) {
      var searchString = "?q=" + q + (type ? "&type=" + type : "") + "&page=" + pageNumber
      DataLoader.load("/search" + searchString)
        .then(function(data) {
          $scope.searchResponse = data
          $scope.pageCount = Math.ceil(data.numberOfResults / 10)
          $scope.searchTermOneTime = q
          resolveRelatedDepartment(q)
        })
    }

    function filter(type) {
      //Clear page parameter if any
      $location.search('page', null)
      $location.search('type', type)
      // if the results are generated from an autocorrect suggest then
      // we need to reset the query parameter to the suggestion
      if ($scope.searchResponse.suggestionBasedResult) {
    	  $location.search('q', $scope.searchResponse.suggestion)
      }
    }

    function isActive(type) {
      var result = PageUtil.getUrlParam("type") === type
      return result
    }

    function resolveRelatedDepartment(searchTerm) {
      $log.debug("Resolving related department for " + searchTerm)
      var departmentCode
      var searchTerm = searchTerm.toLowerCase()

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

    //Expose API
    angular.extend($scope, {
      isLoading:isLoading,
      filter:filter,
      isActive:isActive
    })
  }

})()