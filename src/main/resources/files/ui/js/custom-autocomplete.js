$.widget( "ui.autocomplete", $.ui.autocomplete, {
	_renderMenu: function( ul, items ) {
		var that = this,
	    count = 0;
	    $.each( items, function( index, item ) {
	        if ( count < that.options.maxItems ) {
	            that._renderItemData( ul, item );
	        }
	        count++;
	    });
	} 
});  
  
$(function() {
	$( "#homepageSearch" ).autocomplete({
      	source: function(request, response) {
    	  	var URL = "/search";
    	  	$.getJSON(URL, request, function(data) {

    	  		var results = data.results;
            	$.each(results, function (_, item) {
                	item.title = item.title;
                	item.path = item.path;
                	item.url = item.url;
            	});

            response(results);
     	 	});
    	},
	    minLength: 3,
	    maxItems: 5,
	    focus: function( event, ui ) {
	    	$( "#homepageSearch" ).val( ui.item.label );
	        return false;
	    },
        select: function (event, ui) {
            window.open(ui.item.url);
        }
    })
    .autocomplete( "instance" )._renderItem = function( dl, item ) {
      return $( "<dl>" )
        .append( "<a>" + item.title + "<br>" + item.path + "</a>" )
        .appendTo( dl );
    };
});  
