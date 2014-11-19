module.exports = function(config){
  config.set({

    basePath : '../../../',

    files : [
      'src/main/resources/files/ui/js/lib/angular.js', 
      'src/main/resources/files/ui/js/lib/highcharts.js',
      'src/main/resources/files/ui/js/lib/highcharts-ng.js',             
      'src/main/resources/files/ui/js/lib/angular-route.min.js',
      'src/main/resources/files/ui/js/lib/angular-cache.min.js',      
      'src/main/resources/files/ui/js/lib/loading-bar.js',
      'src/main/resources/files/ui/js/lib/smart-table.min.js',
      'node_modules/angular-sanitize/angular-sanitize.js',
      'node_modules/angular-mocks/angular-mocks.js',
      'src/main/resources/files/app/**/*.js',
      'src/test/js/**/*.js'
    ],

    autoWatch : true,

    frameworks: ['jasmine'],

    browsers : ['Chrome'],

    plugins : [
            'karma-chrome-launcher',
            'karma-jasmine',
            'karma-coverage'
            ],

    junitReporter : {
      outputFile: 'test_out/unit.xml',
      suite: 'unit'
    },
  
	  // coverage reporter generates the coverage
	  reporters: ['progress', 'coverage'],
	
	  preprocessors: {
	    // source files, that you wanna generate coverage for
	    // do not include tests or libraries
	    // (these files will be instrumented by Istanbul)
	    'src/main/resources/files/app/**/*.js': ['coverage']
	  },
	
	  // optionally, configure the reporter
	  coverageReporter: {
	    type : 'html',
	    dir : 'coverage/'
	  }  

  });
};