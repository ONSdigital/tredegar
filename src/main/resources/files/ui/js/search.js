/**
* ons search
* Jquery based search plugin to search and generated markup for results
*
*/


$.extend({
	search: function(options) {
		var onssearch = $.extend({
			query : '',
			type  : '',
			resultsHolder : '',
			paginatorHolder : '',
			resultInfoHolder : '' ,
			searchInput : '' ,
			page: 0,
			onSearchStart: function () {
				// Callback triggered before searching
			},
			onSearchComplete: function() {
			// Callback triggered when search is complete and markup generated
			}
		}, options || {});


		onssearch.onSearchStart();

		// attach search widget to document body
		$('body').data('onssearch', onssearch);

		//Fill search input with search term
		fillSearchInput();

		
		if (!onssearch.query) {
			createDummyresults();
			return onssearch;
		}

		doSearch();


		return onssearch;
	} 			
});



function fillSearchInput() {
	var onssearch = $('body').data('onssearch');
	if (!onssearch.searchInput) {
		return;
	};

	var searchBox = $('#' + onssearch.searchInput);
	searchBox.val(onssearch.query)
}

function loadPage(pageNumber) {
	var onssearch = $('body').data('onssearch');
	onssearch.page = pageNumber || onssearch.page;
	var onssearch = $('body').data('onssearch');
	var type = onssearch.type ? ("&type=" +onssearch.type) : '';

	clearAll();
	showLoadingMessage();
	$.getJSON("search?q=" + onssearch.query + type + "&page=" + onssearch.page,  function(data) {
			onssearch.data = data;  
			hideLoadingMessage();
		  	buildResultList();
		  }).fail(function() {
		  	showErrorMessage();
		    console.log( "Failed searching for query " + onssearch.query );
	});
}


function doSearch() {
		
		var onssearch = $('body').data('onssearch');
		var type = onssearch.type ? ("&type=" +onssearch.type) : '';
		
		clearAll();
		showLoadingMessage();
		$.getJSON("search?q=" + onssearch.query + type + "&page=" + onssearch.page,  function(data) {
			  	console.log( "Search successful for query " + 	onssearch.query );
				console.log(data);
				onssearch.data = data;  
				hideLoadingMessage();
			  	buildResultList();
			  	buildInfo();
			  	buildPaginator();
			  	onssearch.onSearchComplete();
			  }).fail(function() {
			  	showErrorMessage();
			    console.log( "Failed searching for query " + onssearch.query );
		});
}


function buildResultList() {
	
	var onssearch = $('body').data('onssearch');
	if (!onssearch.resultsHolder) {
		return;
	};
	var resultsContainer = $('#' + onssearch.resultsHolder);

	var results = onssearch.data.results;
	var dl = $("<dl/>"); 
	resultsContainer.append(dl);
		
	for (i = 0; i < results.length; i++) {
			var dt =  $("<dt class=''/>");
			dt.append("<a href=' "  +  results[i].url  + "'>"  +  results[i].title + "</a>");
			dt.append("<span class='lozenge' href='#'>" + results[i].type +  "</span>"  );
			var releaseDate =  $("<dd class='microcopy'>Released ...." + "</dd>");
			var content = $("<dd>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque sit amet diam vel est congue eleifend non eget sem. Nam enim ipsum, vestibulum sed facilisis ut, commodo et nulla. Aliquam erat volutpat. Sed ut aliquet nunc. Ut mollis, arcu non vehicula gravida, lectus magna auctor quam, in rhoncus nibh nisl nec odio. Nam suscipit, dui in maximus porttitor, velit nulla rutrum leo, sit amet porttitor tellus dolor sit amet est. Quisque nulla augue, gravida sit amet mauris vel, congue aliquam urna. Quisque consectetur commodo mi eu pulvinar. Nam egestas magna sem, non sagittis massa vehicula non. Cras ultricies eleifend scelerisque. Pellentesque nunc tellus, commodo eget est nec, molestie elementum nulla. Donec mattis sed nisl in tincidunt. Mauris</dd>");

   	 	dl.append(dt);
   	 	dl.append(releaseDate);
   	 	dl.append(content);
	}	
}

function buildPaginator() {

	var onssearch = $('body').data('onssearch');
	if (!onssearch.paginatorHolder) {
		return;
	};	
	var paginatorContainer = $('#' + onssearch.paginatorHolder);

    $(paginatorContainer).pagination({
        items: onssearch.data.numberOfResults,
        itemsOnPage: 10,
        displayedPages: 10,
        currentPage : onssearch.page,
        edges: 0,
        cssStyle: "pagination",
        hideIfSinglePage : true,
        hideNextOnLastPage : true,
        hidePrevOnFirstPage : true,
		onPageClick: function (pageNumber) {
			loadPage(pageNumber);		
		}
    });
}

function buildInfo() {

	var onssearch = $('body').data('onssearch');
	if (!onssearch.resultInfoHolder) {
		return;
	};	

	var infoContainer = $('#' + onssearch.resultInfoHolder);

	var resultNum = $("<span class='results-total'>" + onssearch.data.numberOfResults + "</span>");
	infoContainer.append(resultNum);
	infoContainer.append(" results for ");
	infoContainer.append($("<span class='results-search-term'>\'" + onssearch.query + "\'</span>"));
}


function clearAll() {
	var onssearch = $('body').data('onssearch');
	var infoContainer = $('#' + onssearch.resultInfoHolder);
	var resultsContainer = $('#' + onssearch.resultsHolder);

	if (infoContainer) {
		infoContainer.html('');	
	}
	if (resultsContainer) {
		resultsContainer.html('');	
	}
}

function showLoadingMessage() {
	var onssearch = $('body').data('onssearch');
	var infoContainer = $('#' + onssearch.resultInfoHolder);
	infoContainer.html('<b style="font-size:22px">Loading...</b>');
}

function hideLoadingMessage() {
	var onssearch = $('body').data('onssearch');
	var infoContainer = $('#' + onssearch.resultInfoHolder);
	infoContainer.html('');
}

function showErrorMessage() {
	clearAll();
	var onssearch = $('body').data('onssearch');
	var resultsContainer = $('#' + onssearch.resultsHolder);
	resultsContainer.html('<b style="color:red;font-size:20px">Oops!! Search failed!<b>');
}

function  createDummyresults() {
	
	var onssearch = $('body').data('onssearch');

	var data={
		took:10,
		numberOfResults:1004,
		results:new Array()
	}
	
	for (i=0; i<10;i++) {
		data.results[i] = { title: "Title" + i, tags: "Tag" + i, type: "dummy" } 	
	}	

	onssearch.data = data;
	buildResultList();
	buildPaginator();
	buildInfo();
	onssearch.onSearchComplete();
}