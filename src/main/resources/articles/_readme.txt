

 * How to create an article for the Alpha website


Copy one of the existing files in this folder and edit it.


 - File format
 
The general format is a "header" block, followed by a blank line, followed by markup for the content.


 - Headers

The header values you can use are:

Theme  : Economy
Level 2: Government, Public Sector and Taxes
Level 3: Public Sector Finance
Lede: Lorem ipsum dolor sit amet.
More: Sit autem paulo necessitatibus an, eu case
Summary: Displayed in the boxes on other pages leading to the article
Contact name: Displayed on article page
Contact email: Displayed on article page
Next release: Displayed on article page

The block of header values must be followed by at least one blank line. After than, the rest of the document is processed as markdown.


 - Main title
 
Use an H1 line to set the article title, e.g.:

# Expert analysis of Consumer Price Indices


 - Content sections
 
Then use H2 lines to delimit each section, e.g.:

## Introduction

Anything following an H2 tag will be added to the Json as raw markdown and processed when the page renders.


 - Footer "accordion" sections
 
Use an H2 line, prefixed with [accordion] to add an accordion section, e.g.:

## [accordion] Background Notes

These sections will be processed in the same way as normal sections, but will appear as "accordions" at the end of the page.


 - Publishing
 
Check your changes in to Github and pretty soon they will be processed by someone's build and made available on Tredegar.


---

David Carboni
