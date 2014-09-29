// Functionality to load up a t2 page from data.json



    $( document ).ready(function() {

      var dataPath = window.location.pathname
      if (dataPath.substring(dataPath.length - 1) != "/") {
        dataPath += "/"
      }
      dataPath += "data.json"
      console.log("Data at: "+dataPath)

      // Get the data.json file to populate the page:
      $.get( dataPath, function( data ) {

        /*
        * Builds a link to the given file using the given data.
        */
        var link = function (fileName) {
          var result = window.location.pathname
          if (result.substring(result.length - 1) != "/") {
            result += "/"
          }
          result += fileName
          return result
        }

        // Titles and headings:
        $( "title" ).html( data.name )
        $( "h1" ).html( data.name )

        // Sections:

        if (data.children.length > 0) {
          var first = data.children.shift()
          $("#firstTitle").text(first.name)
          $("#firstLink").text("View all " + first.name).attr("href", link(first.fileName))
        }
        
        if (data.children.length > 0) {
          var second = data.children.shift()
          $("#secondTitle").text(second.name)
          $("#secondLink").text("View all " + second.name).attr("href", link(second.fileName))
        }
        
        if (data.children.length > 0) {
          var third = data.children.shift()
          $("#thirdTitle").text(third.name)
          $("#thirdLink").text("View all " + third.name).attr("href", link(third.fileName))
        }

        // Clear the Other Categories list, but retain the header:
        // var otherList = $( "#otherList" );
        // var otherListHeader = $( "header", otherList )
        // otherListHeader.detach()
        // var otherListItem = $( ":first-child", otherList )
        // otherListItem.detach()
        // otherList.empty();
        // otherList.append(otherListHeader)
        // $("h2", otherListHeader).text('Other ' + data.name + ' categories')
        // otherList.append(otherListItem)

        // If there are no more items, show a "nowt" message:
        // if (data.children.length == 0) {
        //   $("#otherList").append('<footer class="nav-panel__footer">' +
        //                 '<div class="nav-panel__action">' +
        //                   '<a class="nav-panel__roomy" href="#">Lies, damned lies and missing statistics! There\'s nothing here to see.</a>' +
        //                 '</div>' + 
        //               '</footer>')
        // }

        // // Otherwise list the additional items:
        // while (data.children.length > 0) {
        //   var other = data.children.shift();

        //   // Build the markup template:
        //   var item = $('<footer class="nav-panel__footer">' +
        //                 '<div class="nav-panel__action">' +
        //                   '<a class="nav-panel__roomy" href="' + link(other.fileName) + '">' + other.name + '</a>' +
        //                 '</div>' + 
        //               '</footer>')

        //   // Append the item:
        //   $("#otherList").append(item)
        // }

        // Build the breadcrumb:
        var upLink = "../"
        while (data.breadcrumb.length > 0) {
          var crumb = data.breadcrumb.shift();
          $('#breadcrumb').append(document.createTextNode(" > "));
          $('#breadcrumb').append('<a href="' + upLink + '" class="action-link">' + crumb.name + '</a>');
          upLink += "../"
        }

        // Add the current page at the end of the breadcrumb:
        $('#breadcrumb').append(document.createTextNode(" > "));
        $('#breadcrumb').append(document.createTextNode(data.name));

      });

    });