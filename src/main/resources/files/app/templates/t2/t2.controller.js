(function() {

  angular.module('onsTemplates')
    .controller('T2Controller', ['Page', T2Controller])

  function T2Controller(Page) {
    init()
    function init() {
      Page.setTitle('Home')
    }
  }
})()