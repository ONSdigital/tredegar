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
    console.log("Data for this page is at: " + dataPath)
    return dataPath
}
/*
 * Builds an absolute link to the data.json file.
 */
function dataChildPath(child) {
    var dataPath = link(child.fileName + "/?data")
    console.log("Data for " + child.name + " is at: " + dataPath)
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
// Breadcrumb
var breadcrumb
var breadcrumbItem
// Main sections
var section = new Array()
var header = new Array()
var sectionItem = new Array()
var footer = new Array()
// "Other" section
var headerOther
var sectionOther
var sectionOtherItem
var footerOther
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
    for (var i = 0; i < 3; i++) {
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
    // "Other" section:
    headerOther = $("header", sectionOther)
    $("h2", headerOther).text("Loading..")
    sectionOther = $(".nav-panel--stats:eq(3)")
    sectionOtherItem = $("footer", sectionOther).first()
    sectionOtherItem.detach()
    $("footer", sectionOther).remove()
}
var populateChild = function(child, section, itemMarkupTemplate) {
    $.get(dataChildPath(child), function(data) {
        // Select the right child array:
        var children
        if (data.level == "t2") children = data.children
        else if (data.level == "t3") children = data.timeseries
        else children = new Array();
        // Sections
        var i = 0;
        while (children.length > 0 && i++ < 4) {
            var item = children.shift()
            var itemMarkup = itemMarkupTemplate.clone()
            $("a", itemMarkup).text(item.name)
            $(".stat__figure", itemMarkup).text(item.number)
            $(".stat__figure__unit", itemMarkup).text(item.unit)
            $(".stat__description", itemMarkup).text(item.date)
            $("ul", section).append(itemMarkup)
        }
    })
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
        // Lede and reveal:
        $("p", ".lede").contents()[0].textContent = data.lede;
        $(".content-reveal__hidden").text(data.more)
        // Select the right child array:
        var children
        if (data.level == "t2") children = data.children
        else if (data.level == "t3") children = data.timeseries
        else children = new Array();
        // Main sections
        for (var i = 0; i < 3; i++) {
            if (children.length > 0) {
                var item = children.shift()
                $("h2", header[i]).text(item.name)
                populateChild(item, section[i], sectionItem[i])
                // while (item.detail.length > 0) {
                // 	var detailItem = populateDetail(item.detail.shift(), sectionItem[i])
                // 	$("ul", section[i]).append(detailItem)
                // }
                $("a", footer[i]).text("View all " + item.name).attr("href", link(item.fileName))
                section[i].append(footer[i])
            } else {
                section[i].remove()
            }
        }
        // "Other..." Section
        if (children.length > 0) {
            $("h2", headerOther).text('Other ' + data.name + ' categories')
            while (children.length > 0) {
                var item = children.shift()
                var other = sectionOtherItem.clone()
                $("a", other).text(item.name).attr("href", link(item.fileName))
                sectionOther.append(other)
            }
        } else {
            sectionOther.remove()
        }
        // Breadcrumb
        buildBreadcrumb(data, breadcrumbItem)
    });
});