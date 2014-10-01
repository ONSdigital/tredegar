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
	setTitle("Loading..")

	// Breadcrumb
	var breadcrumb = $(".breadcrumb")
	var breadcrumbItem = $("li:eq(0)", breadcrumb)
	breadcrumbItem.detach()
	$("li", breadcrumb).remove()

	// Section blocks
	var section1 = $(".nav-panel--stats:eq(0)")
	var section2 = $(".nav-panel--stats:eq(1)")
	var section3 = $(".nav-panel--stats:eq(2)")
	var sectionOther = $(".nav-panel--stats:eq(3)")

	// Section headers - set placeholders:
	var header1 = $("header", section1)
	var header2 = $("header", section2)
	var header3 = $("header", section3)
	var headerOther = $("header", sectionOther)
	$("h2", header1).text("Loading..")
	$("h2", header2).text("Loading..")
	$("h2", header3).text("Loading..")
	$("h2", headerOther).text("Loading..")

	// Section items
	// - detach one to use as a template and remove the rest:
	var section1Item = $("li:eq(0)", section1)
	var section2Item = $("li:eq(0)", section2)
	var section3Item = $("li:eq(0)", section3)
	var sectionOtherItem = $("footer:eq(0)", sectionOther)
	section1Item.detach()
	section2Item.detach()
	section3Item.detach()
	sectionOtherItem.detach()
	$("li", section1).remove()
	$("li", section2).remove()
	$("li", section3).remove()
	$("footer", sectionOther).remove()

	// Section footers
	// - detach these to use as a template:
	var footer1 = $("footer", section1)
	var footer2 = $("footer", section2)
	var footer3 = $("footer", section3)
	footer1.detach()
	footer2.detach()
	footer3.detach()

	/* Get the data.json file to populate the page: */

	$.get( dataPath(), function( data ) {

		// Titles:
		setTitle(data.name)

		// Lede and reveal:
		$("p", ".lede").text(data.lede);
		$(".content-reveal__hidden").text(data.more)

		// Section 1
		if (data.children.length > 0) {
			var item = data.children.shift()
			$("h2", header1).text(item.name)
			while (item.detail.length > 0) {
				var detailItem = populateDetail(item.detail.shift(), section1Item)
				$("ul", section1).append(detailItem)
			}
			$("a", footer1).text("View all " + item.name).attr("href", link(item.fileName))
			section1.append(footer1)
		}

		// Section 2
		if (data.children.length > 0) {
			var item = data.children.shift()
			$("h2", header2).text(item.name)
			while (item.detail.length > 0) {
				var detailItem = populateDetail(item.detail.shift(), section2Item)
				$("ul", section2).append(detailItem)
			}
			$("a", footer2).text("View all " + item.name).attr("href", link(item.fileName))
			section2.append(footer2)
		} else {
			section2.remove()
		}

		// Section 3
		if (data.children.length > 0) {
			var item = data.children.shift()
			$("h2", header3).text(item.name)
			while (item.detail.length > 0) {
				var detailItem = populateDetail(item.detail.shift(), section3Item)
				$("ul", section3).append(detailItem)
			}
			$("a", footer3).text("View all " + item.name).attr("href", link(item.fileName))
			section3.append(footer3)
		} else {
			section3.remove()
		}

		// "Other..." Section
		if (data.children.length > 0) {

			$("h2", headerOther).text('Other ' + data.name + ' categories')
			while (data.children.length > 0) {
				var item = data.children.shift()
				var other = sectionOtherItem.clone()
				$("a", other).text(item.name).attr("href", link(item.fileName))
				sectionOther.append(other)
			}
		} else {
			sectionOther.remove()
		}

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
