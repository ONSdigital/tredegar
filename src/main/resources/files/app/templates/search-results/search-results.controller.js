//Search ctrl, used for search results page
"use strict";

(function(){

angular.module("onsTemplates")
  .controller("SearchCtrl", ["$scope", "$location", "$http",SearchController])


  function SearchController($scope, $location, $http) {
      var page = getUrlParam("page")
      var searchTerm = $scope.searchTerm = getUrlParam("q")
      var type = $scope.type = getUrlParam("type")
      var departmentHandoff = [
                                  ["dfe", "education", "gcse","a level","degree","nvq","school","college","university","national curriculum","qualification"],
                                  ["bis","business","apprenticeship","building","construction","higher education","insolvency","trade union"],
                                  ["decc","energy","solar","coal","oil","gas","electricity","fuel poverty"],
                                  ["defra","agriculture","farming","air quality","emissions","recycling","food"],
                                  ["hmrc","tax","benefit","paye","national insurance","vat"],
                                  ["ho","arrests","asylum","immigration","firearm","crime","visa","police","terrorism"],
                                ];
      var relatedDepartments= {
        dfe: {
          departmentName: "Department for Education",
          departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-education",
          departmentLogo: "ui/img/dfe.png"
        },
        bis: {
          departmentName: "Department for Business, Innovation & Skills",
          departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-business-innovation-skills",
          departmentLogo: "ui/img/bis.png"
        },
        decc: {
          departmentName: "Department of Energy & Climate Change",
          departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-of-energy-climate-change",
          departmentLogo: "ui/img/decc.png"
        },
        defra: {
          departmentName: "Department for Environment, Food & Rural Affairs",
          departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=department-for-environment-food-rural-affairs",
          departmentLogo: "ui/img/defra.png"
        },
        hmrc: {
          departmentName: "HM Revenue & Customs ",
          departmentLink: "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=hm-revenue-customs",
          departmentLogo: "ui/img/hmrc.png"
        },
        ho: {
          departmentName: "Home Office",
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

      search(searchTerm, type, page, function(data) {
        $scope.searchTermOneTime = searchTerm
        resolveRelatedDepartment(searchTerm)
      })

      $scope.isLoading = function() {
        return ($scope.searchTerm && !$scope.searchResponse)
      }

      function search(q, type, pageNumber, callback) {
        var searchString = "?q=" + q + (type ? "&type=" + type : "") + "&page=" + pageNumber
        getData("/search" + searchString, function(data) {
          $scope.searchResponse = data
          $scope.pageCount = Math.ceil(data.numberOfResults / 10)
          callback(data)
        })
      }

      function getData(path, callback) {
        console.log("Loading data at " + path)
        $http.get(path).success(callback)
      }

      function getUrlParam(paramName) {
        var params = $location.search()
        return params[paramName]
      }

      function resolveRelatedDepartment(searchTerm) {
       console.log("Resolving related department for " + searchTerm)
       var departmentCode
       var searchTerm =  searchTerm.toLowerCase()

        for(var i = 0; i < departmentHandoff.length; i++){
          for(var x = 0; x < departmentHandoff[i].length; x++){
            if(departmentHandoff[i][x] === searchTerm){
              departmentCode =  departmentHandoff[i][0]
              $scope.relatedDepartment = relatedDepartments[departmentCode]
              console.log("Department resolved as " + departmentCode)
              return
           }
         }
        }
        $scope.relatedDepartment = undefined       
      }
    }

})()