// Functionality to load up a collection page from collection.json


/*
 * Sets the window title
 */
function setTitle(title) {
	$( "title" ).html( title )
}

/*
 * Sets the h2
 */
function setH2(h2) {
	$( "h2" ).html( h2 )
}

/*
 * Sets the frequency
 */
function setFrequency(frequency) {
	$( "#frequency" ).html( frequency )
}

// Breadcrumb
var breadcrumb
var breadcrumbItem

// Introductory text
var summaryBlock
var lede
var moreLink
var more


/**
  * Deconstructs the page into chunks of markup template. 
  */
var deconstruct = function() {

	// Title
	setTitle("Loading..")

	// Summary
	summaryBlock = $(".content-reveal")
	more = $(".content-reveal__hidden", summaryBlock)
	more.detach()
	lede = $("p", summaryBlock)
	moreLink = $("a", summaryBlock)
	moreLink.detach()
	lede.text("Loading...")
	more.text("Loading...")
}


function setSummary(data) {

	lede.text(data.lede + " ")
	console.log(data.lede)
	if (data.more) {
		more.text(data.more)
		summaryBlock.append(more)
		lede.append(moreLink)
	}
}

function buildBreadcrumb(data, breadcrumbItem) {
    var breadcrumb = $(".breadcrumb")
    var breadcrumbHome = breadcrumbItem.clone()
    $("a", breadcrumbHome).text("Home").attr("href", "/")
    breadcrumb.append(breadcrumbHome)
    var breadcrumbLink = ""
    while (data.breadcrumb.length > 0) {
        var breadcrumbSegment = breadcrumbItem.clone()
        var crumb = data.breadcrumb.shift();
        breadcrumbLink += "/" + crumb.fileName
        $("a", breadcrumbSegment).text(crumb.name).attr("href", breadcrumbLink)
        breadcrumb.append(breadcrumbSegment)
    }
    // Add the current page at the end of the breadcrumb:
    var breadcrumbHere = breadcrumbItem.clone()
    breadcrumbLink += "/" + data.fileName
    $("a", breadcrumbHere).text(data.name).attr("href", breadcrumbLink)
    breadcrumb.append(breadcrumbHere);
}


/*
 * Main function to populate the page.
 */
$( document ).ready(function() {

	/* Deconstruct the template: */
	deconstruct()

	/* Get the collection.json file to populate the page: */

	$.get( "http://localhost:8080/collection.json", function( data ) {
		
		// Titles:
		setTitle(data.title)

		// H2:
		setH2(data.h2)
		
		// Frequency:
		setFrequency(data.frequency)		
		
		// Summary:
		setSummary(data)

		// Breadcrumb
		buildBreadcrumb(data, breadcrumbItem)

	});

});
