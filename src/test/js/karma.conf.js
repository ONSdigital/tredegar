module.exports = function(config){
  config.set({

    basePath : '../../../',

    files : [
      'src/main/resources/files/ui/js/lib/angular.js', 
      'src/main/resources/files/ui/js/lib/highcharts.js',
      'src/main/resources/files/ui/js/lib/highcharts-ng.js',             
      'src/main/resources/files/ui/js/lib/angular-route.min.js',
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
            'karma-jasmine'
            ],

    junitReporter : {
      outputFile: 'test_out/unit.xml',
      suite: 'unit'
    }

  });
};