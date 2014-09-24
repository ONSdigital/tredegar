var onssearch = {

};	
	
onssearch.search = function(query, contentHolderId, paginatorId, page) {
		onssearch.query = query;		
		onssearch.contentHolder = $("#" + contentHolderId);
		onssearch.paginator = $("#" + paginatorId);
		onssearch.page = page || 1;
		
		if (!query) {
					createDummyresults(onssearch.contentHolder, onssearch.paginator);
					return;
		}		
		
		$.getJSON("search?q=" + query + "&page=" + onssearch.page,   function(data) {
		  	console.log( "Search successful for query " + 	query );
			console.log(data);
			onssearch.data = data;  
		  	buildResultList(data.results, onssearch.contentHolder);
			buildPaginator(onssearch.data.numberOfResults, onssearch.paginator, onssearch.page);
		  }).fail(function() {
		    console.log( "Failed searching for query " + keyword );
		});
				 
}

onssearch.loadPage = function (pageNumber) {
	doSearch(onssearch.query, pageNumber);
}


function doSearch(query, page) {
		
		if (!query) {
					createDummyresults(onssearch.contentHolder, onssearch.paginator);
					return;
		}		
		
	onssearch.page = page || 1;			
	
	$.getJSON("search?q=" + query + "&page=" + onssearch.page,   function(data) {
		  	console.log( "Search successful for query " + 	query );
			console.log(data);
			onssearch.data = data;  
		  	buildResultList(data.results, onssearch.contentHolder);
		  }).fail(function() {
		    console.log( "Failed searching for query " + keyword );
		});
}


function buildResultList(results, holder) {
	
	clearDiv(holder);	
	
	var dl = $("<dl/>"); 
	holder.append(dl);
		
	for (i = 0; i < results.length; i++) {
			var dt =  $("<dt class=''/>");
			dt.append("<a href='#' >" +  results[i].title + "</a>");
			dt.append("<span class='lozenge' href='#'>" + results[i].type +  "</span>"  );
			var releaseDate =  $("<dd class='microcopy'>Released ...." + "</dd>");
			var content = $("<dd>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque sit amet diam vel est congue eleifend non eget sem. Nam enim ipsum, vestibulum sed facilisis ut, commodo et nulla. Aliquam erat volutpat. Sed ut aliquet nunc. Ut mollis, arcu non vehicula gravida, lectus magna auctor quam, in rhoncus nibh nisl nec odio. Nam suscipit, dui in maximus porttitor, velit nulla rutrum leo, sit amet porttitor tellus dolor sit amet est. Quisque nulla augue, gravida sit amet mauris vel, congue aliquam urna. Quisque consectetur commodo mi eu pulvinar. Nam egestas magna sem, non sagittis massa vehicula non. Cras ultricies eleifend scelerisque. Pellentesque nunc tellus, commodo eget est nec, molestie elementum nulla. Donec mattis sed nisl in tincidunt. Mauris</dd>");

   	 	dl.append(dt);
   	 	dl.append(releaseDate);
   	 	dl.append(content);
	}	
}

function buildPaginator(numberOfResults, holder, page) {
    $(holder).pagination({
        items: numberOfResults,
        itemsOnPage: 10,
        displayedPages: 10,
        currentPage : page,
        edges: 0,
		onPageClick: function (pageNumber) {
			onssearch.loadPage(pageNumber);		
		}
    });
}


function buildDummyPaginator(numberOfResults, holder, page) {
    $(holder).pagination({
        items: numberOfResults,
        itemsOnPage: 10,
        displayedPages: 10,
        currentPage : page,
        edges: 0	
        
    });
}


function clearDiv(div) {
//Clear div content
div.html('');
}

function printError(div) {
	div.append("Enter a keyword for search");
}

createDummyresults = function(contentHolder, paginator) {
	
	var data={
	took:10,
	numberOfResults:1004,
	results:new Array()
	}
	
	for (i=0; i<10;i++) {
		data.results[i] = { title: "Title" + i, tags: "Tag" + i, type: "dummy" } 	
	}	

	onssearch.data = data;
	
	buildResultList(data.results,onssearch.contentHolder);
	buildDummyPaginator(data.numberOfResults,onssearch.paginator);
	
}













