// Functionality to load up a Stats Bulletin page from bulletin.json


/*
 * Sets the window title and h1 text.
 */
function setTitle(title) {
	$( "title" ).html( title )
	$( "h1" ).html( title )
}


// Breadcrumb
var breadcrumb
var breadcrumbItem

// Introductory text
var summaryBlock
var lede
var moreLink
var more

// Table of contents
var toc
var tocItem

// Content sections
var sections
var section


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

	// Table of contents
	toc = $(".toc-columns")
    tocItem = $("<li><a></a></li>")
	//tocItem = $("ol", toc).first()
	//$("ol", toc).empty()

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


function setSummary(data) {

	lede.text(data.lede + " ")
	//console.log(data.lede)
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
	var content = $(".box__content", sectionItemTemplate)
	// console.log("Processing markdown:")
	// console.log(section.markdown)
	var html = markdown.toHTML(section.markdown)
	content.html(html)
	// Switch from <blockquote> to <div>
	$("blockquote", content).each(function( index, element) {
		var blockquote = $(element)
		var div = $("<div></div>").addClass("box--padded--highlight")
		blockquote.after(div)
		div.append($("p", blockquote))
		blockquote.remove()
	});
	// Stick in the temporary image
	$("pre", content).each(function( index, element ) {
		$(element).after("<p>"+
				'<img src="/ui/img/sample--chart.png" alt="sample chart">'+
			"</p>")
		$(element).remove()
	})

	// Attach
	sections.append(sectionItemTemplate)
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

	/* Get the data.json file to populate the page: */

	$.get( "http://localhost:9090/tredegar/src/main/resources/files/bulletin.json", function( data1 ) {

		// Titles:
		setTitle(data1.title)

		// Summary:
		setSummary(data1)

		// Sections
		var i=1
		while (data1.sections.length > 0) {
			var section = data1.sections.shift()
			addTocItem(section, i)
			addSection(section, i)
			i++
		}

<<<<<<< HEAD:src/main/resources/files/ui/js/article.js
		// // Lede and reveal:
		// $("p", ".lede").contents()[0].textContent = data1.lede;
		// $(".content-reveal__hidden").text(data1.more)

		// // Headline box
		// $(".lede", headline).text(data1.name + " Statistical Bulletin Headlines")

		// // Time series items
		// var i = 0;
		// while (data1.timeseries.length > 0 && i++ < 5) {

		// 	var timeseriesItem = timeseriesTemplate.clone()
		// 	var item = data1.timeseries.shift()

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
		// 		populateStatsBulletinHeadlines(data1, item)
		// 	}

		// }

		// // Breadcrumb
		// buildBreadcrumb(data1, breadcrumbItem)
=======
		// Breadcrumb
		buildBreadcrumb(data, breadcrumbItem)
>>>>>>> 81842efda93249991711a43b5c712a070b8f1683:src/main/resources/files/ui/js/bulletin.js

	});

});
