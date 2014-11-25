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