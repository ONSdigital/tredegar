var onssearch = {
search : function(url, keyword, containerDiv) {
		$.getJSON(url + "?q=" + keyword, function (data) {
 		 console.log(data);
			buildResultPage(data, containerDiv);			
 		 });
}
}

function buildResultPage(data, containerDiv) {
		
		var div =$("#" + containerDiv);
		
		if (data) {
			div.append("<p class=took> " +  data.numberOfResults + " results found in " + data.took + " milliseconds" );			
			buildResultList(data.results, div);
			buildPager(data.totalPages,div);				
		}			
}


function buildResultList(results, div) {

	div.append("<dl class='result_list'>");	
			
	for (i = 0; i < results.length; i++) { 
   	 	div.append("<dt class='title'>" + results[i].title + "</dt>");
			div.append("<dd class = 'tags'>" );
			div.append(results[i].tags);
			div.append("</dd>" );
	}	
	div.append("<dl>");	
	
}

function buildPager(numberOfPages, div) {
	for (i = 1; i <= numberOfPages; i++) { 
    div.append("<a class='page_link' href='#page=" + i + "'>"+ i +"</a> ")
	}
}














