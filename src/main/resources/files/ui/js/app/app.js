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




// create an observer instance
  var observer = new MutationObserver(function(mutations) {
    mutations.forEach(function(mutation) {
      console.log(mutation);
    });
  });

// configuration of the observer:
  var config = { attributes: true, childList: true, characterData: true };

  $('.icon--inline').each(function() {

    observer.observe($(this)[0], config);
  });

// pass in the target node, as well as the observer options


});
