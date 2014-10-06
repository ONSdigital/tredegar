// Functionality to load up a Stats Bulletin page from bulletin.json


/*
 * Sets the window title and h1 text.
 */
function setTitle(title) {
	$( "title" ).html( title )
	$( "h1" ).html( title )
}

var summaryBlock
var lede
var moreLink
var more
var toc
var tocItem
var sections
var section

/**
  * Deconstructs the page into chunks of markup template. 
  */
var deconstruct = function() {

	// Title
	setTitle("Loading..")

	// Exec summary
	summaryBlock = $(".content-reveal")
	more = $(".content-reveal__hidden", summaryBlock)
	more.detach()
	lede = $("p", summaryBlock)
	moreLink = $("a", summaryBlock)
	moreLink.detach()
	lede.text("Loading...")
	more.text("Loading...")

	// Table of contents
	toc = $(".toc-columns")
	tocItem = $("li", toc).first()
	$("ol", toc).empty()

	// Article section
	// Select the second one - the first is the TOC
	sectionTemplate = $("article:eq(1)")
	sections = sectionTemplate.parent()
	sectionTemplate.detach()
	sections.empty()


	// // Breadcrumb
	// breadcrumb = $(".breadcrumb")
	// breadcrumbItem = $("li:eq(0)", breadcrumb)
	// breadcrumbItem.detach()
	// $("li", breadcrumb).remove()

	// // Section blocks
	// timeseries = $("#timeseries")
	
	// // Section items
	// // - detach one to use as a template and remove the rest:
	// timeseriesTemplate = $(".list--table__body:eq(0)", timeseries)
	// timeseriesTemplate.detach()
	// $(".list--table__body", timeseries).remove()
}


function setExecSummary(data) {

	lede.text(data.lede + " ")
	console.log(data.lede)
	if (data.more) {
		more.text(data.more)
		summaryBlock.append(more)
		lede.append(moreLink)
	}
}

function addTocItem(section, index) {
	var tocItemTemplate = tocItem.clone()
	$("a", tocItemTemplate).text(section.title).attr("href", "#Section"+index)
	toc.append(tocItemTemplate)
}

function addSection(section, index) {
	var sectionItemTemplate = sectionTemplate.clone()

	// Header anchor and text - adding an id, which is more html5:
	var header = $("h2", sectionItemTemplate)
	header.attr("id", "Section"+index)
	var anchor = $("a", header)
	anchor.detach()
	anchor.attr("name", "Section"+index)
	header.text(index+". "+section.title)
	header.prepend(anchor)

	// Section body:
	var html = markdown.toHTML(section.markdown)
	$(".box__content", sectionItemTemplate).html(html)

	// Attach
	sections.append(sectionItemTemplate)
}


/*
 * Main function to populate the page.
 */
$( document ).ready(function() {

	/* Deconstruct the template: */
	deconstruct()

	/* Get the data.json file to populate the page: */

	$.get( "http://localhost:8080/home/economy/inflationandpriceindices/bulletins/bulletin.json", function( data ) {

		// Titles:
		setTitle(data.title)

		// Exec summary:
		setExecSummary(data)

		// Sections
		var i=1
		while (data.sections.length > 0) {
			var section = data.sections.shift()
			addTocItem(section, i)
			addSection(section, i)
			i++
		}

		// // Lede and reveal:
		// $("p", ".lede").contents()[0].textContent = data.lede;
		// $(".content-reveal__hidden").text(data.more)

		// // Headline box
		// $(".lede", headline).text(data.name + " Statistical Bulletin Headlines")

		// // Time series items
		// var i = 0;
		// while (data.timeseries.length > 0 && i++ < 5) {
			
		// 	var timeseriesItem = timeseriesTemplate.clone()
		// 	var item = data.timeseries.shift()

		// 	var header = $("h3", timeseriesItem)
		// 	$("a:eq(0)", header).text(item.name).attr("href", item.link)
		// 	$(".stat__figure", timeseriesItem).text(item.number+item.unit)
		// 	//TODO: Format and set the datetime property of date
		// 	var updateDate = $("dl", timeseriesItem)
		// 	$("dd:eq(0)", updateDate).text(item.lastUpated)
		// 	$("dd:eq(1)", updateDate).text(item.nextUpdate)
		// 	$(".stat__description", timeseriesItem).text(item.date)
		// 	if(item.note) {
		// 		$(".list--table__item__description", timeseriesItem).text(item.note)
		// 	} else {
		// 		$(".list--table__item__description", timeseriesItem).text("")
		// 	}

		// 	$(".list--table__head", timeseries).after(timeseriesItem)

		// 	if(item.headline) {
		// 		populateHeadline(item);
		// 		populateStatsBulletinHeadlines(data, item)
		// 	}

		// }

		// // Breadcrumb
		// buildBreadcrumb(data, breadcrumbItem)

	});

});
