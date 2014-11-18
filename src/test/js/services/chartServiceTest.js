'use strict';

describe('services', function() {

  // load modules
  beforeEach(function() {
	  angular.mock.module('onsApp')
  })
  
  // quarter values
  it('check quarter values are returned', inject(function(Chart) {
	  var result = Chart.quarterVal('Q1')
      expect(result).toBe(1);
    }));
  
  // quarter values
  it('check quarter values are returned', inject(function(Chart) {
	  var result = Chart.quarterVal('Q2')
      expect(result).toBe(2);
    }));
  
  // quarter values
  it('check quarter values are returned', inject(function(Chart) {
	  var result = Chart.quarterVal('Q3')
      expect(result).toBe(3);
    }));  
  
  // quarter values
  it('check quarter values are returned', inject(function(Chart) {
	  var result = Chart.quarterVal('Q4')
      expect(result).toBe(4);
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