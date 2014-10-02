$.widget( "ui.autocomplete", $.ui.autocomplete, {
	_renderMenu: function( dl, items ) {
		var that = this,
	    count = 0;
	    $.each( items, function( index, item ) {
	        if ( count < that.options.maxItems ) {
	            that._renderItemData( dl, item );
	        }
	        count++;
	    });
	},
});  
  
$(function() {
	$("#homepageSearch").autocomplete({
	   open: function() { 
		    // keep width of results linked to search field
			var width = $(".ui-autocomplete-input").innerWidth();
			$('.ui-menu').css('max-width',auto);
	    }, 
		source : function(request, response) {
			var URL = "/search";
			$.getJSON(URL, request, function(data) {

				// total number of matches will/could differ from the
				// actual search results because they are batched for pagination
				var numberOfResults = data.numberOfResults
				// Search returns first 10
				var firstTenResults = data.results;
				var searchTerm = $("#homepageSearch").val();
				// default index in array to put the count of results
				var searchResultsIndex = 5;
				var firstTenResultsCount = firstTenResults.length;

				// if we don't have 5 results then add count to end of array
				if (firstTenResultsCount < 5) {
					searchResultsIndex = firstTenResultsCount;
				}

				// if no results then just output a message
				if (firstTenResultsCount == 0) {
					firstTenResults[searchResultsIndex] = {
						title : 'No results found',
						path : ''
					};
				// otherwise add count at end of array
				} else {
					firstTenResults[searchResultsIndex] = {
						title : 'See all ' + numberOfResults + ' results',
						path : '',
						url : '/searchresults.html?q=' + searchTerm
					};
				}

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
	    maxItems: 6,
	    focus: function( event, ui ) {
	    	$( "#homepageSearch" ).val( ui.item.label );
	        return false;
	    },
        select: function (event, ui) {
        	if (ui.item.title != 'No results found') {
        		window.open(ui.item.url);
        	}
        }
    })
    .autocomplete( "instance" )._renderItem = function( dt, item ) {
		var contentType;
		switch (item.type) {
			case "home":
				contentType = "<span class='lozenge lozenge-spacer lozenge-grey'>HOME</span>";
				break;
			case "bulletins":
				contentType = "<span class='lozenge lozenge-spacer lozenge-blue'>STATISTICAL BULLETIN</span>";
				break;
			case "datasets":
				contentType = "<span class='lozenge lozenge-spacer lozenge-red'>DATASET</span>";
				break;
			case "articles":
				contentType = "<span class='lozenge lozenge-spacer'>ARTICLE</span>";
				break;
			case "methodology":
				contentType = "<span class='lozenge lozenge-spacer lozenge-green'>METHODOLOGY</span>";
				break;
			default:
				contentType = "";
		}
		
      return $( "<dt>" )
        .append( "<a>" 
        		+ contentType 
        		+ item.title 
        		+ "</a>" )
        .appendTo( dt );
    };
});  
