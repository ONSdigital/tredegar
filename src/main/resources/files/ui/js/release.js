// Functionality to load up a release page from release.json


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
	
    // Article section
    // Select the second one - the first is the TOC
    sectionTemplate = $("article:eq(1)")
    sections = sectionTemplate.parent()
    sectionTemplate.detach()
    sections.empty()	
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
 * Sets the release data
 */
function setReleaseDate(releaseDate) {
	$( "#releaseDate" ).html( releaseDate )
}

/*
 * Sets the next release data
 */
function setNextReleaseDate(nextReleaseDate) {
	$( "#nextReleaseDate" ).html( nextReleaseDate )
}

/*
 * Sets the contact details
 */
function setContact(data) {
	$( "#contactName" ).html( data.contact.name )
	$( "#contactOrganisation" ).html( data.contact.organisation)	
	$( "#contactEmail" ).html( data.contact.email )
	$( "#contactTelephone" ).html( data.contact.telephone )
}

function setBulletinsAndArticles(data) {
	var i = 1
	while (data.bulletinsAndArticles.length > 0) {
		var bulletinOrArticle = data.bulletinsAndArticles.shift()
		addBulletinOrArticle(bulletinOrArticle, i)
		i++
	}
}

function addBulletinOrArticle(section, index) {
    var sectionItemTemplate = sectionTemplate.clone()

    // Header anchor and text - adding an id, which is more html5:
    var header = $("h3", sectionItemTemplate)
    header.attr("id", "Section"+index)
    var anchor = $("a", header)
    anchor.detach()
    anchor.attr("name", "Section"+index)
    anchor.attr("href", section.url)
    anchor.text(section.title)
    header.prepend(anchor)

    // Section body:
    var content = $(".box__content", sectionItemTemplate)
    var html = markdown.toHTML(section.markdown)
    content.html(html)

    // Attach
    sections.append(sectionItemTemplate)
}

/*
 * Main function to populate the page.
 */
$( document ).ready(function() {

	/* Deconstruct the template: */
	deconstruct()

	/* Get the release.json file to populate the page: */
	$.get( "http://localhost:8080/release.json", function( data ) {
		// Titles:
		setTitle(data.title)

		// H2:
		setH2(data.h2)
		
		// Summary:
		setSummary(data)

		// Release date:
		setReleaseDate(data.releaseDate)		
		
		// Next Release date:
		setNextReleaseDate(data.nextReleaseDate)
		
		// Contact:
		setContact(data)
		
		// Bulletins and Articles:
		setBulletinsAndArticles(data)
		
		// Datasets:
//		setDatasets(data)		
		
		// Breadcrumb
		buildBreadcrumb(data, breadcrumbItem)

	});

});
