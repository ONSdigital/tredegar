$(document).on('ready', function() {
  var lozenges = $('.lozenge--expandable');

  lozenges.addClass('is-collapsed');

  lozenges.on('click', function() {
    // Collapsation by click event should override hover events
    // Hence the double classes. Do not use .js-click-collapsed for styling.
    if ($(this).is('.is-collapsed, .js-click-collapsed')) {
      $(this).removeClass('is-collapsed js-click-collapsed');
    }
    else {
      $(this).addClass('js-click-collapsed');
    }
  });

  lozenges.mouseenter(function() {
    if (!$(this).hasClass('js-click-collapsed')) {
      $(this).removeClass('is-collapsed');
    }
  });

  lozenges.mouseleave(function() {
    if (!$(this).hasClass('js-click-collapsed')) {
      $(this).addClass('is-collapsed');
    }
  });
});
