'use strict';

// checks that all services are still available
describe('services', function() {

  // load modules
  beforeEach(function() {
	  angular.mock.module('onsApp')
	  angular.mock.module('onsUtils')
  })
  
  it('check the existence of Chart service', inject(function(Chart) {
      expect(Chart).toBeDefined();
    }));
  
  it('check the existence of ArrayUtil service', inject(function(ArrayUtil) {
      expect(ArrayUtil).toBeDefined();
    }));
});