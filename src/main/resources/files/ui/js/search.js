var onssearch = {
keyword: null,	
div: null,
search : function(containerDiv) {
		keyword = this.keyword =   $.getUrlVar('q');
		div = this.div = $("#" + containerDiv);
		if (!keyword) {
			clearDiv(div);
			createDummyresults(div);
			return;
		}
		
		$.getJSON("search?q=" + keyword, function (data) {
			buildResultPage(data, div);			
 		 });
}
}

function buildResultPage(data, div) {		
		
		clearDiv(div);
		if (data) {
			console.log(data);
			div.append("<p class=took> " +  data.numberOfResults + " results found in " + data.took + " milliseconds" );			
			buildResultList(data.results, div);
			buildPager(data.totalPages,div);				
		}			
}

function clearDiv(div) {
//Clear div content
div.val('');
}


function buildResultList(results, div) {

	div.append("<dl class='result_list'>");	
	for (i = 0; i < results.length; i++) { 
   	 	div.append("<dt class='title'>" + results[i].title + "</dt>");
			div.append("<dd class='tags'>" + results[i].tags +  "</dd>" );
	}	
	div.append("</dl>");	
	
}

function buildPager(numberOfPages, div) {
	for (i = 1; i <= numberOfPages; i++) { 
    div.append("<a class='page_link' href='#page=" + i + "'>"+ i +"</a> ")
	}
}

function printError(div) {
	div.append("Enter a keyword for search");
}

function createDummyresults(div) {

	var data={
	took:10,
	numberOfResults:1004,
	totalPages:104,
	results:new Array()
	}
	
	for (i=0; i<10;i++) {
	data.results[i] = { title: "Title" + i, tags: "Tag" + i } 	
	}	
	
	buildResultPage(data,div);
	


}











