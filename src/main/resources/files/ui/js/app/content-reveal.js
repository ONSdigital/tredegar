$(document).ready(function() {
  $('.content-reveal__hidden').addClass('hidden');
  $('.content-reveal__action').removeClass('hidden').on('click', function() {
    var hidden = $(this).parents('.content-reveal').find('.content-reveal__hidden');
    hidden.toggleClass('hidden');
    if (hidden.hasClass('hidden')) {
      $(this).text('See more');
    }
    else {
      $(this).text('See less');
    }
  });
});
