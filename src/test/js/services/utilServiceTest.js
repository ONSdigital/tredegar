'use strict';

describe('services', function() {

  // load modules
  beforeEach(function() {
	  angular.mock.module('onsUtils')
  })
  
  // is not empty array
  it('check that array has elements', inject(function(ArrayUtil) {
	  var emptyArray = ['anElement']
	  var result = ArrayUtil.isNotEmpty(emptyArray)
      expect(result).toBe(true);
    }));  
  
  // empty array
  it('check for empty array', inject(function(ArrayUtil) {
	  var emptyArray = []
	  var result = ArrayUtil.isNotEmpty(emptyArray)
      expect(result).toBe(false);
    }));
  
  // first item in array
  it('check for first item in array', inject(function(ArrayUtil) {
	  var array = [8, 4, 7, 5]
	  var result = ArrayUtil.getFirst(array)
      expect(result).toBe(8);
    }));  
  
  // last item in array
  it('check for last item in array', inject(function(ArrayUtil) {
	  var array = [8, 4, 7, 5]
	  var result = ArrayUtil.getLast(array)
      expect(result).toBe(5);
    })); 
});