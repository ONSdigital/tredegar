$(document).ready(function() {
  $('body').addClass('js');

  $(document).ready(function() {
    $('.tooltip').tooltipster({
      'maxWidth': 270
    });
  });

  var placeHolderConfig = {
    className: 'placeholder-polyfill'
  };
  Modernizr.load({
    test: Modernizr.input.placeholder,
    nope: [
            '/ui/css/lib/placeholder_polyfill.min.css',
            '/ui/js/lib/jquery.placeholder_polyfill.combo.min.js'
          ]
  });

});
