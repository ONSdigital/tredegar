$(document).on('ready', function() {
  $('.supporting-content__title').on('click', function() {
    $(this).parent('.supporting-content').toggleClass('is-expanded is-collapsed');
  });
});
