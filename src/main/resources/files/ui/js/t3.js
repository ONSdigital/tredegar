// Functionality to load up a t2 page from data.json


/*
 * Sets the window title and h1 text.
 */
function setTitle(title) {
	$( "title" ).html( title )
	$( "h1" ).html( title )
}


/*
 * Builds an absolute link to the given filename.
 */
function link(filename) {

	// Get the current path:
	var result = window.location.pathname

	// This is to help when editing templates:
	if (result.indexOf(".") != -1)
		result = result.substring(0, result.lastIndexOf("/"))

	// Add a trailing slash if necessary:
	if (result.substring(result.length - 1) != "/") {
		result += "/"
	}

	// Append the filename:
	result += filename

	return result
}


/*
 * Builds an absolute link to the data.json file.
 */
function dataPath() {

	var dataPath =  link("data.json")

	console.log("Data at: "+dataPath)
	return dataPath
}

function populateDetail(detail, template) {

	var detailItem = template.clone()

	$("a", detailItem).text(detail.name)
	$(".stat__figure", detailItem).text(detail.number)
	$(".stat__figure__unit", detailItem).text(detail.unit)
	$(".stat__description", detailItem).text(detail.date)

	return detailItem
}

/*
 * Main function to populate the page.
 */
$( document ).ready(function() {

	/* Deconstruct the template: */

	// Title
	setTitle("Loading.. Cats")

	// Breadcrumb
	var breadcrumb = $(".breadcrumb")
	var breadcrumbItem = $("li:eq(0)", breadcrumb)
	breadcrumbItem.detach()
	$("li", breadcrumb).remove()

	// Section blocks
	var headline = $("#headline")
	var timeseries = $("#timeseries")
	
	// Section items
	// - detach one to use as a template and remove the rest:
	var timeseriesTemplate = $(".list--table__body:eq(0)", timeseries)
	timeseriesTemplate.detach()
	$(".list--table__body", timeseries).remove()

	/* Get the data.json file to populate the page: */

	$.get( dataPath(), function( data ) {

		// Titles:
		setTitle(data.name)

		// Lede and reveal:
		$("p", ".lede").text(data.lede);
		$(".content-reveal__hidden").text(data.more)

		// Time series items
		while (data.timeseries.length > 0) {
			console.log(JSON.stringify(data.timeseries[0]))
			var timeseriesItem = timeseriesTemplate.clone()
			var item = data.timeseries.shift()

			var header = $("h3", timeseriesItem)
			$("a:eq(0)", header).text(item.name).attr("href", item.link)
			$(".stat__figure", timeseriesItem).text(item.number)
			//TODO: Format and set the datetime property of date
			var updateDate = $("dl", timeseriesItem)
			$("dd:eq(0)", updateDate).text(item.lastUpated)
			$("dd:eq(1)", updateDate).text(item.nextUpdate)
			$(".stat__description", timeseriesItem).text(item.date)
			if(item.note) {
				$(".list--table__item__description", timeseriesItem).text(item.note)
			} else {
				$(".list--table__item__description", timeseriesItem).text("")
			}

			$(".list--table__head", timeseries).after(timeseriesItem)

		// "name": "Total population (UK) ",
	   //    "link": "#",
	   //    "info": "Lorem ipsum dolor sit amet",
	   //    "number": "64.1",
	   //    "unit": "m",
	   //    "date": "Mid-2013 estimate",
	   //    "lastUpated": "18th Fex 2014",
	   //    "nextUpdate": "25th June 2014",
	   //    "headline": true


		}
		//var item = timeseriesTemplate.clone()
		//$(".list--table__head", timeseries).after(item)

		// Breadcrumb
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

	});

});
