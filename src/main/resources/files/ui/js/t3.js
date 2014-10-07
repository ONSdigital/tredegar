// Functionality to load up a t2 page from data.json


/*
 * Sets the window title and h1 text.
 */
function setTitle(title) {
    $("title").html(title)
    $("h1").html(title)
}


/*
 * Builds an absolute link to the given filename.
 */
function link(filename) {
    // Get the current path:
    var result = window.location.pathname
    // This is to help when editing templates:
    if (result.indexOf(".") != -1) result = result.substring(0, result.lastIndexOf("/"))
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
    var dataPath = link("?data")
    console.log("Data at: " + dataPath)
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


function populateHeadline(headlineItem) {
    var headline = $("#headline")
    //Headline Title
    $("h2", headline).contents()[0].textContent = headlineItem.name
    var statFigure = $(".stat__figure", headline)
    //Number
    $("strong", statFigure).text(headlineItem.number + headlineItem.unit)
    //Date and change in number
    var statDescription = $(".stat__description", headline)
    //Last update and next update
    var updateDate = $("dl", statDescription)
    $("dd:eq(0)", updateDate).text(headlineItem.lastUpated)
    $("span", $("dd:eq(1)", updateDate)).text(headlineItem.nextUpdate)
    //Explanation
    $(".stat_change_term", headline).text(headlineItem.explanation)
}


function populateStatsBulletinHeadlines(data, timeseriesItem) {
    var headlines = $("#statsBulletinHeadlines")
    $(".lede", headlines).text(data.name + " Statistical Bulletin Headlines")
    var headlineList = $("ul", headlines)
    var itemTemplate = $("li", headlineList).first()
    itemTemplate.detach()
    $("li", headlineList).remove()
    while (timeseriesItem.statsBulletinHeadlines.length > 0) {
        var headlineData = timeseriesItem.statsBulletinHeadlines.shift()
        var headlineItem = itemTemplate.clone()
        // For now it's not actually a link, so we just populate the text.
        // TODO: eventually use headlineData.href too.
        headlineItem.text(headlineData.text)
        headlineList.append(headlineItem)
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


//Breadcrumb
var breadcrumb
var breadcrumbItem

//Introductory text
var summaryBlock
var lede
var moreLink
var more

// Timeseries
var timeseries
var timeseriesTemplate


/**
 * Deconstructs the page into chunks of markup template.
 */
var deconstruct = function() {
	
    // Title
    setTitle("Loading..")
    
    // Breadcrumb
    breadcrumb = $(".breadcrumb")
    breadcrumbItem = $("li", breadcrumb).first()
    breadcrumbItem.detach()
    $("li", breadcrumb).remove()

    // Summary
    summaryBlock = $(".content-reveal")
    more = $(".content-reveal__hidden", summaryBlock)
    more.detach()
    lede = $("p", summaryBlock)
    moreLink = $("a", summaryBlock)
    moreLink.detach()
    lede.text("Loading...")
    more.text("Loading...")
    
    // Timeseries blocks
    timeseries = $("#timeseries")
    
    // Section items
    // - detach one to use as a template and remove the rest:
    timeseriesTemplate = $(".list--table__body", timeseries).first()
    timeseriesTemplate.detach()
    $(".list--table__body", timeseries).remove()
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


/*
 * Main function to populate the page.
 */
$(document).ready(function() {
	
    /* Deconstruct the template: */
    deconstruct()
    
    /* Get the data.json file to populate the page: */
    $.get(dataPath(), function(data) {
    	
        // Titles:
        setTitle(data.name)

        // Summary:
        setSummary(data)
        
        // Headline box
        $(".lede", headline).text(data.name + " Statistical Bulletin Headlines")
        
        // Time series items
        var i = 0;
        while (data.timeseries.length > 0 && i++ < 5) {
            var timeseriesItem = timeseriesTemplate.clone()
            var item = data.timeseries.shift()
            var header = $("h3", timeseriesItem)
            $("a", header).first().text(item.name).attr("href", item.link)
            $(".stat__figure", timeseriesItem).text(item.number + item.unit)
            //TODO: Format and set the datetime property of date
            var updateDate = $("dl", timeseriesItem)
            $("dd:eq(0)", updateDate).text(item.lastUpated)
            $("dd:eq(1)", updateDate).text(item.nextUpdate)
            $(".stat__description", timeseriesItem).text(item.date)
            if (item.note) {
                $(".list--table__item__description", timeseriesItem).text(item.note)
            } else {
                $(".list--table__item__description", timeseriesItem).text("")
            }
            $(".list--table__head", timeseries).after(timeseriesItem)
            if (item.headline) {
                populateHeadline(item);
                populateStatsBulletinHeadlines(data, item)
            }
        }
        
        // Breadcrumb
        buildBreadcrumb(data, breadcrumbItem)
    });
});