(function() {
  'use strict';

  angular.module('onsTemplates')
    .controller('T1Controller', ['Page', T1Controller])

  function T1Controller(Page) {
    var ctrl = this
    init()

    function init() {
      Page.setTitle('Home')
    }
  }

})()