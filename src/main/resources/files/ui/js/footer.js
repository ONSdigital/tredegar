$(document).ready(function() {

  $('body').addClass('js');

  $('.tooltip').tooltipster({
    'maxWidth': 270
  });

  var placeHolderConfig = {
    className: 'placeholder-polyfill'
  };

});

$(document).ready(function() {

  $('.content-reveal').each(function() {
    var clone = $(this).find('.content-reveal__action')
                  .clone()
                    .addClass('content-reveal__action--clone')
                    .text('See less');
    $(this).append(clone);
  });

  $('[data-content-reveal]:not(.content-reveal__action--clone)')
    .removeClass('hidden');

  $('.content-reveal__hidden').addClass('hidden');

  $('[data-content-reveal]').on('click', function() {
    var activatedToggleLink = $(this),
        contentReveal = activatedToggleLink.parents('.content-reveal'),
        allToggleLinks = contentReveal.find('.content-reveal__action');

    // Toggle the display of link that wasn't clicked
    allToggleLinks.removeClass('hidden');
    activatedToggleLink.addClass('hidden');

    contentReveal.find('.content-reveal__hidden').toggleClass('hidden');
  });
});

$(document).on('ready', function() {
  $('.footnote-body').on('click', function(e) {
    var footnotesSection = $('.accordion--footnotes');
    if (footnotesSection.hasClass('is-collapsed')) {
      footnotesSection.removeClass('is-collapsed').addClass('is-expanded');
    }
  });
});

$(document).ready(function() {
  $('.js-expandable').on('click' ,function(event) {
    event.preventDefault();
    $(this)
      .toggleClass('js-expandable-active')
      .find('.js-expandable__content').toggleClass('js-nav-hidden');
  });


  // The following function to enable focus-tabbing through menu graciously
  // taken from Simply Accessible — thank you kindly.
  // http://simplyaccessible.com/article/better-for-accessibility

  $(function(){
    $('#nav-primary').setup_navigation();
  });

  $.fn.setup_navigation = function(settings) {
    settings = jQuery.extend({
      focusClass: 'menu-focus',
    }, settings);

    // Set tabIndex to -1 so that links can't receive focus until menu is open
    $(this).find('> li > a').next('ul').find('a').attr('tabIndex',-1);

    $(this).find('> li > a').hover(function(){
      $(this).closest('ul')
        .find('.'+settings.focusClass).removeClass(settings.focusClass)
        .find('a').attr('tabIndex',-1);
    });
    $(this).find('> li > a').focus(function(){
      $(this).closest('ul')
        .find('.'+settings.focusClass).removeClass(settings.focusClass)
        .find('a').attr('tabIndex',-1);
      $(this).next('ul')
        .addClass(settings.focusClass)
        .find('a').attr('tabIndex',0);
    });

    // Hide menu if click or focus occurs outside of navigation
    $(this).find('a').last().keydown(function(e){
      if(e.keyCode === 9) {
        // If the user tabs out of the navigation hide all menus
        $('.'+settings.focusClass)
        .removeClass(settings.focusClass)
        .find('a').attr('tabIndex',-1);
      }
    });
    $(document).click(function(){
      $('.'+settings.focusClass)
      .removeClass(settings.focusClass)
      .find('a').attr('tabIndex',-1);
    });

    $(this).click(function(e){
      e.stopPropagation();
    });
  };

});

$(document).on('ready', function() {

  // set up ARIA roles
  $('.accordion-container')
    .attr('role', 'tablist')
    .attr('aria-multiselectable', 'true');

  $('.accordion__title')
    .attr('role', 'tab')
    .attr('tabindex', '0')
    .attr('aria-selected', 'false');

  $('.accordion__content')
    .attr('role', 'tabpanel');

  $('.accordion.is-collapsed > .accordion__content')
    .attr('aria-expanded', 'true');

  $('.accordion.is-expanded > .accordion__content')
    .attr('aria-expanded', 'false');

  $('.accordion__title').on('click', function() {
    toggleAccordion($(this));
  });

  $('.accordion__title').on('keydown', function(e) {
    // Listen for space / enter, respectively
    if (e.keyCode === 32 || e.keyCode === 13) {
      toggleAccordion($(this));
    }

    // left arrow / up arrow
    if (e.keyCode === 37 || e.keyCode === 38) {
      $(this).parent('.accordion')
        .prev('.accordion')
        .find('.accordion__title')
        .focus();
    }

    // right arrow / down arrow
    if (e.keyCode === 39 || e.keyCode === 40) {
      $(this).parent('.accordion')
        .next('.accordion')
        .find('.accordion__title')
        .focus();
    }
  });

  function toggleAccordion(element) {
    var $accordionContainer = element.parent('.accordion');
    $accordionContainer.toggleClass('is-expanded is-collapsed');
    if ($accordionContainer.hasClass('is-expanded')) {
      element.attr('aria-selected', true);
      element.siblings('.accordion__content').attr('aria-expanded', false);
    }
    else {
      element.attr('aria-selected', false);
      element.siblings('.accordion__content').attr('aria-expanded', true);
    }
  }
});

$(document).ready(function() {

  // Function to check select box associated with table rows
  $( '.table--selectable' ).on( 'click', '.list--table__body', function(e) {

    // target clicks on selectable table that's not a link or an (i)
    if ($(e.target).not('a, .information > span, .list--table__item__body--select, input[type="checkbox"]').length) {
      $(this).find('input').click();
    }
  });

  $( '.table--selectable' ).on( 'touchend', '.list--table__body', function(e) {
    // prevent hover state on mobile from being triggered from a tap on the body
    $(this).addClass('hover-override');
  });

  // Listens to select all functionality
  $( '.list--table__item__body--select-all input' ).change(function() {
    var checkboxes = $(this).closest('li').siblings().find('input[type=checkbox]');
    if (this.checked) {
      checkboxes.prop('checked', true);
    }
    else {
      checkboxes.prop('checked', false);
    }
  });

  // Programmatically determine the state of select all button when triggering checkboxes
  $( '.table--selectable .list--table__body' ).on( 'click', 'input[type=checkbox]', function(e) {
    var checkboxes = $(this).closest('ol').find('.list--table__body input[type=checkbox]');

    if (checkboxes.length !== checkboxes.filter(':checked').length) {
      $('.list--table__item__body--select-all input').prop('checked', false);
    }
    else {
      $('.list--table__item__body--select-all input').prop('checked', true);
    }
  });
});

$(document).ready(function() {

  $('.tablesaw-columntoggle-btn').on('click', function(e) {
    $(this).toggleClass('is-active');
  });

  /*
   * When user clicks on the dialog background, trigger a click
   * event on the close icon.
   */
  $('.dialog-background').on('click', function() {
    $('.tablesaw-columntoggle-btn').trigger('click');
  });

});
$(document).ready(function() {
  // many thanks to Heydon Pickering for this code
  // http://heydonworks.com/practical_aria_examples/#tab-interface

  // The class for the container div

  var $container = '.tab-pane';

  // The setup

  $($container +' ul').attr('role','tablist').addClass('nav tab-pane__tabs-container').removeClass('nav--block--spaced');
  $($container +' [role="tablist"] li').attr('role','presentation').addClass('tab-pane__tab-container');
  $('[role="tablist"] a').attr({
    'role' : 'tab',
    'tabindex' : '-1'
  }).addClass('tab-pane__tab');

  // Make each aria-controls correspond id of targeted section (re href)

  $($container + ' [role="tablist"] a').each(function() {
    $(this).attr(
      'aria-controls', $(this).attr('href').substring(1)
    );
  });

  // Make the first tab selected by default and allow it focus

  $($container + ' [role="tablist"] li:first-child a').attr({
    'aria-selected' : 'true',
    'tabindex' : '0'
  }).addClass('tab-pane__tab--selected');

  // Make each section focusable and give it the tabpanel role

  $($container +' section').attr({
    'role' : 'tabpanel'
  }).addClass('tab-pane__panel');

  // Make all but the first section hidden (ARIA state and display CSS)

  $($container + ' [role="tabpanel"]:not(:first-of-type)').attr({
    'aria-hidden' : 'true'
  }).addClass('tab-pane__tab--inactive');


  // Change focus between tabs with arrow keys

  $($container + ' [role="tab"]').on('keydown', function(e) {

    // define current, previous and next (possible) tabs

    var $original = $(this);
    var $prev = $(this).parents('li').prev().children('[role="tab"]');
    var $next = $(this).parents('li').next().children('[role="tab"]');
    var $target;

    // find the direction (prev or next)

    switch (e.keyCode) {
      case 37:
        $target = $prev;
        break;
      case 39:
        $target = $next;
        break;
      default:
        $target = false;
        break;
    }

    if ($target.length) {
        $original.attr({
          'tabindex' : '-1',
          'aria-selected' : null
        }).removeClass('tab-pane__tab--selected');
        $target.attr({
          'tabindex' : '0',
          'aria-selected' : true
        }).addClass('tab-pane__tab--selected').focus();
    }

    // Hide panels

    $($container +' [role="tabpanel"]')
      .attr('aria-hidden', 'true')
      .addClass('tab-pane__tab--inactive');

    // Show panel which corresponds to target

    $('#' + $(document.activeElement).attr('href').substring(1))
      .attr('aria-hidden', null)
      .removeClass('tab-pane__tab--inactive');
  });

  // Handle click on tab to show + focus tabpanel

  $($container + ' [role="tab"]').on('click', function(e) {

    e.preventDefault();

    // remove focusability [sic] and aria-selected

    $($container + ' [role="tab"]').attr({
      'tabindex': '-1',
      'aria-selected' : null
      }).removeClass('tab-pane__tab--selected');

    // replace above on clicked tab

    $(this).attr({
      'aria-selected' : true,
      'tabindex' : '0'
    }).addClass('tab-pane__tab--selected');

    // Hide panels

    $($container +' [role="tabpanel"]').attr('aria-hidden', 'true').addClass('tab-pane__tab--inactive');

    // show corresponding panel

    $('#' + $(this).attr('href').substring(1))
      .attr('aria-hidden', null)
      .removeClass('tab-pane__tab--inactive');
  });

});
