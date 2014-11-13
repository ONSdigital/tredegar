describe('services', function() {

  // load modules
  beforeEach(function() {
	  angular.mock.module('onsApp')
  })
  
  // Test service availability
  it('check the existence of Chart service', inject(function(Chart) {
      expect(Chart).toBeDefined();
    }));
  
  // is not empty array
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
  
  // first item in array
  it('check for first item in array', inject(function(Chart) {
	  var array = [8, 4, 7, 5]
	  var result = Chart.getFirst(array)
      expect(result).toBe(8);
    }));  
  
  // last item in array
  it('check for last item in array', inject(function(Chart) {
	  var array = [8, 4, 7, 5]
	  var result = Chart.getLast(array)
      expect(result).toBe(5);
    })); 
  
  // quarter values
  it('check quarter values are returned', inject(function(Chart) {
	  var result = Chart.quarterVal('Q3')
      expect(result).toBe(3);
    }));
  
  // quarter values exception handling
  it('check default if unknown Q format', inject(function(Chart) {
	  expect(function() {
		  Chart.quarterVal('K11')
	  }).toThrow('Invalid Quarter:K11')
    }));  
  
  // monthly values
  it('check monthly values are returned', inject(function(Chart) {
	  var result = Chart.monthVal('JANUARY')
      expect(result).toBe(1);
    }));
  
  // monthly values exception handling
  it('check default if invalid month', inject(function(Chart) {
	  expect(function() {
		  Chart.quarterVal('JAMUARY')
	  }).toThrow('Invalid Quarter:JAMUARY')
    }));
  
  
});