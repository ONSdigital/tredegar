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

	// data enrichment
	it('check if timeseries was enriched', inject(function(Chart) {
		var timeseries = {}
		var date = '1st March'
		timeseries.value = 1999
		timeseries.date = date
		timeseries.quarter = 'Q3'
		timeseries.month = 'FEB'
		timeseries.year = '2014'
			
		var result = Chart.enrichData(timeseries)
		expect(result.y).toBe(1999);
		// value is built up by combining year+quarter+month
		expect(result.value).toBe(201432);
		expect(result.name).toBe(date);
	}));
	
	// data enrichment with some empty values
	it('check if timeseries was enriched', inject(function(Chart) {
		var timeseries = {}
		timeseries.year = '2013'
		var result = Chart.enrichData(timeseries)
		expect(result.value).toBe(2013);
	}));	

});