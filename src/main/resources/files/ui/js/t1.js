// Functionality to load up a t1 page from data.json


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

	var dataPath =  link("?data")

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

function buildBreadcrumb(breadcrumbItem) {

	var breadcrumb = $(".breadcrumb")
	
	var breadcrumbHome = breadcrumbItem.clone()
	$("a", breadcrumbHome).text("Home").attr("href", "/")
	breadcrumb.append(breadcrumbHome)
}

/*
 * Main function to populate the page.
 */
$( document ).ready(function() {

	/* Deconstruct the template: */

	// Title
	setTitle("Home")

	// Breadcrumb
	var breadcrumb = $(".breadcrumb")
	var breadcrumbItem = $("li:eq(0)", breadcrumb)
	breadcrumbItem.detach()
	$("li", breadcrumb).remove()

	var section = new Array();
	var header = new Array();
	var sectionItem = new Array();
	var footer = new Array();

	for (var i = 0; i < 4; i++) {

		// Section blocks
		section.push($(".nav-panel--stats:eq("+i+")"))

		// Section headers - set placeholders:
		header.push($("header", section[i]))
		$("h2", header[i]).text("Loading..")

		// Section items
		// - detach one to use as a template and remove the rest:
		sectionItem.push($("li:eq(0)", section[i]))
		sectionItem[i].detach()
		$("li", section[i]).remove()

		// Section footers
		// - detach these to use as a template:
		footer.push($("footer", section[i]))
		footer[i].detach()
	}

	/* Get the data.json file to populate the page: */

	$.get( dataPath(), function( data ) {

		// Titles:
		setTitle(data.name)

		// Lede and reveal:
		$("p", ".lede").text(data.lede);
		$(".content-reveal__hidden").text(data.more)

		for (var i = 0; i < 4; i++) {

			// Sections
			if (data.children.length > 0) {
				var item = data.children.shift()
				$("h2", header[i]).text(item.name)
				while (item.detail.length > 0) {
					var detailItem = populateDetail(item.detail.shift(), sectionItem[i])
					$("ul", section[i]).append(detailItem)
				}
				$("a", footer[i]).text("View all " + item.name).attr("href", link(item.fileName))
				section[i].append(footer[i])
			}
		}

		// Breadcrumb
		buildBreadcrumb(breadcrumbItem)

	});

});
