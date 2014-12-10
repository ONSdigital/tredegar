module.exports = function (grunt) {
 
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
 
        clean: ["dist", '.tmp'],
 
        copy: {
            main: {
                expand: true,
                cwd: 'app/',
                src: ['app/**/*.js', 'ui/js/lib/*.js' ],
                dest: 'dist/'
            }
        },
 
        rev: {
            files: {
                src: ['dist/**/*.{js}']
            }
        },
 
        useminPrepare: {
            html: 'index.html'
        },
 
        usemin: {
            html: ['dist/index.html']
        },
 
 
		ngAnnotate: {
		  dist: {
			files: [{
			  expand: true,
			  cwd: '.tmp/concat',
			  src: '*/**.js',
			  dest: '.tmp/concat'
			}]
		  }
		},
 
        uglify: {
            options: {
                report: 'min',
                mangle: false
            }
        }
    });
 
 
	require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);
 
    // Tell Grunt what to do when we type "grunt" into the terminal
    grunt.registerTask('default', [
        'copy' , 'useminPrepare', 'concat', 'ngAnnotate', 'uglify', 'usemin'
    ]);
};
