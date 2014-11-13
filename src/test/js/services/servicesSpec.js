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
  
  // empty array
  it('check that array has elements', inject(function(Chart) {
	  var emptyArray = ['anElement']
	  var result = Chart.isNotEmpty(emptyArray)
      expect(result).toBe(true);
    }));  
  
  // empty array
  it('check for empty array', inject(function(Chart) {
	  var emptyArray = []
	  var result = Chart.isNotEmpty(emptyArray)
      expect(result).toBe(false);
    }));
});