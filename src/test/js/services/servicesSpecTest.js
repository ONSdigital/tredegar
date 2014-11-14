'use strict';

describe('services', function() {

  // load modules
  beforeEach(function() {
	  angular.mock.module('onsApp')
  })
  
  // Test service availability
  it('check the existence of Chart service', inject(function(Chart) {
      expect(Chart).toBeDefined();
    }));
});