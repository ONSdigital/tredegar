// Functionality to load up a t1 page from data.json


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
    console.log("Data for this page is at: " + dataPath)
    return dataPath
}


/*
 * Builds an absolute link to the data.json file.
 */
function dataChildPath(child) {
    var dataPath = link(child.fileName + "/?data")

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

// Breadcrumb
var breadcrumb
var breadcrumbItem

// Sections
var section = new Array();
var header = new Array();
var sectionItem = new Array();
var footer = new Array();


/**
 * Deconstructs the page into chunks of markup template.
 */
var deconstruct = function() {

    // Title
    setTitle("Home")

    // Breadcrumb
    breadcrumb = $(".breadcrumb")
    breadcrumbItem = $("li", breadcrumb).first()
    breadcrumbItem.detach()
    $("li", breadcrumb).remove()

    // Sections
    for (var i = 0; i < 4; i++) {

        // Section blocks
        section.push($(".nav-panel--stats:eq(" + i + ")"))

        // Section headers - set placeholders:
        header.push($("header", section[i]))
        $("h2", header[i]).text("Loading..")

        // Section items
        // - detach one to use as a template and remove the rest:
        sectionItem.push($("li", section[i]).first())
        sectionItem[i].detach()
        $("li", section[i]).remove()
        
        // Section footers
        // - detach these to use as a template:
        footer.push($("footer", section[i]))
        footer[i].detach()
    }
}

var populateTimeseries = function(uri, itemMarkup) {

            // $("a", itemMarkup).text(item.name)
            // $(".stat__figure", itemMarkup).text(item.number)
            // $(".stat__figure__unit", itemMarkup).text(item.unit)
            // $(".stat__description", itemMarkup).text(item.date)
            // $("ul", section).append(itemMarkup)
}

var populateChild = function(child, section, itemMarkupTemplate) {
    var count = 5
    while (child.timeseries.length > 0 && (count--)>0) {
        var timeseriesUri = child.timeseries.shift()   
        var itemMarkup = itemMarkupTemplate.clone()
        $("a", itemMarkup).text("Loading...: "+timeseriesUri)
        $(".stat__figure", itemMarkup).text("")
        $(".stat__figure__unit", itemMarkup).text("")
        $(".stat__description", itemMarkup).text("")
        $("ul", section).append(itemMarkup)
        populateTimeseries(timeseriesUri, itemMarkup)
    }
    // $.get(dataChildPath(child), function(data) {
    //     // Sections
    //     var i = 0;
    //     while (data.children.length > 0 && i++ < 4) {
    //         var item = data.children.shift()
    //         var itemMarkup = itemMarkupTemplate.clone()
    //         $("a", itemMarkup).text(item.name)
    //         $(".stat__figure", itemMarkup).text(item.number)
    //         $(".stat__figure__unit", itemMarkup).text(item.unit)
    //         $(".stat__description", itemMarkup).text(item.date)
    //         $("ul", section).append(itemMarkup)
    //     }
    // })
}


/*
 * Main function to populate the page.
 */
$(document).ready(function() {

    /* Deconstruct the template: */
    deconstruct();

    /* Get the data.json file to populate the page: */
    $.get(dataPath(), function(data) {

        // Titles:
        setTitle(data.name)

        // Headline stat
        // TODO

        for (var i = 0; i < 4; i++) {

            // Panels
            if (data.children.length > 0) {
                var item = data.children.shift()
                $("h2", header[i]).text(item.name)
                var detail = populateChild(item, section[i], sectionItem[i])
                $("a", footer[i]).text("View all " + item.name).attr("href", link(item.fileName))
                section[i].append(footer[i])
            }
        }

        // Breadcrumb
        buildBreadcrumb(breadcrumbItem)
    })
})